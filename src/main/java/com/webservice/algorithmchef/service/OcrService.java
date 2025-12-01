package com.webservice.algorithmchef.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.ProcessorName;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.protobuf.ByteString;
import com.webservice.algorithmchef.dto.ocr.OcrResult;
import com.webservice.algorithmchef.dto.ocr.ReceiptAnalysisResponse;
import com.webservice.algorithmchef.model.Ingredient;
import com.webservice.algorithmchef.repository.IngredientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    @Value("${gcp.project.id}")
    private String projectId;
    @Value("${gcp.processor.location}")
    private String location;
    @Value("${gcp.processor.id}")
    private String processorId;

    private final IngredientRepository ingredientRepository;

    private static final List<String> FILTER_KEYWORDS = Arrays.asList(
            "부가세", "면세", "과세", "합계", "총액", "금액", "승인", "매니저",
            "카드", "신용", "결제", "TEL", "POS", "대표", "사업자", "주소", 
            "매장", "영수증", "주문", "번호", "날짜", "단가", "수량", 
            "할인", "에누리", "봉투", "배송", "반품", "포인트", "현금"
    );

    public ReceiptAnalysisResponse processAndParseReceipt(MultipartFile file) throws IOException, ApiException {
        try (DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create()) {
            ProcessorName processorName = ProcessorName.of(projectId, location, processorId);
            ByteString content = ByteString.copyFrom(file.getBytes());
            String mimeType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

            RawDocument rawDocument = RawDocument.newBuilder()
                    .setContent(content)
                    .setMimeType(mimeType)
                    .build();

            ProcessRequest request = ProcessRequest.newBuilder()
                    .setName(processorName.toString())
                    .setRawDocument(rawDocument)
                    .build();

            ProcessResponse response = client.processDocument(request);
            Document document = response.getDocument();

            LocalDateTime purchaseDateTime = LocalDateTime.now();
            for (Document.Entity entity : document.getEntitiesList()) {
                if ("receipt_date".equals(entity.getType())) {
                    try {
                        String dateText = entity.getNormalizedValue() != null 
                                ? entity.getNormalizedValue().getText() 
                                : entity.getMentionText();
                        dateText = dateText.trim().replace(".", "-").replace("/", "-");
                        if(dateText.length() > 10) dateText = dateText.substring(0, 10);
                        
                        LocalDate datePart = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        purchaseDateTime = datePart.atStartOfDay();
                    } catch (Exception e) {
                        log.warn("날짜 파싱 실패: {}", e.getMessage());
                    }
                    break;
                }
            }

            List<OcrResult> items = parseToDtoList(document, purchaseDateTime);

            return new ReceiptAnalysisResponse(purchaseDateTime, items);
        }
    }

    private List<OcrResult> parseToDtoList(Document document, LocalDateTime purchaseDateTime) {
        List<OcrResult> resultList = new ArrayList<>();
        int itemIdCounter = 1;

        if (document.getEntitiesList() != null) {
            for (Document.Entity entity : document.getEntitiesList()) {
                if ("line_item".equals(entity.getType())) {
                     String itemDescription = null;
                    int itemQuantity = 1;

                    for (Document.Entity property : entity.getPropertiesList()) {
                        String type = property.getType();
                        String text = property.getMentionText();

                        if ("line_item/description".equals(type)) {
                            itemDescription = text.replaceAll("^\\d+\\*?\\s*", "").trim();
                        } else if ("line_item/quantity".equals(type)) {
                            try {
                                itemQuantity = (int) Math.round(Double.parseDouble(text));
                            } catch (NumberFormatException ignored) { itemQuantity = 1; }
                        }
                    }
                    
                    if (isValidItem(itemDescription)) {
                        resultList.add(createOcrResult(itemIdCounter++, itemDescription, itemQuantity, purchaseDateTime));
                    }
                }
            }
        }

        if (resultList.isEmpty()) {
            log.info("AI Entity 추출 실패 -> 텍스트 라인 파싱 시도");
            String fullText = document.getText();
            String[] lines = fullText.split("\n");

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.contains("-")) continue;
                
                if (!isValidItem(line)) continue;

                if (line.matches(".*\\d+(,\\d{3})*$")) {
                    
                    String tempLine = line.replaceAll("^\\d+\\*?\\s*", "");

                    String prevLine = tempLine;
                    while (true) {
                        String cleaned = tempLine.replaceAll("[\\d,]+$", "").trim();
                        if (cleaned.equals(tempLine) || cleaned.isEmpty()) break;
                        tempLine = cleaned;
                    }

                    String finalName = tempLine.trim();

                    if (finalName.length() > 1 && isValidItem(finalName)) {
                         boolean exists = false;
                         for(OcrResult res : resultList) {
                             if(res.description().equals(finalName)) { exists = true; break; }
                         }
                         
                         if(!exists) {
                             resultList.add(createOcrResult(itemIdCounter++, finalName, 1, purchaseDateTime));
                         }
                    }
                }
            }
        }

        return resultList;
    }

    private boolean isValidItem(String text) {
        if (text == null || text.isEmpty()) return false;
        if (text.startsWith("[") || text.startsWith("(") || text.startsWith("*")) return false;
        
        for (String keyword : FILTER_KEYWORDS) {
            if (text.contains(keyword)) return false;
        }
        return true;
    }

    private OcrResult createOcrResult(int id, String description, int quantity, LocalDateTime purchaseDateTime) {
        String candidateName = null;
        LocalDateTime expirationDate = null;

        Ingredient matched = findIngredientInDb(description);
        if (matched != null) {
            candidateName = matched.getName();
            if (matched.getAvgExpiryDays() > 0) {
                expirationDate = purchaseDateTime.plusDays(matched.getAvgExpiryDays());
            }
        }

        return new OcrResult(id, description, candidateName, expirationDate, quantity);
    }

    private Ingredient findIngredientInDb(String itemDescription) {
        Optional<Ingredient> exactMatch = ingredientRepository.findByName(itemDescription);
        if (exactMatch.isPresent()) return exactMatch.get();

        String[] words = itemDescription.split(" ");
        for (String word : words) {
            if (word.length() < 2) continue;
            List<Ingredient> candidates = ingredientRepository.findByNameContaining(word);
            if (!candidates.isEmpty()) return candidates.get(0);
        }
        return null;
    }
}
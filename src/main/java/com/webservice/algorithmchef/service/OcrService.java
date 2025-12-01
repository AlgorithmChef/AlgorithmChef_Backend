package com.webservice.algorithmchef.service;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.documentai.v1.*;
import com.google.protobuf.ByteString;
import com.webservice.algorithmchef.dto.ocr.OcrResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OcrService {

    @Value("${gcp.project.id}")
    private String projectId;
    @Value("${gcp.processor.location}")
    private String location;
    @Value("${gcp.processor.id}")
    private String processorId;

    // document ai가 출력 시에 제외할 필터링 키워드
    private static final List<String> FILTER_KEYWORDS = Arrays.asList(
            "부가세", "면세", "면세물품", "카드", "신용", "승인", "매니저",
            "합계", "총액", "금액", "승인번호", "과세", "포인트", "할인",
            "공급가액", "받을금액", "과세물품", "결제", "TEL", "POS", "대표", "사업자"
    );


    public List<OcrResult> processAndParseReceipt(MultipartFile file) throws IOException, ApiException {

        // 1. Document AI 호출
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

            // 2. 응답 파싱 (DTO 리스트로 변환)
            return parseToDtoList(response.getDocument());
        }
    }

    // dto 리스트 생성
    private List<OcrResult> parseToDtoList(Document document) {
        List<OcrResult> resultList = new ArrayList<>();
        int itemIdCounter = 1;

        if (document == null || document.getEntitiesList().isEmpty()) {
            return resultList;
        }

        for (Document.Entity entity : document.getEntitiesList()) {
            // 'line_item' 타입만 처리
            if ("line_item".equals(entity.getType())) {

                String itemDescription = null;
                int itemQuantity = 1;

                // line_item 내부 순회
                for (Document.Entity property : entity.getPropertiesList()) {
                    String type = property.getType();
                    String text = property.getMentionText();

                    // document ai api가 멋대로 끼워넣는 줄바꿈 문자 삭제, 실수로 읽히는 바코드넘버 삭제, 불필요한 앞뒤 공백 삭제
                    if ("line_item/description".equals(type)) {
                        String cleaned = text.replace("\n", " ").replaceAll("\\d{8,}", "").trim();
                        itemDescription = cleaned;

                      // 문자형로 인식되는 숫자를 정수형으로 변환
                    } else if ("line_item/quantity".equals(type)) {
                        try {
                            itemQuantity = (int) Math.round(Double.parseDouble(text));
                        } catch (NumberFormatException ignored) {
                            itemQuantity = 1;
                        }
                    }
                }


                // 1. 이름 없으면 패스
                if (itemDescription == null || itemDescription.isEmpty()) {
                    continue;
                }

                // 2. 필터링 키워드 체크
                boolean isFiltered = false;
                for (String keyword : FILTER_KEYWORDS) {
                    if (itemDescription.contains(keyword)) {
                        isFiltered = true;
                        break;
                    }
                }

                // 3. DTO 생성 후 리스트 추가
                if (!isFiltered) {
                    resultList.add(new OcrResult(itemIdCounter++, itemDescription, itemQuantity));
                }
            }
        }
        return resultList;
    }
}

package com.webservice.algorithmchef.controller;

import com.webservice.algorithmchef.dto.ocr.ReceiptAnalysisResponse;
import com.webservice.algorithmchef.service.OcrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/ocr/request")
public class OcrController {

    private final OcrService ocrService;

    @Autowired
    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    // 최종 ocr 요청 주소: /ocr/request/upload
    // React에서 FormData 변수명: imageFile
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndProcessReceipt(
            @RequestParam("imageFile") MultipartFile imageFile) {

        // 파일 유효성 검사
        if (imageFile.isEmpty()) {
            log.warn("업로드 실패: 빈 파일이 전송됨");
            return ResponseEntity.badRequest().body("업로드된 파일이 비어있습니다.");
        }

        try {
            log.info("OCR 요청 수신 - 파일명: {}, 크기: {} bytes",
                    imageFile.getOriginalFilename(), imageFile.getSize());

            // 서비스 호출 (Wrapper DTO 반환)
            ReceiptAnalysisResponse result = ocrService.processAndParseReceipt(imageFile);

            // result.items()로 리스트에 접근하여 사이즈 로그 출력
            log.info("OCR 처리 완료 - 식별된 항목 수: {}개", result.items().size());

            // DTO -> JSON 자동 변환되어 반환
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // 스택트레이스를 로그 파일에 남기기 위해 e 객체를 함께 전달
            log.error("OCR 문서 처리 중 치명적인 오류 발생", e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("문서 처리 중 서버 오류 발생: " + e.getMessage());
        }
    }
}
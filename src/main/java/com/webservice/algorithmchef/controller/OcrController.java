package com.webservice.algorithmchef.controller;

import com.webservice.algorithmchef.dto.ocr.OcrResult;
import com.webservice.algorithmchef.service.OcrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ocr/request")
public class OcrController {

    private final OcrService ocrService;

    @Autowired
    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }
    // 최종 ocr 요청 주소는 /ocr/request/upload가 되어야함
    // react에서 FormData 변수명은 imageFile이 되어야함(axios 사용)
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

            // dto 객체 반환
            List<OcrResult> result = ocrService.processAndParseReceipt(imageFile);

            log.info("OCR 처리 완료 - 식별된 항목 수: {}개", result.size());

            // dto -> json 자동 변환
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

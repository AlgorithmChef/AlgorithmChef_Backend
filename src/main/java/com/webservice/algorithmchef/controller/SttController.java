package com.webservice.algorithmchef.controller;

import com.webservice.algorithmchef.dto.gemini.GeminiRecipeResponse;
import com.webservice.algorithmchef.service.GeminiRecipeService;
import com.webservice.algorithmchef.service.SpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class SttController {

    private final SpeechService speechService;
    private final GeminiRecipeService geminiRecipeService; // 레시피 서비스 객체

    @PostMapping("/stt")
    public ResponseEntity<?> stt(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("userId") String userIdStr
    ) {
        log.info("음성 기반 레시피 추천 요청 - UserId: {}, 파일크기: {} bytes", userIdStr, audioFile.getSize());

        try {
            // 1. 음성 -> 텍스트 변환 (STT)
            String convertedText = speechService.stt(audioFile);

            if (convertedText == null || convertedText.isEmpty()) {
                log.warn("STT 변환 실패: 음성 인식 결과 없음");
                return ResponseEntity.badRequest().body("음성을 인식하지 못했습니다. 다시 말씀해 주세요.");
            }

            log.info("STT 변환 결과: \"{}\"", convertedText);

            // userId String -> Long 변환
            Long userId = Long.parseLong(userIdStr);

            // 음성 검색은 보통 '새로운 검색'이므로 excludedTitles는 빈 리스트로 전달
            List<String> excludedTitles = Collections.emptyList();

            // recipeservice 호출
            List<GeminiRecipeResponse> recipes = geminiRecipeService.recommendCondition(
                    userId,
                    convertedText,
                    excludedTitles
            );

            log.info("음성 추천 완료 - {}개의 레시피 반환", recipes.size());

            // 3. 레시피 리스트 반환
            return ResponseEntity.ok(recipes);

        } catch (NumberFormatException e) {
            log.error("UserId 형식 오류: {}", userIdStr, e);
            return ResponseEntity.badRequest().body("유효하지 않은 사용자 ID입니다.");
        } catch (Exception e) {
            log.error("음성 레시피 추천 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("처리 중 서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

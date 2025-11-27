package com.webservice.algorithmchef.controller;

import com.webservice.algorithmchef.dto.gemini.GeminiRecipeRequest.*;
import com.webservice.algorithmchef.dto.gemini.GeminiRecipeResponse;
import com.webservice.algorithmchef.service.GeminiRecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recipes/recommend")
@RequiredArgsConstructor
public class GeminiRecipeController {

    private final GeminiRecipeService recipeService;

    // 1. 유저 성향 및 식습관 기반 추천 (요청주소 /recipes/recommend/prefer)
        @PostMapping("/prefer")
        public ResponseEntity<List<GeminiRecipeResponse>> recommendPrefer(@RequestBody PreferRequest request) {
            log.info("성향 추천 요청 - userId: {}", request.userId());
            return ResponseEntity.ok(
                    recipeService.recommendPrefer(request.userId(), request.excludedTitles())
            );
        }

    // 2. speech to text 조건 기반 추천 (요청주소 /recipes/recommend/condition)
    @PostMapping("/condition")
    public ResponseEntity<List<GeminiRecipeResponse>> recommendCondition(@RequestBody ConditionRequest request) {
        log.info("조건 추천 요청 - userId: {}, Condition: {}", request.userId(), request.condition());
        return ResponseEntity.ok(
                recipeService.recommendCondition(request.userId(), request.condition(), request.excludedTitles())
        );
    }

    // 3. 임박재료 기반 추천 (요청주소 /recipes/recommend/expir)
    @PostMapping("/expir")
    public ResponseEntity<List<GeminiRecipeResponse>> recommendExpir(@RequestBody ExpirRequest request) {
        log.info("재료 추천 요청 - userId: {}", request.userId());
        return ResponseEntity.ok(
                recipeService.recommendExpir(request.userId(), request.ingredients(), request.excludedTitles())
        );
    }
}

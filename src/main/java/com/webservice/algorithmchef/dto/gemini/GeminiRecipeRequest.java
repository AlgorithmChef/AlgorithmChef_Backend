package com.webservice.algorithmchef.dto.gemini;

import java.util.List;

public class GeminiRecipeRequest {

    // 1. 성향 기반 추천 요청
    public record PreferRequest(
            Long userPkId, // DB의 User PK (id)
            List<String> excludedTitles
    ) {}

    // 2. Speech-to-Text 조건 기반 추천 요청
    public record ConditionRequest(
            Long userPkId,
            List<String> excludedTitles,
            String condition // 기분, 상황
    ) {}

    // 3. 임박한 재료 추천 요청
    public record ExpirRequest(
            Long userPkId,
            List<String> excludedTitles,
            List<String> ingredients // 냉장고 재료
    ) {}
}

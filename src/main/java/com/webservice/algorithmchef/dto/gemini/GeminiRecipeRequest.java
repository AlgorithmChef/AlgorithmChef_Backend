package com.webservice.algorithmchef.dto.gemini;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeminiRecipeRequest {

    // 1. 성향 기반 추천 요청
    public record PreferRequest(
            String userId, // DB의 User (id)
            List<String> excludedTitles
            // client의 첫 요청시 string 리스트는 비어있음.
            // 재요청 시 front에서 요리명만 해당 리스트에 담아 전송. 그러면 gemini는 해당 레시피는 제외하여 탐색.
    ) {}

    // 2. Speech-to-Text 조건 기반 추천 요청
    public record ConditionRequest(
            String userId,
            List<String> excludedTitles,
            String condition // 기분, 상황 등 조건
    ) {}

    // 3. 임박한 재료 추천 요청
    public record ExpirRequest(
    		@JsonProperty("userId")
            String userId,
            @JsonProperty("excludedTitles")
            List<String> excludedTitles,
            @JsonProperty("ingredients")
            List<String> ingredients // 소비기한 임박 재료
    ) {}
}

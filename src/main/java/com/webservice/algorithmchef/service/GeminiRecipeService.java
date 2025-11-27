package com.webservice.algorithmchef.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.*;
import com.webservice.algorithmchef.dto.gemini.GeminiRecipeResponse;
import com.webservice.algorithmchef.model.User;
import com.webservice.algorithmchef.model.UserAllergy;
import com.webservice.algorithmchef.model.UserHealthGoal;
import com.webservice.algorithmchef.model.UserPreference;
import com.webservice.algorithmchef.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiRecipeService {

    private final Client client;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    // 검색 사이트 제한 (오키친, 만개의레시피)
    private static final String TARGET_SITES = "site:10000recipe.com OR site:okitchen.co.kr";

    // 1. [주소 1] 유저 성향 및 식습관 기반 추천 (prefer)
    @Transactional(readOnly = true)
    public List<GeminiRecipeResponse> recommendPrefer(String userId, List<String> excludedTitles) {
        String[] profile = extractUserProfile(userId);

        String specificPrompt = """
            [요청 타입: 사용자 맞춤형 건강/성향 추천]
            사용자의 알레르기 정보, 기피하는 재료, 건강 목표를 최우선으로 고려하여,
            사용자의 입맛(선호 재료, 맵기)에 딱 맞는 '정석적인 건강 레시피' 3가지를 추천해줘.
            """;

        return generateAndCallGemini(specificPrompt, profile, excludedTitles, false);
    }

    // 2. [주소 2] speech-to-text 조건 기반 추천 (condition)
    @Transactional(readOnly = true)
    public List<GeminiRecipeResponse> recommendCondition(String userId, String condition, List<String> excludedTitles) {
        String[] profile = extractUserProfile(userId);

        String specificPrompt = String.format("""
            [요청 타입: 상황 및 기분 등의 조건에 기반한 추천]
            사용자가 입력한 상황: "%s"
            
            1. 위 상황에 적절하고 사용자의 기분을 맞춰줄 수 있는 레시피를 3가지 추천해줘.
            2. 단, 사용자의 알레르기 및 기피 재료는 절대 포함하면 안 돼.
            3. 사용자의 선호 맵기와 좋아하는 식재료를 최대한 반영해줘.
            """, condition);

        // 기분 등 정성적 요소에 기반한 추천은 검색의 유연성을 부여 (isFlexible = true)
        return generateAndCallGemini(specificPrompt, profile, excludedTitles, true);
    }

    // 3. [주소 3] 임박재료 기반 추천 (expir)
    @Transactional(readOnly = true)
    public List<GeminiRecipeResponse> recommendExpir(String userId, List<String> ingredients, List<String> excludedTitles) {
        String[] profile = extractUserProfile(userId);
        String ingredientStr = String.join(", ", ingredients);

        String specificPrompt = String.format("""
            [요청 타입: 냉장고 재료털기 (소비기한이 임박한 재료 소진)]
            사용자가 반드시 사용해야 하는 냉장고 재료: [%s]
            
            1. 위 냉장고 재료들을 메인으로 활용하는 레시피 3가지를 추천해줘.
            2. 사용자의 알레르기 및 기피 재료는 절대 사용하지 마.
            3. 사용자의 건강 목표에 반하는 레시피도 추천하지 마.
            4. 사용자의 맵기 취향을 고려하되, 추가 구매가 필요한 재료를 최소화해줘.
            """, ingredientStr);

        return generateAndCallGemini(specificPrompt, profile, excludedTitles, false);
    }


    // DB 정보 조회 및 문자열 변환 (프로필 정보 추출)
    private String[] extractUserProfile(String userId) {
        // 1. User 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 2. 알레르기 정보 추출
        String allergyInfo = "없음";
        if (user.getUserAllergyList() != null && !user.getUserAllergyList().isEmpty()) {
            allergyInfo = user.getUserAllergyList().stream()
                    .map(UserAllergy::getAllergy)
                    .map(allergy -> allergy.getName())
                    .collect(Collectors.joining(", "));
        }

        // 3. 건강 목표 정보 추출
        String goalInfo = "없음";
        if (user.getUserHealthGoals() != null && !user.getUserHealthGoals().isEmpty()) {
            goalInfo = user.getUserHealthGoals().stream()
                    .map(UserHealthGoal::getHealthGoal)
                    .map(goal -> goal.getName() + "(" + goal.getDescription() + ")")
                    .collect(Collectors.joining(", "));
        }

        // 4. UserPreference (취향) 정보 추출
        UserPreference pref = user.getUserPreference();
        String disLiked = "없음";
        String liked = "없음";
        String spice = "보통"; // 기본값

        if (pref != null) {
            // 싫어하는 재료 (기피 재료)
            if (pref.getDisLikedIngredients() != null && !pref.getDisLikedIngredients().isBlank()) {
                disLiked = pref.getDisLikedIngredients();
            }
            // 좋아하는 재료 (선호 재료)
            if (pref.getLikedIngredients() != null && !pref.getLikedIngredients().isBlank()) {
                liked = pref.getLikedIngredients();
            }
            // 맵기 정도
            if (pref.getSpiceLevel() != null && !pref.getSpiceLevel().isBlank()) {
                spice = pref.getSpiceLevel();
            }
        }

        // 배열 인덱스 매핑:
        // [0]:알레르기, [1]:건강목표, [2]:기피재료(disLiked), [3]:선호재료(liked), [4]:맵기(spice)
        return new String[]{allergyInfo, goalInfo, disLiked, liked, spice};
    }


    // 프롬프트 생성 및 Gemini 호출
    private List<GeminiRecipeResponse> generateAndCallGemini(
            String specificTask,
            String[] profile,
            List<String> excludedTitles,
            boolean isFlexible
    ) {
        String allergyInfo = profile[0];
        String goalInfo = profile[1];
        String disLikedInfo = profile[2];
        String likedInfo = profile[3];
        String spiceLevel = profile[4];

        // 재요청시의 기추천 레시피 제외 필터
        String exclusionPrompt = "";
        if (excludedTitles != null && !excludedTitles.isEmpty()) {
            exclusionPrompt = String.format("""
                [중복 방지] 이전에 추천한 [%s]와 동일한 메뉴는 절대 다시 추천하지 마.
                """, String.join(", ", excludedTitles));
        }

        // 검색 제약 조건
        String searchInstruction = String.format("검색 쿼리 뒤에 반드시 `%s`를 붙여서 검색해.", TARGET_SITES);
        if (isFlexible) {
            searchInstruction += "\n(기분이나 느낌과 같은 정성적 요소에 대한 해석에는 네 지식을 써도 되지만, 레시피 재료와 조리법은 반드시 위 사이트 검색 결과만 인용해야 해.)";
        }

        // 최종 프롬프트 구성
        String finalPrompt = String.format("""
            너는 '팩트 기반' 개인 맞춤형 레시피 추천 AI야. 절대 상상하거나 지어내지 마. 아래 사용자의 프로필 정보를 철저히 분석하여 레시피를 추천해줘.
            
            === 1. 사용자 프로필 (DB 정보) ===
            [제약 사항 - 절대 준수]
            - **알레르기(섭취 시 위험)**: %s
            - **기피하는 재료(섭취 거부)**: %s
            * 위 재료들이 포함된 레시피는 절대 추천하지 마. 재료 목록뿐만 아니라 소스나 양념에 포함된 경우도 제외해.
            
            [건강 및 취향 - 적극 반영]
            - **식습관/건강 목표**: %s
            - **선호하는 재료(가산점)**: %s (가능한 경우 이 재료들을 활용한 레시피를 우선해줘)
            - **선호하는 맵기 정도**: %s (이 맵기 수준에 맞는 레시피를 선정해줘)
            
            === 2. 요청 사항 ===
            %s
            
            === 3. 필터링 ===
            %s
            
            === 4. 검색 규칙 ===
            1. %s
            2. **[Grounding 원칙]**: 네가 알고 있는 사전 지식을 사용하여 레시피를 창조하지 마.
            3. 오직 **Google Search 도구가 반환한 검색 결과(Snippet)**에 있는 내용만 정리해서 답변해.
            4. 만약 위 사이트에서 사용자 조건(특히 알레르기 및 기피재료 제외 조건)을 만족하는 레시피를 찾을 수 없다면, 무리해서 추천하지 말고 빈 리스트를 반환해.
            
            === 5. 출력 형식 (JSON Only) ===
            인사말, 설명, 마크다운(```) 없이 오직 아래 JSON 배열만 반환해. 찾을 수 없는 항목은 자료형에 맞게 0.0 혹은 "정보 없음"으로 반환해.
            [
              {
                "name": "요리명",
                "description": "요리에 대한 간단한 개요 및 추천 이유(사용자 취향 반영 여부 언급)",
                "kcal": 실수(double), // 소숫점 첫째자리까지(예: 365.0)
                "portions": "몇 인분",
                "time": "예상 조리 시간",
                "imageUrl" : "완성된 요리 사진 url 주소",
                "ingredients": "재료1, 재료2, 재료3",
                "instructions": "1. ..., 2. ..., 3. ...",
                "tip": "레시피 팁 및 맵기 조절 팁",
                "type": "gemini"
              }
            ]
            """,
                allergyInfo, disLikedInfo, // 제약 사항
                goalInfo, likedInfo, spiceLevel, // 취향 정보
                specificTask, exclusionPrompt, searchInstruction); // 요청 및 규칙

        return callGeminiWithRetry(finalPrompt);
    }

    private List<GeminiRecipeResponse> callGeminiWithRetry(String prompt) {
        Tool searchTool = Tool.builder()
                .googleSearch(GoogleSearch.builder().build())
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .tools(Collections.singletonList(searchTool))
                .build();

        // gemini 모델 설정
        String modelName = "gemini-2.5-flash-lite";
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            try {
                log.info("Gemini 호출 시도 {}/{}", i + 1, maxRetries);
                GenerateContentResponse response = client.models.generateContent(modelName, prompt, config);
                String text = response.text();

                if (text != null) {
                    // 마크다운 제거 및 JSON 파싱
                    text = text.replace("```json", "").replace("```", "").trim();
                    return objectMapper.readValue(text, new TypeReference<List<GeminiRecipeResponse>>() {});
                }
            } catch (Exception e) {
                log.warn("Gemini 호출 실패 (시도 {}): {}", i + 1, e.getMessage());
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Gemini API 응답 실패: 최대 재시도 횟수 초과");
    }
}

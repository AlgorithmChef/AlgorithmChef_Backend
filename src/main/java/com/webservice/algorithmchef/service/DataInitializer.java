package com.webservice.algorithmchef.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.webservice.algorithmchef.model.Allergy;
import com.webservice.algorithmchef.model.HealthGoal;
import com.webservice.algorithmchef.model.Ingredient;
import com.webservice.algorithmchef.repository.AllergyRepository;
import com.webservice.algorithmchef.repository.HealthGoalRepository;
import com.webservice.algorithmchef.repository.IngredientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final HealthGoalRepository healthGoalRepository;
    private final AllergyRepository allergyRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // --- 1. 건강 목표 초기화 ---
        if (healthGoalRepository.count() == 0) {
            log.info("데이터 베이스에 건강 목표 삽입 진행합니다.");
            List<HealthGoal> goals = Arrays.asList(
                    HealthGoal.builder().name("다이어트 (체중 감량)").category("체중 관리").description("저칼로리, 저지방 식단").build(),
                    HealthGoal.builder().name("근육 증가 (고단백)").category("체중 관리").description("단백질 함량이 높은 식단").build(),
                    HealthGoal.builder().name("체중 유지").category("체중 관리").description("균형 잡힌 영양소 섭취").build(),

                    HealthGoal.builder().name("저염 식단").category("건강 상태").description("나트륨 섭취를 줄이는 식단").build(),
                    HealthGoal.builder().name("혈압 관리").category("건강 상태").description("칼륨이 풍부한 식단").build(),
                    HealthGoal.builder().name("당뇨 관리 (저당)").category("건강 상태").description("혈당을 천천히 올리는 식단").build(),
                    HealthGoal.builder().name("콜레스테롤 관리").category("건강 상태").description("포화지방이 적은 식단").build(),

                    HealthGoal.builder().name("채식 (비건)").category("라이프스타일").description("모든 동물성 제품 제외").build(),
                    HealthGoal.builder().name("채식 (락토-오보)").category("라이프스타일").description("고기 제외 (유제품, 계란 허용)").build(),
                    HealthGoal.builder().name("임산부 식단").category("라이프 스타일")
                            .description("엽산, 철분 등이 풍부하고 날음식을 피하는 식단").build(),
                    HealthGoal.builder().name("키토제닉 (고지방 저탄수화물)").category("라이프 스타일")
                            .description("탄수화물을 극단적으로 줄이고 지방 섭취").build()
            );
            healthGoalRepository.saveAll(goals);
            log.info("건강 목표 저장 완료");
        }

        // --- 2. 알레르기 정보 초기화 ---
        if (allergyRepository.count() == 0) {
            log.info("데이터 베이스에 알러지 종류 삽입 진행합니다.");
            List<Allergy> allergies = Arrays.asList(
                    Allergy.builder().name("갑각류 알레르기").build(),
                    Allergy.builder().name("견과 알레르기").build(),
                    Allergy.builder().name("달걀 알레르기").build(),
                    Allergy.builder().name("땅콩 알레르기").build(),
                    Allergy.builder().name("밀 알레르기").build(),
                    Allergy.builder().name("생선 알레르기").build(),
                    Allergy.builder().name("우유 알레르기").build(),
                    Allergy.builder().name("조개 알레르기").build(),
                    Allergy.builder().name("콩 알레르기").build(),
                    Allergy.builder().name("복숭아 알레르기").build()
            );
            allergyRepository.saveAll(allergies);
            log.info("알레르기 정보 데이터베이스에 저장완료");
        }

        // --- 3. 식재료 정보 초기화 (CSV) ---
        if (ingredientRepository.count() == 0) {

            log.info("식재료 데이터를 DB에 삽입합니다...");

            List<Ingredient> list = new ArrayList<>();

            InputStream is = getClass().getResourceAsStream("/static/ingredients_clean.csv");
            if (is == null) {
                log.error("ingredients_clean.csv 파일을 찾을 수 없습니다! (경로: /static/ingredients_clean.csv)");
                return;
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                
                // (★수정된 부분★) 헤더 스킵을 위한 플래그
                boolean isFirstLine = true; 
                int lineNumber = 0; // 로그용 라인 번호

                String line;
                while ((line = br.readLine()) != null) {
                    lineNumber++;
                    
                    // 첫 번째 줄(헤더)이면 건너뜁니다.
                    if (isFirstLine) {
                        isFirstLine = false;
                        // log.info("Skipping header: {}", line); // 헤더 내용 확인 (필요시 주석 해제)
                        continue;
                    }

                    if (line.trim().isEmpty()) { // 빈 줄은 건너뛰기
                        continue;
                    }

                    String[] arr = line.split(",");

                    // (★개선 추천★)
                    // 데이터 행 파싱 중 예외가 발생해도 전체가 중단되지 않도록
                    // try-catch로 감싸주는 것이 좋습니다. (아래 팁 참고)
                    try {
                        if (arr.length != 4) {
                            log.warn("Skipping line {}: CSV format does not have 4 columns. Content: {}", lineNumber, line);
                            continue;
                        }

                        String name = arr[0].trim();
                        String category = arr[1].trim();
                        String storage = arr[2].trim();
                        int expiry = Integer.parseInt(arr[3].trim()); // 데이터 오류 시 여기서 예외 발생 가능

                        Ingredient ingredient = Ingredient.builder()
                                .name(name)
                                .category(category)
                                .storageType(storage)
                                .avgExpiryDays(expiry)
                                .build();

                        list.add(ingredient);

                    } catch (NumberFormatException e) {
                        // 특정 라인이 숫자가 아니어도, 로그만 남기고 다음 라인으로 계속 진행
                        log.error("Skipping line {}: Failed to parse expiry days (not a number). Content: {}", lineNumber, line);
                    } catch (Exception e) {
                        // 기타 예외 처리
                        log.error("Skipping line {}: An unexpected error occurred. Content: {}", lineNumber, line, e);
                    }
                }
            }

            ingredientRepository.saveAll(list);
            log.info("식재료 데이터 저장 완료 (총 {}개)", list.size());
        }
    }
}
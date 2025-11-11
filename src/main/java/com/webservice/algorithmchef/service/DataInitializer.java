package com.webservice.algorithmchef.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.webservice.algorithmchef.model.Allergy;
import com.webservice.algorithmchef.model.HealthGoal;
import com.webservice.algorithmchef.repository.AllergyRepository;
import com.webservice.algorithmchef.repository.HealthGoalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner{
	
	private final HealthGoalRepository healthGoalRepository;
	private final AllergyRepository allergyRepository;
	
	@Override
	@Transactional
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
			if(healthGoalRepository.count() == 0) {
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
	                    HealthGoal.builder().name("임산부 식단").category("라이프 스타일").
	                    description("엽산, 철분 등이 풍부하고 날음식을 피하는 식단").build(),
	                    HealthGoal.builder().name("키토제닉 (고지방 저탄수화물)").category("라이프 스타일")
	                    .description("탄수화물을 극단적으로 줄이고 지방 섭취").build()
	                );
	                healthGoalRepository.saveAll(goals);
	                log.info("건강 목표 저장 완료");
			}
			
			if(allergyRepository.count() == 0) {
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
		
	}

}

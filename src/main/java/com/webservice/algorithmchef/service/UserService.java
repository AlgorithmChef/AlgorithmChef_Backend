package com.webservice.algorithmchef.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webservice.algorithmchef.dto.user.mypage.MyPageResponse;
import com.webservice.algorithmchef.dto.user.survey.SurveyRequest;
import com.webservice.algorithmchef.dto.user.survey.MessageResponse;
import com.webservice.algorithmchef.dto.user.survey.NudgeRequest;
import com.webservice.algorithmchef.model.Allergy;
import com.webservice.algorithmchef.model.HealthGoal;
import com.webservice.algorithmchef.model.User;
import com.webservice.algorithmchef.model.UserAllergy;
import com.webservice.algorithmchef.model.UserHealthGoal;
import com.webservice.algorithmchef.model.UserPreference;
import com.webservice.algorithmchef.repository.AllergyRepository;
import com.webservice.algorithmchef.repository.HealthGoalRepository;
import com.webservice.algorithmchef.repository.UserRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{
	
	
	private final UserRepository userRepository;
	private final HealthGoalRepository healthGoalRepository;
	private final AllergyRepository allergyRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    
		return userRepository.findByUserId(username)
	            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
	}
	
	
	@Transactional
	public MessageResponse makeOrUpdateSurvey(SurveyRequest sRequest,String userId) {
		User user = userRepository.findByUserId(userId)
				.orElseThrow(()-> new IllegalArgumentException("해당 아이디에 적합한 사용자를 찾을 수 없습니다"));
				
		List<Long> healthGoalIds = sRequest.getHealthGoalIds();
		List<Long> allergyIds = sRequest.getAllergyIds();
		
		if(user.getUserHealthGoals() != null) {
			user.getUserHealthGoals().clear();
		}
		List<UserHealthGoal> newHealthGoals = healthGoalIds.stream().map(id ->{
					HealthGoal goal = healthGoalRepository.findById(id)
							.orElseThrow(()-> new IllegalArgumentException("해당하는 아이디로 건강 목표를 찾을 수 없습니다."));
					return UserHealthGoal.builder()
							.user(user)
							.healthGoal(goal)
							.build();
				}).toList();
		user.getUserHealthGoals().addAll(newHealthGoals);
		
		if(user.getUserAllergyList()!=null) {
			user.getUserAllergyList().clear();
		}
		List<UserAllergy> newUserAllergies = allergyIds.stream().map(id ->{
			Allergy allergy = allergyRepository.findById(id)
					.orElseThrow(()-> new IllegalArgumentException("해당하는 아이디로 알러지 정보를 찾을 수 없습니다."));
			return UserAllergy.builder()
							.allergy(allergy)
							.user(user)
							.build();
		}).toList();
		user.getUserAllergyList().addAll(newUserAllergies);
		
		
		UserPreference userPreference = user.getUserPreference();
        boolean isNewPreference = false;

		if(userPreference == null) {
            isNewPreference = true;
			userPreference = UserPreference.builder()
										.user(user)
										.build();
            user.setUserPreference(userPreference);
		}
        
        userPreference.setDisLikedIngredients(sRequest.getDislikedIngredients());
        userPreference.setLikedIngredients(sRequest.getLikedIngredients());
        userPreference.setSpiceLevel(sRequest.getSpiceLevel());
        userPreference.setPreferredCuisine(sRequest.getPreferredCuisine());
        userPreference.setAllowPushComment(sRequest.isAllowPushComment());
        userPreference.setAllowPushConsumption(sRequest.isAllowPushConsumption());
        userPreference.setAllowPushNudge(sRequest.isAllowPushNudge());
			
        String message = isNewPreference ? 
            "님의 취향과 알림 설정이 성공적으로 저장되었습니다." : 
            "님의 취향과 알림 설정이 성공적으로 업데이트 되었습니다.";

		return new MessageResponse(userId,message);
	}
	
	public MyPageResponse retrieveMyInformation(String userId) {
		User user = userRepository.findByUserId(userId)
						.orElseThrow(()-> new IllegalArgumentException("해당 아이디에 적합한 사용자를 찾을 수 없습니다"));
		return new MyPageResponse(user);
	}
	
	@Transactional
	public MessageResponse postponeNudge(NudgeRequest nRequest, String userId) {
		boolean isWantsRegisterNudge = nRequest.isWantsRegisterNudge();
		User user = userRepository.findByUserId(userId)
				.orElseThrow(()-> new IllegalArgumentException("해당 아이디에 적합한 사용자를 찾을 수 없습니다"));
		user.getUserPreference().setWantsRegisterNudge(isWantsRegisterNudge);
		String message = "알림이 예약되었습니다. 3일 뒤에 다시 알려드릴게요!";
		return new MessageResponse(userId, message);
	}
	
	

}

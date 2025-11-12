package com.webservice.algorithmchef.dto.user.mypage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import com.webservice.algorithmchef.model.User;

import lombok.Getter;

@Getter
public class MyPageResponse {
	
	private String userId;
	private LocalDateTime birthDate;
	private int age;
	private List<String> goals;
	private List<String> allergyList;
	private String dislikedIngredients;
	private String likedIngredients;
	private String preferredCuisine;
	private String spiceLevel;
	private boolean allowPushConsumption;
	private boolean allowPushComment;
	private boolean allowPushNudge;
	
	public MyPageResponse(User user) {
		this.userId = user.getUserId();
		this.birthDate = user.getBirthDate();
		this.age = Period.between(birthDate.toLocalDate(), LocalDate.now()).getYears();
		this.goals = user.getUserHealthGoals().stream()
                .map(userHealthGoal -> userHealthGoal.getHealthGoal().getName())
                .toList();
		this.allergyList = user.getUserAllergyList().stream()
                .map(userAllergy -> userAllergy.getAllergy().getName())
                .toList();
		this.dislikedIngredients = user.getUserPreference().getDisLikedIngredients();
		this.likedIngredients = user.getUserPreference().getLikedIngredients();
		this.preferredCuisine = user.getUserPreference().getPreferredCuisine();
		this.spiceLevel = user.getUserPreference().getSpiceLevel();
		this.allowPushConsumption = user.getUserPreference().isAllowPushConsumption();
		this.allowPushComment = user.getUserPreference().isAllowPushComment();
		this.allowPushNudge = user.getUserPreference().isAllowPushNudge();
	}
}

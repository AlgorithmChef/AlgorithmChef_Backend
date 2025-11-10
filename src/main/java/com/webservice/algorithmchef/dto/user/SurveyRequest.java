package com.webservice.algorithmchef.dto.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SurveyRequest {

	private List<Long> healthGoalIds;
	private List<Long> allergyIds;
	private String dislikedIngredients;
	private String likedIngredients;
	private String preferredIngredients;
	private String preferredCuisinel;
	private String spiceLevel;
	private boolean allowPushConsumption;
	private boolean allowPushComment;
	private boolean allowPushNudge;
	
}

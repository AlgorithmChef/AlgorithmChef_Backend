package com.webservice.algorithmchef.dto.user.survey;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SurveyRequest {

	private List<Long> healthGoalIds;
	private List<Long> allergyIds;
	private String dislikedIngredients;
	private String likedIngredients;
	private String preferredCuisine;
	private String spiceLevel;
	private boolean allowPushConsumption;
	private boolean allowPushComment;
	private boolean allowPushNudge;
	
}

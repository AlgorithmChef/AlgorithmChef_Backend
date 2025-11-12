package com.webservice.algorithmchef.dto.healthgoal;

import com.webservice.algorithmchef.model.HealthGoal;

import lombok.Getter;

@Getter
public class HealthGoalResponse {
	private String name;
	private Long id;
	private String category;
	
	public HealthGoalResponse(HealthGoal goal) {
		this.name = goal.getName();
		this.id = goal.getId();
		this.category = goal.getCategory();
	}
}

package com.webservice.algorithmchef.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.webservice.algorithmchef.dto.healthgoal.HealthGoalResponse;
import com.webservice.algorithmchef.model.HealthGoal;
import com.webservice.algorithmchef.repository.HealthGoalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HealthGoalService {

	private final HealthGoalRepository healthGoalRepository;
	
	public List<HealthGoalResponse> retriveAll() {
		List<HealthGoal> goals = healthGoalRepository.findAll();
		
        List<HealthGoalResponse> healthGoals = 
				goals.stream().map(goal -> {
			        return new HealthGoalResponse(goal);
		        })
                .toList();
        
        return healthGoals;
	}
}

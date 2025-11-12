package com.webservice.algorithmchef.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webservice.algorithmchef.dto.healthgoal.HealthGoalResponse;
import com.webservice.algorithmchef.service.HealthGoalService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class HealthGoalController {
	
	private final HealthGoalService hGoalService;
	
	@GetMapping("/healthGoals")
	public ResponseEntity<?> retrieveAll(){
		try {
			List<HealthGoalResponse> goals = hGoalService.retriveAll();
			return ResponseEntity.ok(goals);
		}catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}

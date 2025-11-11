package com.webservice.algorithmchef.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webservice.algorithmchef.model.HealthGoal;

public interface HealthGoalRepository extends JpaRepository<HealthGoal, Long> {

	
}

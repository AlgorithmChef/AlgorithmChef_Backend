package com.webservice.algorithmchef.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webservice.algorithmchef.dto.allergy.AllergyResponse;
import com.webservice.algorithmchef.service.AllergyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AllergyController {
	
	private final AllergyService allergyService;

	@GetMapping("/allergies")
	public ResponseEntity<?> retrieveAll(){
		try {
			List<AllergyResponse> allergies = allergyService.retrieveAll();
			return ResponseEntity.ok(allergies);
			
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}

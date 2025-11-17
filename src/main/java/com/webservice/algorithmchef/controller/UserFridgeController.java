package com.webservice.algorithmchef.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webservice.algorithmchef.dto.userfridge.PageUserFridgeResponse;
import com.webservice.algorithmchef.dto.userfridge.UserFridgeRequest;
import com.webservice.algorithmchef.dto.userfridge.UserFridgeResponse;
import com.webservice.algorithmchef.service.UserFridgeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fridge")
public class UserFridgeController {
	
	private final UserFridgeService userFridgeService;
	
	@PostMapping("/ingredient/register/manual")
	public ResponseEntity<?> registerIngredients(@RequestBody UserFridgeRequest fridgeRequest,
			@AuthenticationPrincipal UserDetails userDetails){
		try {
			String userId = userDetails.getUsername();
			UserFridgeResponse response = userFridgeService.addIngredients(userId, fridgeRequest);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/ingredients")
	public ResponseEntity<?> retrieve(
			@RequestParam(value="category",required = false)String category,
			@RequestParam(value="name",required = false)String name,
			@RequestParam(value="page", defaultValue = "0")int page,
			@RequestParam(value="size", defaultValue = "10")int size,
			@AuthenticationPrincipal UserDetails userDetails){
		try {
			String userId = userDetails.getUsername();
			PageUserFridgeResponse response = null;
			if(category != null) {
				response = userFridgeService.filteredByCategory(userId, category, size, page);
			}else if(name != null) {
				response = userFridgeService.filteredByName(userId, name, size, page);
			}else {
				response = userFridgeService.retrieveAll(userId, size, page);				
			}
			return ResponseEntity.ok(response);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}

}

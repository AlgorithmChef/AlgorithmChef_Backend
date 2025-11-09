package com.webservice.algorithmchef.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webservice.algorithmchef.dto.user.UserLoginRequest;
import com.webservice.algorithmchef.dto.user.UserLoginResponse;
import com.webservice.algorithmchef.dto.user.UserSignUpRequest;
import com.webservice.algorithmchef.dto.user.UserSignUpResponse;
import com.webservice.algorithmchef.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

	private final UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest){
		try {
			UserLoginResponse userLoginResponse = userService.login(userLoginRequest);
			return ResponseEntity.ok(userLoginResponse);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/signUp")
	public ResponseEntity<?> signUp(@RequestBody UserSignUpRequest userSignUpRequest){
		try {
			UserSignUpResponse userSignUpResponse = userService.signUp(userSignUpRequest);
			return ResponseEntity.status(HttpStatus.CREATED).body(userSignUpResponse);
			
		}catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}

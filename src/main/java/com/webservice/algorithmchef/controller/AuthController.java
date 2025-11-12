package com.webservice.algorithmchef.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webservice.algorithmchef.dto.user.ChangePasswordRequest;
import com.webservice.algorithmchef.dto.user.ChangePasswordResponse;
import com.webservice.algorithmchef.dto.user.FindPasswordRequest;
import com.webservice.algorithmchef.dto.user.FindPasswordResponse;
import com.webservice.algorithmchef.dto.user.FindUserIdRequest;
import com.webservice.algorithmchef.dto.user.FindUserIdResponse;
import com.webservice.algorithmchef.dto.user.UserLoginRequest;
import com.webservice.algorithmchef.dto.user.UserLoginResponse;
import com.webservice.algorithmchef.dto.user.UserSignUpRequest;
import com.webservice.algorithmchef.dto.user.UserSignUpResponse;
import com.webservice.algorithmchef.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest){
		try {
			UserLoginResponse userLoginResponse = authService.login(userLoginRequest);
			return ResponseEntity.ok(userLoginResponse);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/signUp")
	public ResponseEntity<?> signUp(@RequestBody UserSignUpRequest userSignUpRequest){
		try {
			UserSignUpResponse userSignUpResponse = authService.signUp(userSignUpRequest);
			return ResponseEntity.status(HttpStatus.CREATED).body(userSignUpResponse);
			
		}catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@Transactional
	@PatchMapping("/findPassword")
	public ResponseEntity<?> findPassword(@RequestBody FindPasswordRequest fPasswordRequest){
		try {
			FindPasswordResponse pResponse = authService.findPassword(fPasswordRequest);
			return ResponseEntity.ok(pResponse);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PatchMapping("/update-tempPassword")
	public ResponseEntity<?> updateTempPasswod(@RequestBody ChangePasswordRequest cPasswordRequest,
			@AuthenticationPrincipal UserDetails userDetails){
		try {
			String userId = userDetails.getUsername();
			ChangePasswordResponse cResponse = authService.updateTempPassword(cPasswordRequest, userId);
			return ResponseEntity.ok(cResponse);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/findUserId")
	public ResponseEntity<?> findUserId(@RequestBody FindUserIdRequest fIdRequest){
		try {
			FindUserIdResponse idResponse = authService.findUserId(fIdRequest);
			return ResponseEntity.ok(idResponse);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}

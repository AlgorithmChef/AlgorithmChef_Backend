package com.webservice.algorithmchef.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webservice.algorithmchef.dto.user.mypage.MyPageResponse;
import com.webservice.algorithmchef.dto.user.survey.SurveyRequest;
import com.webservice.algorithmchef.dto.user.survey.MessageResponse;
import com.webservice.algorithmchef.dto.user.survey.NudgeRequest;
import com.webservice.algorithmchef.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	
	@PostMapping("/survey")
	public ResponseEntity<?> makeSurvey(@RequestBody SurveyRequest sRequest,
			@AuthenticationPrincipal UserDetails userDetails){
		try {
			String userId = userDetails.getUsername();
			MessageResponse sResponse = userService.makeOrUpdateSurvey(sRequest, userId);
			return ResponseEntity.status(HttpStatus.CREATED).body(sResponse);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/mypage")
	public ResponseEntity<?> retrieveMyInformation(@AuthenticationPrincipal UserDetails userDetails){
		try {
			String userId = userDetails.getUsername();
			MyPageResponse pageResponse = userService.retrieveMyInformation(userId);
			return ResponseEntity.ok(pageResponse);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PatchMapping("/survey")
	public ResponseEntity<?> updateSurvey(@RequestBody SurveyRequest sRequest,
			@AuthenticationPrincipal UserDetails userDetails){
		try {
			String userId = userDetails.getUsername();
			MessageResponse mResponse = userService.makeOrUpdateSurvey(sRequest, userId);
			return ResponseEntity.ok(mResponse);
		}catch (IllegalArgumentException e) {
			// TODO: handle exception
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PatchMapping("/postpone/register")
	public ResponseEntity<?> postponeNudge(@RequestBody NudgeRequest nRequest,
			@AuthenticationPrincipal UserDetails userDetails){
		try {
			String userId = userDetails.getUsername();
			MessageResponse mResponse = userService.postponeNudge(nRequest, userId);
			return ResponseEntity.ok(mResponse);
		}catch (IllegalArgumentException e) {
			// TODO: handle exception
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
}

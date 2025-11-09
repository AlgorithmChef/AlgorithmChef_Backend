package com.webservice.algorithmchef.dto.user;

import com.webservice.algorithmchef.model.User;

import lombok.Getter;

@Getter
public class UserSignUpResponse {
	
	private Long id;
	private String userId;
	private String role;
	
	public UserSignUpResponse(User user) {
		this.id = user.getId();
		this.userId = user.getUserId();
		this.role=user.getRole();
	}

}

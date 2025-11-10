package com.webservice.algorithmchef.dto.user;

import com.webservice.algorithmchef.model.User;

public class FindPasswordResponse {

	private String userId;
	private String message;
	
	public FindPasswordResponse(User user) {
		this.userId = user.getUserId();
		this.message = user.getEmail() + "로 임시비밀번호 전송했습니다.";
	}
	
}

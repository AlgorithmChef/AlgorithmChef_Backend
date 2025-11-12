package com.webservice.algorithmchef.dto.user.survey;

import lombok.Getter;

@Getter
public class MessageResponse {

	private String message;
	
	public MessageResponse(String userId, String message) {
		this.message = userId + message;
	}
}

package com.webservice.algorithmchef.dto.user;

import lombok.Getter;

@Getter
public class FindUserIdResponse {

	private String userId;
	public FindUserIdResponse(String userId) {
	    this.userId = userId;
	}
}

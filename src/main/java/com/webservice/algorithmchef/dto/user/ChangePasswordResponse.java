package com.webservice.algorithmchef.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangePasswordResponse {

	private String accessToken;
	private String message;
}

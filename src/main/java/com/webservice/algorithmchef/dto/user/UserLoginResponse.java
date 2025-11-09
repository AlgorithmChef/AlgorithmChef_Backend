package com.webservice.algorithmchef.dto.user;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {

	private String accessToken;
	private LoginStatus status;	
	
	 public enum LoginStatus {
        SUCCESS,               
        FORCE_PASSWORD_CHANGE
    }
	 
}

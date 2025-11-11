package com.webservice.algorithmchef.dto.user;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class FindUserIdRequest {
	private String email;
	private LocalDateTime birthDate;

}

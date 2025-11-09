package com.webservice.algorithmchef.dto.user;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpRequest {

	private String userId;
	private String password;
	private LocalDateTime birthDate;
	private String email;
	private String gender;
}

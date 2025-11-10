package com.webservice.algorithmchef.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindPasswordRequest {

	private String userId;
	private String email;
}

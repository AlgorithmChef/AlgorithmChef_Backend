package com.webservice.algorithmchef.dto.user.survey;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NudgeRequest {

	 @JsonProperty("wants_register_nudge")
	 private boolean wantsRegisterNudge; 
}

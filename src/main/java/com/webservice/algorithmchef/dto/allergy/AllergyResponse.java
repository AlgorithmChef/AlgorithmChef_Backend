package com.webservice.algorithmchef.dto.allergy;

import com.webservice.algorithmchef.model.Allergy;

import lombok.Getter;

@Getter
public class AllergyResponse {

	private Long id;
	private String name;
	
	public AllergyResponse(Allergy allergy) {
		this.id = allergy.getId();
		this.name = allergy.getName();
	}
}

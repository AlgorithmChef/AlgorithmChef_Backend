package com.webservice.algorithmchef.dto.userfridge;

import org.springframework.data.domain.Page;

import com.webservice.algorithmchef.dto.fridgeingredient.FridgeIngredientResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageUserFridgeResponse {
	private Long id;
	private Page<FridgeIngredientResponse> ingredients;
}

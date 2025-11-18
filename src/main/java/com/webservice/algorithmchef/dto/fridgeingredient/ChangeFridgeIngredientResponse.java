package com.webservice.algorithmchef.dto.fridgeingredient;

import com.webservice.algorithmchef.model.FridgeIngredient;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeFridgeIngredientResponse {

	private Long ingredientId;
	private String name;
	private String message;
	
	public ChangeFridgeIngredientResponse(FridgeIngredient ingredient,String message) {
		this.ingredientId = ingredient.getId();
		this.name = ingredient.getIngredient().getName();
		this.message = message;
	}
}

package com.webservice.algorithmchef.dto.ingredient;

import com.webservice.algorithmchef.model.Ingredient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IngredientResponse {
	
	private Long id;
	private String name;
	private String category;
	
	public IngredientResponse(Ingredient ingredient) {
		this.id = ingredient.getId();
		this.name = ingredient.getName();
		this.category = ingredient.getCategory();
	}

}

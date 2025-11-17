package com.webservice.algorithmchef.dto.ingredient;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IngredientRequest {

	public String name;
	public LocalDateTime purchaseDate;
}

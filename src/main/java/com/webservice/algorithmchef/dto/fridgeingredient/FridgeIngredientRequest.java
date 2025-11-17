package com.webservice.algorithmchef.dto.fridgeingredient;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FridgeIngredientRequest {

	public String name;
	public LocalDateTime purchaseDate;
}

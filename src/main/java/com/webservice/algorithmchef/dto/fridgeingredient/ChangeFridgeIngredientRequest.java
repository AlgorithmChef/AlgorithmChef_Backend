package com.webservice.algorithmchef.dto.fridgeingredient;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeFridgeIngredientRequest {

	private Long ingredientId;
	private LocalDateTime purchasedDate;
}

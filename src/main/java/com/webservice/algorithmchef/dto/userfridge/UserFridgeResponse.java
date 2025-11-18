package com.webservice.algorithmchef.dto.userfridge;

import java.util.List;

import com.webservice.algorithmchef.dto.fridgeingredient.FridgeIngredientResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFridgeResponse {
	
	private Long fridgeId;
	private List<FridgeIngredientResponse> ingredients;

}

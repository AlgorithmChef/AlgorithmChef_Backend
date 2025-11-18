package com.webservice.algorithmchef.dto.userfridge;

import java.util.List;

import com.webservice.algorithmchef.dto.fridgeingredient.FridgeIngredientRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserFridgeRequest {

	private List<FridgeIngredientRequest> ingredients;
}

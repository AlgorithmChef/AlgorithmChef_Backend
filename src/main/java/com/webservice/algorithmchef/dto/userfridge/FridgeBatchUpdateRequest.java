package com.webservice.algorithmchef.dto.userfridge;

import java.util.List;

import com.webservice.algorithmchef.dto.fridgeingredient.FridgeIngredientUpdateItem;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FridgeBatchUpdateRequest {
	@Valid
    private List<FridgeIngredientUpdateItem> ingredients;
}

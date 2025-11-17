package com.webservice.algorithmchef.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.webservice.algorithmchef.dto.ingredient.IngredientRequest;
import com.webservice.algorithmchef.dto.ingredient.IngredientResponse;
import com.webservice.algorithmchef.dto.userfridge.UserFridgeRequest;
import com.webservice.algorithmchef.dto.userfridge.UserFridgeResponse;
import com.webservice.algorithmchef.model.Fridge;
import com.webservice.algorithmchef.model.FridgeIngredient;
import com.webservice.algorithmchef.model.Ingredient;
import com.webservice.algorithmchef.repository.FridgeIngredientRepository;
import com.webservice.algorithmchef.repository.FridgeRepository;
import com.webservice.algorithmchef.repository.IngredientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFridgeService {
	
	private final IngredientRepository ingredientRepository;
	private final FridgeIngredientRepository fIngredientRepository;
	private final FridgeRepository fRepository;
	
	public UserFridgeResponse addIngredients(String userId,UserFridgeRequest userFridgeRequest) {
		
		Fridge fridge = fRepository.findByNameAndUser_UserId("나의 냉장고", userId)
				.orElseThrow(() -> new IllegalArgumentException("접근 권한이 없거나 존재하지 않는 냉장고입니다."));
		
		List<FridgeIngredient> newFridgeIngredients = new ArrayList<>();
		List<IngredientRequest> ingredients = userFridgeRequest.getIngredients();
		ingredients.forEach(ingredient -> {
			Ingredient newIngredient = ingredientRepository.findByName(ingredient.getName())
					.orElseThrow(()-> new IllegalArgumentException("해당하는 식자재가 없습니다."));
			
			FridgeIngredient fridgeIngredient = FridgeIngredient.builder()
																.fridge(fridge)
																.ingredient(newIngredient)
																.purchaseDate(ingredient.getPurchaseDate())
																.build();
			newFridgeIngredients.add(fridgeIngredient);
		});
		
		List<FridgeIngredient> savedIngredients = fIngredientRepository.saveAll(newFridgeIngredients);
		List<IngredientResponse> responseIngredients = savedIngredients.stream().
			map(fi ->{
				LocalDateTime expiredDate = fi.getPurchaseDate()
						.plusDays(fi.getIngredient().getAvgExpiryDays());
				return new IngredientResponse(
						fi.getId(),
						fi.getIngredient().getCategory(),
						fi.getIngredient().getName(),
						fi.getPurchaseDate(),
						expiredDate
						);
						
			}).toList();
		
		return new UserFridgeResponse(
				fridge.getId(),
				responseIngredients
				);		
	}

}

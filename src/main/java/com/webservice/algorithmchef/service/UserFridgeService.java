package com.webservice.algorithmchef.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webservice.algorithmchef.dto.fridgeingredient.FridgeIngredientRequest;
import com.webservice.algorithmchef.dto.fridgeingredient.FridgeIngredientResponse;
import com.webservice.algorithmchef.dto.userfridge.PageUserFridgeResponse;
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
	
	@Transactional
	public UserFridgeResponse addIngredients(String userId,UserFridgeRequest userFridgeRequest) {
		
		Fridge fridge = fRepository.findByNameAndUser_UserId("나의 냉장고", userId)
				.orElseThrow(() -> new IllegalArgumentException("접근 권한이 없거나 존재하지 않는 냉장고입니다."));
		
		List<FridgeIngredient> newFridgeIngredients = new ArrayList<>();
		List<FridgeIngredientRequest> ingredients = userFridgeRequest.getIngredients();
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
		List<FridgeIngredientResponse> responseIngredients = savedIngredients.stream().
			map(fi ->{
				LocalDateTime expiredDate = fi.getPurchaseDate()
						.plusDays(fi.getIngredient().getAvgExpiryDays());
				Duration duration = Duration.between(LocalDateTime.now(),expiredDate);
				long daysLeft = duration.toDays();
				return new FridgeIngredientResponse(
						fi.getId(),
						fi.getIngredient().getCategory(),
						fi.getIngredient().getName(),
						fi.getPurchaseDate(),
						expiredDate,
						daysLeft
						);
						
			}).toList();
		
		return new UserFridgeResponse(
				fridge.getId(),
				responseIngredients
				);		
	}
	
	private Page<FridgeIngredientResponse> convertEntityDto(Page<FridgeIngredient> ingredients) {
		return ingredients.map(
				fi ->{
					LocalDateTime expiredDate = fi.getPurchaseDate()
							.plusDays(fi.getIngredient().getAvgExpiryDays());
					Duration duration = Duration.between(LocalDateTime.now(),expiredDate);
					long daysLeft = duration.toDays(); 
					return new FridgeIngredientResponse(
							fi.getId(),
							fi.getIngredient().getCategory(),
							fi.getIngredient().getName(),
							fi.getPurchaseDate(),
							expiredDate,
							daysLeft
							);
				}
		);
	}
	
	public PageUserFridgeResponse retrieveAll(String userId,int size,int page){
		Fridge fridge = fRepository.findByNameAndUser_UserId("나의 냉장고", userId)
				.orElseThrow(() -> new IllegalArgumentException("접근 권한이 없거나 존재하지 않는 냉장고입니다."));
		Pageable pageable = PageRequest.of(page, size,Sort.by("purchaseDate").descending());
		Page<FridgeIngredient> ingredients = fIngredientRepository.findByFridge(fridge, pageable);
		Page<FridgeIngredientResponse> fridgeIngredients = convertEntityDto(ingredients);
		return new PageUserFridgeResponse(
				fridge.getId(),
				fridgeIngredients
		);
	}
	
	public PageUserFridgeResponse filteredByName(String userId,String name,int size,int page) {
		Fridge fridge = fRepository.findByNameAndUser_UserId("나의 냉장고", userId)
				.orElseThrow(() -> new IllegalArgumentException("접근 권한이 없거나 존재하지 않는 냉장고입니다."));
		Pageable pageable = PageRequest.of(page, size,Sort.by("purchaseDate").descending());
		Page<FridgeIngredient> ingredients = fIngredientRepository.findByFridgeAndIngredient_Name(fridge, name, pageable);
		Page<FridgeIngredientResponse> fridgeIngredients = convertEntityDto(ingredients);
		return new PageUserFridgeResponse(
				fridge.getId(),
				fridgeIngredients
		);
	}
	
	public PageUserFridgeResponse filteredByCategory(String userId,String category,int size,int page) {
		Fridge fridge = fRepository.findByNameAndUser_UserId("나의 냉장고", userId)
				.orElseThrow(() -> new IllegalArgumentException("접근 권한이 없거나 존재하지 않는 냉장고입니다."));
		Pageable pageable = PageRequest.of(page, size,Sort.by("purchaseDate").descending());
		Page<FridgeIngredient> ingredients = fIngredientRepository.findByFridgeAndIngredient_Category(fridge, category, pageable);
		Page<FridgeIngredientResponse> fridgeIngredients = convertEntityDto(ingredients);
		return new PageUserFridgeResponse(
				fridge.getId(),
				fridgeIngredients
		);
	}

}

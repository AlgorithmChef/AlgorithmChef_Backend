package com.webservice.algorithmchef.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webservice.algorithmchef.dto.fridgeingredient.ChangeFridgeIngredientRequest;
import com.webservice.algorithmchef.dto.fridgeingredient.ChangeFridgeIngredientResponse;
import com.webservice.algorithmchef.dto.fridgeingredient.FridgeIngredientRequest;
import com.webservice.algorithmchef.dto.fridgeingredient.FridgeIngredientResponse;
import com.webservice.algorithmchef.dto.fridgeingredient.FridgeIngredientUpdateItem;
import com.webservice.algorithmchef.dto.userfridge.FridgeBatchUpdateRequest;
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
		List<FridgeIngredient> savedIngredients = saveFridgeIngredients(fridge, newFridgeIngredients, ingredients);
		List<FridgeIngredientResponse> responseIngredients = toResponseList(savedIngredients);
		
		return new UserFridgeResponse(
				fridge.getId(),
				responseIngredients
				);		
	}

	private List<FridgeIngredientResponse> toResponseList(List<FridgeIngredient> savedIngredients) {
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
		return responseIngredients;
	}

	private List<FridgeIngredient> saveFridgeIngredients(Fridge fridge, List<FridgeIngredient> newFridgeIngredients,
			List<FridgeIngredientRequest> ingredients) {
		ingredients.forEach(ingredient -> {
			Ingredient newIngredient = ingredientRepository.findByName(ingredient.getName())
					.orElseThrow(()-> new IllegalArgumentException("해당하는 식자재가 없습니다."));
			
			FridgeIngredient fridgeIngredient = FridgeIngredient.builder()
																.fridge(fridge)
																.ingredient(newIngredient)
																.purchaseDate(ingredient.getPurchaseDate())
																.expiredDate(ingredient.getPurchaseDate().plusDays(newIngredient.getAvgExpiryDays()))
																.build();
			newFridgeIngredients.add(fridgeIngredient);
		});
		
		List<FridgeIngredient> savedIngredients = fIngredientRepository.saveAll(newFridgeIngredients);
		return savedIngredients;
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
		Page<FridgeIngredient> ingredients = fIngredientRepository.findByFridgeAndIngredient_NameContaining(fridge, name, pageable);
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
	
	@Transactional
	public ChangeFridgeIngredientResponse updatePurchasedDate(String userId,ChangeFridgeIngredientRequest request) {
		Fridge fridge = fRepository.findByNameAndUser_UserId("나의 냉장고", userId)
				.orElseThrow(() -> new IllegalArgumentException("접근 권한이 없거나 존재하지 않는 냉장고입니다."));
		FridgeIngredient fridgeIngredient = fIngredientRepository.findByIdAndFridge(request.getIngredientId(),fridge)
				.orElseThrow(()-> new IllegalArgumentException("해당하는 아이디로 재료 찾을 수 없습니다."));
		fridgeIngredient.setPurchaseDate(request.getPurchasedDate());
		String message = "업데이트 완료";
		return new ChangeFridgeIngredientResponse(fridgeIngredient,message);	
	}
	
	@Transactional
	public void deleteIngredient(String userId, Long fridgeIngredientId) {
		
        Fridge fridge = fRepository.findByNameAndUser_UserId("나의 냉장고", userId)
				.orElseThrow(() -> new IllegalArgumentException("접근 권한이 없거나 존재하지 않는 냉장고입니다."));
        
		FridgeIngredient fridgeIngredient = fIngredientRepository
                .findByIdAndFridge(fridgeIngredientId, fridge)
				.orElseThrow(() -> new IllegalArgumentException("삭제 권한이 없거나 존재하지 않는 재료입니다."));
		
		fIngredientRepository.delete(fridgeIngredient);
	}
	@Transactional
	public UserFridgeResponse updateIngredientInformation(String userId, FridgeBatchUpdateRequest fRequest) {
	    
	    Fridge fridge = fRepository.findByNameAndUser_UserId("나의 냉장고", userId)
	            .orElseThrow(() -> new IllegalArgumentException("접근 권한이 없거나 존재하지 않는 냉장고입니다."));

	    List<FridgeIngredientUpdateItem> requestItems = fRequest.getIngredients();

	    List<Long> ingredientIds = requestItems.stream()
	            .map(FridgeIngredientUpdateItem::getIngredientId)
	            .toList();
	            
	    List<Ingredient> masterIngredients = ingredientRepository.findAllById(ingredientIds);
	    
	    Map<Long, Ingredient> ingredientMap = masterIngredients.stream()
	            .collect(Collectors.toMap(Ingredient::getId, i -> i));

	    if (fridge.getIngredients() != null) {
	        fridge.getIngredients().clear();
	    }
	    
	    List<FridgeIngredient> newFridgeIngredients = new ArrayList<>();
	    
	    for (FridgeIngredientUpdateItem item : requestItems) {
	        Ingredient masterIngredient = ingredientMap.get(item.getIngredientId());
	        
	        if (masterIngredient == null) {
	            throw new IllegalArgumentException("존재하지 않는 식재료 ID입니다: " + item.getIngredientId());
	        }

	        LocalDateTime expiredDate = item.getPurchaseDate()
	                .plusDays(masterIngredient.getAvgExpiryDays());

	        FridgeIngredient fridgeIngredient = FridgeIngredient.builder()
	                .fridge(fridge)
	                .ingredient(masterIngredient)
	                .purchaseDate(item.getPurchaseDate())
	                .expiredDate(expiredDate)
	                .build();
	        
	        newFridgeIngredients.add(fridgeIngredient);
	    }
	    
	    List<FridgeIngredient> savedIngredients = fIngredientRepository.saveAll(newFridgeIngredients);
	    
	    return new UserFridgeResponse(
	            fridge.getId(),
	            toResponseList(savedIngredients)
	    );
	}

}

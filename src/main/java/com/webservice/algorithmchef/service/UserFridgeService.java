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
@Transactional(readOnly = true)
public class UserFridgeService {
	
	private final IngredientRepository ingredientRepository;
	private final FridgeIngredientRepository fIngredientRepository;
	private final FridgeRepository fRepository;
	
	@Transactional
	public UserFridgeResponse addIngredients(String userId, UserFridgeRequest userFridgeRequest) {
		
		Fridge fridge = findFridge(userId);
		
		List<FridgeIngredient> newFridgeIngredients = new ArrayList<>();
		List<FridgeIngredientRequest> ingredients = userFridgeRequest.getIngredients();
		
		for (FridgeIngredientRequest req : ingredients) {
			Ingredient masterIngredient = ingredientRepository.findByName(req.getName())
					.orElseThrow(() -> new IllegalArgumentException("해당하는 식자재가 없습니다: " + req.getName()));
			
			LocalDateTime expiredDate = calculateExpireDate(req.getPurchaseDate(), masterIngredient.getAvgExpiryDays(), null);

			FridgeIngredient fridgeIngredient = FridgeIngredient.builder()
					.fridge(fridge)
					.ingredient(masterIngredient)
					.purchaseDate(req.getPurchaseDate())
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

	@Transactional
	public UserFridgeResponse updateIngredientInformation(String userId, FridgeBatchUpdateRequest fRequest) {
	    
	    Fridge fridge = findFridge(userId);

	    List<FridgeIngredientUpdateItem> requestItems = fRequest.getIngredients();

	    List<Long> ingredientIds = requestItems.stream()
	            .map(FridgeIngredientUpdateItem::getIngredientId)
	            .toList();
	            
	    Map<Long, Ingredient> ingredientMap = ingredientRepository.findAllById(ingredientIds).stream()
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

	        LocalDateTime expiredDate = calculateExpireDate(
	        		item.getPurchaseDate(), 
	        		masterIngredient.getAvgExpiryDays(), 
	        		item.getExpiredDate()
	        );

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
	
	
	public PageUserFridgeResponse retrieveAll(String userId, int size, int page){
		Fridge fridge = findFridge(userId);
		Pageable pageable = PageRequest.of(page, size, Sort.by("purchaseDate").descending());
		
		Page<FridgeIngredient> ingredients = fIngredientRepository.findByFridge(fridge, pageable);
		
		return new PageUserFridgeResponse(
				fridge.getId(),
				ingredients.map(this::toFridgeIngredientResponse)
		);
	}
	
	public PageUserFridgeResponse filteredByName(String userId, String name, int size, int page) {
		Fridge fridge = findFridge(userId);
		Pageable pageable = PageRequest.of(page, size, Sort.by("purchaseDate").descending());
		
		Page<FridgeIngredient> ingredients = fIngredientRepository.findByFridgeAndIngredient_NameContaining(fridge, name, pageable);
		
		return new PageUserFridgeResponse(
				fridge.getId(),
				ingredients.map(this::toFridgeIngredientResponse)
		);
	}
	
	public PageUserFridgeResponse filteredByCategory(String userId, String category, int size, int page) {
		Fridge fridge = findFridge(userId);
		Pageable pageable = PageRequest.of(page, size, Sort.by("purchaseDate").descending());
		
		Page<FridgeIngredient> ingredients = fIngredientRepository.findByFridgeAndIngredient_Category(fridge, category, pageable);
		
		return new PageUserFridgeResponse(
				fridge.getId(),
				ingredients.map(this::toFridgeIngredientResponse)
		);
	}
	
	@Transactional
	public ChangeFridgeIngredientResponse updatePurchasedDate(String userId, ChangeFridgeIngredientRequest request) {
		Fridge fridge = findFridge(userId);
		
		FridgeIngredient fridgeIngredient = fIngredientRepository.findByIdAndFridge(request.getIngredientId(), fridge)
				.orElseThrow(() -> new IllegalArgumentException("해당하는 아이디로 재료 찾을 수 없습니다."));
		
		fridgeIngredient.setPurchaseDate(request.getPurchasedDate());
		
		return new ChangeFridgeIngredientResponse(fridgeIngredient, "업데이트 완료");	
	}
	
	@Transactional
	public void deleteIngredient(String userId, Long fridgeIngredientId) {
        Fridge fridge = findFridge(userId);
        
		FridgeIngredient fridgeIngredient = fIngredientRepository
                .findByIdAndFridge(fridgeIngredientId, fridge)
				.orElseThrow(() -> new IllegalArgumentException("삭제 권한이 없거나 존재하지 않는 재료입니다."));
		
		fIngredientRepository.delete(fridgeIngredient);
	}


	private Fridge findFridge(String userId) {
		return fRepository.findByNameAndUser_UserId("나의 냉장고", userId)
				.orElseThrow(() -> new IllegalArgumentException("접근 권한이 없거나 존재하지 않는 냉장고입니다."));
	}
	
	private LocalDateTime calculateExpireDate(LocalDateTime purchaseDate, int avgDays, LocalDateTime userExpiredDate) {
		if (userExpiredDate != null) {
			return userExpiredDate;
		}
		return purchaseDate.plusDays(avgDays);
	}

	private List<FridgeIngredientResponse> toResponseList(List<FridgeIngredient> ingredients) {
		return ingredients.stream()
				.map(this::toFridgeIngredientResponse)
				.toList();
	}

	private FridgeIngredientResponse toFridgeIngredientResponse(FridgeIngredient fi) {
		LocalDateTime expiredDate = fi.getExpiredDate(); 
		
		long daysLeft = 0;
		if (expiredDate != null) {
			Duration duration = Duration.between(LocalDateTime.now(), expiredDate);
			daysLeft = duration.toDays();
		}

		return new FridgeIngredientResponse(
				fi.getId(),
				fi.getIngredient().getCategory(),
				fi.getIngredient().getName(),
				fi.getPurchaseDate(),
				expiredDate,
				daysLeft
		);
	}
}
package com.webservice.algorithmchef.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.webservice.algorithmchef.dto.ingredient.IngredientResponse;
import com.webservice.algorithmchef.model.Ingredient;
import com.webservice.algorithmchef.repository.IngredientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredientService {

	private final IngredientRepository ingredientRepository;
	
	public Page<IngredientResponse> retrieveAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Ingredient> ingredients = ingredientRepository.findAll(pageable);
		Page<IngredientResponse> ingredientResponses = ingredients.
				map(IngredientResponse::new);
		return ingredientResponses;
	}
	
	public Page<IngredientResponse> filterByName(String name,int page,int size){
		Pageable pageable = PageRequest.of(page, size);
		Page<Ingredient> filteredIngredients = ingredientRepository.findByNameContaining(name, pageable);
		Page<IngredientResponse> ingredientResponses = filteredIngredients.
				map(IngredientResponse::new);
		
		return ingredientResponses;
	}
	
	public Page<IngredientResponse> filterByCategory(String category,int page,int size){
		Pageable pageable = PageRequest.of(page, size);
		Page<Ingredient> filteredIngredients = ingredientRepository.findByCategory(category, pageable);
		Page<IngredientResponse> ingredientResponses = filteredIngredients.
				map(IngredientResponse::new);
		return ingredientResponses;
	}
}

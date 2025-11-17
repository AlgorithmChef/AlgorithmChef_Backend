package com.webservice.algorithmchef.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webservice.algorithmchef.dto.ingredient.IngredientResponse;
import com.webservice.algorithmchef.service.IngredientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class IngredientController {
	
	private final IngredientService ingredientService;
	
	@GetMapping("/ingredients")
	public ResponseEntity<?> retrieveAll(@RequestParam(value="page",defaultValue = "0") int page,
			@RequestParam(value="size",defaultValue = "10") int size,
			@RequestParam(value="name",required = false)String name,
			@RequestParam(value="category",required = false)String category){
		try {
			Page<IngredientResponse> response = null;
			if(category != null) {
				response = ingredientService.filterByCategory(category,page, size);
			}else if(name != null) {
				response = ingredientService.filterByName(name, page, size);
			}else {
				response = ingredientService.retrieveAll(page, size);				
			}
			return ResponseEntity.ok(response);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}

}

package com.webservice.algorithmchef.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.webservice.algorithmchef.model.Fridge;
import com.webservice.algorithmchef.model.FridgeIngredient;

public interface FridgeIngredientRepository extends JpaRepository<FridgeIngredient, Long> {

	Page<FridgeIngredient> findByFridge(Fridge fridge,Pageable pageable);
	Page<FridgeIngredient> findByFridgeAndIngredient_NameContaining(Fridge fridge,String name,Pageable pageable);
	Page<FridgeIngredient> findByFridgeAndIngredient_Category(Fridge fridge,String category,Pageable pageable);
	Optional<FridgeIngredient> findByIdAndFridge(Long id, Fridge fridge);
}

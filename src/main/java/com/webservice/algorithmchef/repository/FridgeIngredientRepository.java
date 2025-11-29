package com.webservice.algorithmchef.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.webservice.algorithmchef.model.Fridge;
import com.webservice.algorithmchef.model.FridgeIngredient;

public interface FridgeIngredientRepository extends JpaRepository<FridgeIngredient, Long> {

	List<FridgeIngredient> findByFridge(Fridge fridge,Sort sort);
	//Page<FridgeIngredient> findByFridgeAndIngredient_NameContaining(Fridge fridge,String name,Pageable pageable);
	List<FridgeIngredient> findByFridgeAndIngredient_Category(Fridge fridge,String category,Sort sort);
	Optional<FridgeIngredient> findByIdAndFridge(Long id, Fridge fridge);
}

package com.webservice.algorithmchef.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webservice.algorithmchef.model.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

	Optional<Ingredient> findByName(String name);
}

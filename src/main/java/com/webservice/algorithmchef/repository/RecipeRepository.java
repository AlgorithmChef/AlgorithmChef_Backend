package com.webservice.algorithmchef.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webservice.algorithmchef.model.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long>{

	public Optional<Recipe> findByName(String name);
}

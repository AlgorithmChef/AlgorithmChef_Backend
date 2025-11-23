package com.webservice.algorithmchef.repository;

import com.webservice.algorithmchef.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByName(String name);

    boolean existsByName(String name);

    List<Recipe> findByNeededIngredientsContaining(String ingredient);
}

package com.webservice.algorithmchef.repository;

import com.webservice.algorithmchef.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByName(String name);

    boolean existsByName(String name);
}

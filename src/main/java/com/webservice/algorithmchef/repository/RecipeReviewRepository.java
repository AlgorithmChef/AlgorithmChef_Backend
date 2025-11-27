package com.webservice.algorithmchef.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webservice.algorithmchef.model.Recipe;
import com.webservice.algorithmchef.model.RecipeReview;
import com.webservice.algorithmchef.model.User;

public interface RecipeReviewRepository extends JpaRepository<RecipeReview, Long> {

    boolean existsByUserAndRecipe(User user, Recipe recipe);
}

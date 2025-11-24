package com.webservice.algorithmchef.service;

import com.webservice.algorithmchef.model.Recipe;
import com.webservice.algorithmchef.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final Random random = new Random();

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe getRandomRecipeByIngredient(String ingredient) {
        List<Recipe> list = recipeRepository.findByIngredientKeyword(ingredient);

        if (list.isEmpty()) {
            return null;
        }

        int idx = (int) (Math.random() * list.size());
        return list.get(idx);
    }

    public Recipe getRandomRecipeByIngredients(List<String> ingredients) {

        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("재료 리스트가 필요합니다.");
        }

        String i1 = ingredients.size() > 0 ? ingredients.get(0) : "";
        String i2 = ingredients.size() > 1 ? ingredients.get(1) : "";
        String i3 = ingredients.size() > 2 ? ingredients.get(2) : "";
        String i4 = ingredients.size() > 3 ? ingredients.get(3) : "";
        String i5 = ingredients.size() > 4 ? ingredients.get(4) : "";

        List<Recipe> list =
                recipeRepository.findByIngredientsMulti(i1, i2, i3, i4, i5);

        if (list.isEmpty()) {
            return null;
        }

        return list.get(random.nextInt(list.size()));
    }
}

package com.webservice.algorithmchef.controller;

import com.webservice.algorithmchef.model.Recipe;
import com.webservice.algorithmchef.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
//@CrossOrigin(origins = "http://localhost:3000")
public class RecipeSearchController {

    private final RecipeService recipeService;

    public RecipeSearchController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> getRandomRecipe(@RequestParam("ingredient") String ingredient) {

        Recipe recipe = recipeService.getRandomRecipeByIngredient(ingredient);

        if (recipe == null) {
            return ResponseEntity.ok("검색된 레시피가 없습니다.");
        }

        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/search-multi")
    public ResponseEntity<?> getRandomRecipeMultiple(@RequestParam("ingredients") String ingredients) {

        List<String> ingList =
                Arrays.stream(ingredients.split(","))
                        .map(String::trim)
                        .toList();

        Recipe recipe = recipeService.getRandomRecipeByIngredients(ingList);

        if (recipe == null) {
            return ResponseEntity.ok("해당 재료들을 모두 포함한 레시피가 없습니다.");
        }

        return ResponseEntity.ok(recipe);
    }
}

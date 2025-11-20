package com.webservice.algorithmchef.service;

import com.webservice.algorithmchef.client.FoodSafetyApiClient;
import com.webservice.algorithmchef.dto.recipe.CookRcpResponse;
import com.webservice.algorithmchef.model.Recipe;
import com.webservice.algorithmchef.model.RecipeIngredient;
import com.webservice.algorithmchef.model.Ingredient;
import com.webservice.algorithmchef.repository.RecipeRepository;
import com.webservice.algorithmchef.repository.RecipeIngredientRepository;
import com.webservice.algorithmchef.repository.IngredientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class RecipeImportService {

    private final FoodSafetyApiClient apiClient;
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeImportService(FoodSafetyApiClient apiClient,
                               RecipeRepository recipeRepository,
                               RecipeIngredientRepository recipeIngredientRepository,
                               IngredientRepository ingredientRepository) {
        this.apiClient = apiClient;
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public void importAllFromFoodSafety() {

        System.out.println("[IMPORT] foodsafetycountry total recipe api start");

        final int BATCH_SIZE = 100;
        int start = 1;
        int totalSaved = 0;

        while (true) {
            int end = start + BATCH_SIZE - 1;

            System.out.println("[IMPORT] fetchRange(" + start + ", " + end + ")");

            List<CookRcpResponse.Item> items;
            try {
                items = apiClient.fetchRange(start, end);
            } catch (Exception e) {
                System.out.println("[IMPORT] fetchRange exception, import stop");
                e.printStackTrace();
                break;
            }

            if (items == null || items.isEmpty()) {
                System.out.println("[IMPORT] " + start + " ~ " + end + " this panel No recipe. import stop");
                break;
            }

            System.out.println("[IMPORT] received count = " + items.size());

            for (CookRcpResponse.Item item : items) {

                String name = item.getName();
                System.out.println("  - API recipe name = " + name);

                if (name == null || name.isBlank()) {
                    System.out.println("    -> No name, skip");
                    continue;
                }

                if (recipeRepository.existsByName(name)) {
                    System.out.println("    -> exist recipe, skip");
                    continue;
                }


                Recipe r = new Recipe();
                r.setName(name);
                r.setDescription(null);
                r.setInstructions(item.getInstructions());
                r.setImageUrl(item.getImageUrl());
                r.setKcal(item.getKcal());
                String type = item.getType();
                if (type == null || type.isBlank()) {
                    type = "기타";
                }
                r.setType(type);


                recipeRepository.save(r);


                List<RecipeIngredient> recipeIngredients = new ArrayList<>();
                for (String ingredientWithUnit : item.getParts()) {
                    String ingredientName = extractIngredientName(ingredientWithUnit);
                    if (ingredientName == null || ingredientName.isBlank()) {
                        continue;
                    }


                    Ingredient ingredient = findMatchingIngredient(ingredientName.trim());
                    if (ingredient == null) {
                        System.out.println("Ingredient not found for: " + ingredientName);
                    }

                    RecipeIngredient recipeIngredient = new RecipeIngredient();
                    recipeIngredient.setRecipe(r);
                    recipeIngredient.setNeededIngredients(ingredientWithUnit.trim());
                    recipeIngredient.setIngredient(ingredient);

                    recipeIngredients.add(recipeIngredient);
                }

                // Save RecipeIngredients to DB
                recipeIngredientRepository.saveAll(recipeIngredients);
                totalSaved++;
                System.out.println("    -> DB save complete (current new save: " + totalSaved + ")");
            }

            start += BATCH_SIZE;
        }

        System.out.println("[IMPORT] every import complete. A total " + totalSaved + " new saved");
    }

    private String extractIngredientName(String ingredientWithUnit) {
        String ingredientName = ingredientWithUnit.replaceAll("\\d+\\s*[a-zA-Z가-힣]+", "").trim();
        return ingredientName.isEmpty() ? null : ingredientName;
    }

    private Ingredient findMatchingIngredient(String ingredientName) {
        ingredientName = ingredientName.trim();

        Ingredient ingredient = ingredientRepository.findByName(ingredientName).orElse(null);
        if (ingredient == null) {
            Pageable pageable = PageRequest.of(0, 10);

            Page<Ingredient> ingredientsPage = ingredientRepository.findByNameContaining(ingredientName, pageable);

            if (!ingredientsPage.isEmpty()) {
                ingredient = ingredientsPage.getContent().get(0);
            }
        }

        return ingredient;
    }

}

package com.webservice.algorithmchef.service;

import com.webservice.algorithmchef.client.FoodSafetyApiClient;
import com.webservice.algorithmchef.dto.recipe.CookRcpResponse;
import com.webservice.algorithmchef.model.Recipe;
import com.webservice.algorithmchef.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeImportService {

    private final FoodSafetyApiClient apiClient;
    private final RecipeRepository recipeRepository;

    public RecipeImportService(FoodSafetyApiClient apiClient,
                               RecipeRepository recipeRepository) {
        this.apiClient = apiClient;
        this.recipeRepository = recipeRepository;
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

                // description은 당분간 null 유지
                r.setDescription(null);

                r.setInstructions(item.getInstructions());
                r.setImageUrl(item.getImageUrl());

                // kcal이 null일 수 있으니까 방어 코드
                Double kcal = item.getKcalOrNull();
                r.setKcal(kcal != null ? kcal : 0.0);

                String type = item.getType();
                if (type == null || type.isBlank()) {
                    type = "기타";
                }
                r.setType(type);

                // ✅ 재료 전체 문자열 그대로 저장
                r.setNeededIngredients(item.getParts());

                recipeRepository.save(r);
                totalSaved++;
                System.out.println("    -> DB save complete (current new save: " + totalSaved + ")");
            }

            start += BATCH_SIZE;
        }

        System.out.println("[IMPORT] every import complete. A total " + totalSaved + " new saved");
    }

}

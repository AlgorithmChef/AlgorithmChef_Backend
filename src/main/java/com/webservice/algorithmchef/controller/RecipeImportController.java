package com.webservice.algorithmchef.controller;

import com.webservice.algorithmchef.service.RecipeImportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class RecipeImportController {

    private final RecipeImportService recipeImportService;

    public RecipeImportController(RecipeImportService recipeImportService) {
        this.recipeImportService = recipeImportService;
    }

    @GetMapping("/import/foodsafety")
    public String importFromFoodSafety() {
        recipeImportService.importAllFromFoodSafety();
        return "import started";
    }
}

package com.webservice.algorithmchef.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RecipeDataInitializer implements CommandLineRunner {

    private final RecipeImportService importService;

    public RecipeDataInitializer(RecipeImportService importService) {
        this.importService = importService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("[INIT] import start");
        importService.importAllFromFoodSafety();
        System.out.println("[INIT] import complete");
    }
}

package com.webservice.algorithmchef.dto.gemini;

import java.util.List;

public record GeminiRecipeResponse (
    String name,
    String description,
    String kcal,
    String portions,
    String time,
    List<String> ingredients,
    List<String> instructions,
    String tip,
    String type
) {}

package com.webservice.algorithmchef.dto.gemini;

import java.util.List;

public record GeminiRecipeResponse (
    String name,
    String description,
    double kcal,
    String portions,
    String time,
    String imageUrl,
    String ingredients,
    String instructions,
    String tip,
    String type
) {}

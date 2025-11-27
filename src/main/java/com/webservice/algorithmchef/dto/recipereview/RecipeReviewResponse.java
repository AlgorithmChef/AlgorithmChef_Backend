package com.webservice.algorithmchef.dto.recipereview;

import com.webservice.algorithmchef.model.RecipeReview;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecipeReviewResponse {

    private String name;
    private String userId;
    private float rating;

    public RecipeReviewResponse(RecipeReview review) {
        this.name = review.getRecipe().getName();
        this.userId = review.getUser().getUserId();
        this.rating = review.getRating();
    }
}

package com.webservice.algorithmchef.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webservice.algorithmchef.dto.recipereview.RecipeReviewRequest;
import com.webservice.algorithmchef.dto.recipereview.RecipeReviewResponse;
import com.webservice.algorithmchef.model.Recipe;
import com.webservice.algorithmchef.model.RecipeReview;
import com.webservice.algorithmchef.model.User;
import com.webservice.algorithmchef.repository.RecipeRepository;
import com.webservice.algorithmchef.repository.RecipeReviewRepository;
import com.webservice.algorithmchef.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeReviewService {

    private final RecipeRepository recipeRepository;
    private final RecipeReviewRepository recipeReviewRepository;
    private final UserRepository userRepository;


    @Transactional
    public RecipeReviewResponse makeReview(RecipeReviewRequest reviewRequest, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(()-> new IllegalArgumentException("해당하는 아이디의 사용작 없습니다."));
        String recipeName = reviewRequest.getName();
        float rating = reviewRequest.getRating();
        String content = reviewRequest.getContent();
        Recipe recipe = recipeRepository.findByName(recipeName)
                .orElseThrow(()-> new IllegalArgumentException("해당하는 이름의 recipe가 존재하지 않습니다."));
        if (recipeReviewRepository.existsByUserAndRecipe(user, recipe)) {
            throw new IllegalStateException("이미 리뷰를 작성한 레시피입니다.");
        }
        RecipeReview review = RecipeReview.builder()
                .content(content)
                .rating(rating)
                .user(user)
                .recipe(recipe)
                .build();

        RecipeReview recipeReview = recipeReviewRepository.save(review);
        return new RecipeReviewResponse(recipeReview);

    }

}

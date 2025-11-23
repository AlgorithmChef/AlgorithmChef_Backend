package com.webservice.algorithmchef.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.webservice.algorithmchef.dto.recipereview.RecipeReviewRequest;
import com.webservice.algorithmchef.dto.recipereview.RecipeReviewResponse;
import com.webservice.algorithmchef.service.RecipeReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RecipeReviewController {

    private final RecipeReviewService rService;

    @PostMapping("/recipe/review")
    public ResponseEntity<?> makeReview(@RequestBody RecipeReviewRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails){
        try {
            String userId = userDetails.getUsername();
            RecipeReviewResponse response = rService.makeReview(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
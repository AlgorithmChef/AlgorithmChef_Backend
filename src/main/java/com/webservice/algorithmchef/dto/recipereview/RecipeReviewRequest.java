package com.webservice.algorithmchef.dto.recipereview;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecipeReviewRequest {

	private String name;
	private float rating;
	private String content;
}

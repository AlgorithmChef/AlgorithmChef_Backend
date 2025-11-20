package com.webservice.algorithmchef.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="recipe_id")
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String name;
	
	@Lob
	private String description;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String instructions;

    private String imageUrl;
	
	@Column(nullable = false)
	private String type;
	
	@Column(nullable = false)
	private double kcal;
	
	@OneToMany(mappedBy = "recipe", orphanRemoval = true)
	private List<RecipeTag> recipeTags;
	
	@OneToMany(mappedBy = "recipe", orphanRemoval = true)
	private List<RecipeIngredient> recipeIngredients;
	
	@OneToMany(mappedBy = "recipe", orphanRemoval = true)
	private List<RecipeReview> recipeReviews;
	
}

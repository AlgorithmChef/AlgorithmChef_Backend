package com.webservice.algorithmchef.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
public class RecipeIngredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="recipe_ingredient_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="recipe_id")
	private Recipe recipe;
	
	@Column(nullable = false)
	@Lob
	private String neededIngredients;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ingredient_id")
	private Ingredient ingredient;

}

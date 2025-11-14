package com.webservice.algorithmchef.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Ingredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ingredient_id")
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String category;
	
	@Column(nullable = false)
	private int avgExpiryDays;
	
	@Column(nullable = false)
	private String storageType;
	
	
	@OneToMany(mappedBy = "ingredient",orphanRemoval = true,cascade = CascadeType.ALL)
	private List<UserFridge> userFridges;
	
	@OneToMany(mappedBy = "ingredient",orphanRemoval = true,cascade = CascadeType.ALL)
	private List<RecipeIngredient> recipeIngredients;
	
}

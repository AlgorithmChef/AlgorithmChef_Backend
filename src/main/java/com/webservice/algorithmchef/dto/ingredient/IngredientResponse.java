package com.webservice.algorithmchef.dto.ingredient;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponse {

	private Long id;
	private  String category;
	private String name;
	private LocalDateTime purchaseDate;
	private LocalDateTime expiredDate;
}

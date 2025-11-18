package com.webservice.algorithmchef.dto.fridgeingredient;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FridgeIngredientResponse {

	private Long id;
	private  String category;
	private String name;
	private LocalDateTime purchaseDate;
	private LocalDateTime expiredDate;
	private Long dDay;
}
	
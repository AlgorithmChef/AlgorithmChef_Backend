package com.webservice.algorithmchef.dto.fridgeingredient;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FridgeIngredientUpdateItem {

    private Long ingredientId;
    private String category;
    private String name;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiredDate;
}

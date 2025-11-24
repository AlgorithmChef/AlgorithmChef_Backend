package com.webservice.algorithmchef.repository;

import com.webservice.algorithmchef.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByName(String name);

    boolean existsByName(String name);

    @Query("""
        SELECT r FROM Recipe r
        WHERE (:i1 = '' OR r.neededIngredients LIKE %:i1%)
          AND (:i2 = '' OR r.neededIngredients LIKE %:i2%)
          AND (:i3 = '' OR r.neededIngredients LIKE %:i3%)
          AND (:i4 = '' OR r.neededIngredients LIKE %:i4%)
          AND (:i5 = '' OR r.neededIngredients LIKE %:i5%)
        """)
    List<Recipe> findByIngredientsMulti(
            @Param("i1") String i1,
            @Param("i2") String i2,
            @Param("i3") String i3,
            @Param("i4") String i4,
            @Param("i5") String i5
    );

    @Query("SELECT r FROM Recipe r " +
            "WHERE r.neededIngredients LIKE %:ingredient%")
    List<Recipe> findByIngredientKeyword(@Param("ingredient") String ingredient);
}

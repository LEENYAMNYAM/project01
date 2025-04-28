package com.example.pro.repository;

import com.example.pro.entity.RecipeIngredientsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredientsEntity, Long> {
}

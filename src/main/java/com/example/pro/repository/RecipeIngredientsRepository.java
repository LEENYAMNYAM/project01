package com.example.pro.repository;

import com.example.pro.entity.RecipeIngredientsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeIngredientsRepository extends JpaRepository<RecipeIngredientsEntity, Long> {
    List<RecipeIngredientsEntity> findByRecipeEntity_Id(Long recipeId);
    void deleteByRecipeEntity_Id(Long recipeId);
}

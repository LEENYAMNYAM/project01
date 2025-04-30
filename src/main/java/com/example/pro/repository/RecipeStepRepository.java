package com.example.pro.repository;

import com.example.pro.entity.RecipeIngredientsEntity;
import com.example.pro.entity.RecipeStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStepEntity, Long> {
    List<RecipeStepEntity> findByRecipeEntity_Id(Long recipeId);
    void deleteByRecipeEntity_id(Long recipeId);
}

package com.example.pro.repository;

import com.example.pro.entity.RecipeEntity;
import com.example.pro.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByRecipe(RecipeEntity recipe);
    List<ReviewEntity> findByRecipeId(Long recipeId);
}

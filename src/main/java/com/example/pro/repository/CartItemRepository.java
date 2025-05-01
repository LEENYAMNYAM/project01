package com.example.pro.repository;

import com.example.pro.entity.CartEntity;
import com.example.pro.entity.CartItemEntity;
import com.example.pro.entity.IngredientEntity;
import com.example.pro.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByCartEntity(CartEntity cartEntity);
    
    Optional<CartItemEntity> findByCartEntityAndIngredientEntity(CartEntity cartEntity, IngredientEntity ingredientEntity);
    
    Optional<CartItemEntity> findByCartEntityAndIngredientEntityAndRecipeEntity(
            CartEntity cartEntity, 
            IngredientEntity ingredientEntity,
            RecipeEntity recipeEntity);
    
    void deleteByCartEntity(CartEntity cartEntity);
}
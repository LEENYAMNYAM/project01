package com.example.pro.service;


import com.example.pro.dto.IngredientDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.entity.IngredientEntity;

import java.util.List;

public interface IngredientService {
    List<RecipeIngredientsDTO> findAllIngredient();

    default IngredientEntity dtoToEntity(IngredientDTO ingredientDTO) {

        IngredientEntity ingredient = new IngredientEntity();
        ingredient.setId(ingredientDTO.getId());
        ingredient.setIngredientName(ingredientDTO.getIngredientName());
        ingredient.setProductId(ingredientDTO.getProductId());
        ingredient.setPrice(ingredientDTO.getPrice());
        ingredient.setMallLink(ingredientDTO.getMallLink());
        ingredient.setMallName(ingredientDTO.getMallName());
        ingredient.setImageUrl(ingredientDTO.getImageUrl());
        return ingredient;
    }

    default IngredientDTO entityToDto(IngredientEntity ingredientEntity) {

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setId(ingredientEntity.getId());
        ingredientDTO.setIngredientName(ingredientEntity.getIngredientName());
        ingredientDTO.setProductId(ingredientEntity.getProductId());
        ingredientDTO.setPrice(ingredientEntity.getPrice());
        ingredientDTO.setMallLink(ingredientEntity.getMallLink());
        ingredientDTO.setMallName(ingredientEntity.getMallName());
        ingredientDTO.setImageUrl(ingredientEntity.getImageUrl());

        return ingredientDTO;
    }



}

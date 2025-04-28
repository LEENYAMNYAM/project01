package com.example.pro.service;


import com.example.pro.dto.IngredientDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.entity.IngredientEntity;

import java.util.List;

public interface IngredientService {
    List<IngredientDTO> findAllIngredient();

    default IngredientEntity dtoToEntity(IngredientDTO ingredientDTO) {

        IngredientEntity ingredient = new IngredientEntity();
        ingredient.setId(ingredientDTO.getId());
        ingredient.setIngredientName(ingredientDTO.getIngredientName());
        ingredient.setDetail(ingredientDTO.getDetail());
        ingredient.setPrice(ingredientDTO.getPrice());
        ingredient.setImageUrl(ingredientDTO.getImageUrl());
        return ingredient;
    }

    default IngredientDTO entityToDto(IngredientEntity ingredientEntity) {

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setId(ingredientEntity.getId());
        ingredientDTO.setIngredientName(ingredientEntity.getIngredientName());
        ingredientDTO.setDetail(ingredientEntity.getDetail());
        ingredientDTO.setPrice(ingredientEntity.getPrice());
        ingredientDTO.setImageUrl(ingredientEntity.getImageUrl());

        return ingredientDTO;
    }



}

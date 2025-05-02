package com.example.pro.service;

import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.entity.RecipeIngredientsEntity;
import com.example.pro.repository.CartRepository;
import com.example.pro.repository.IngredientRepository;
import com.example.pro.repository.RecipeIngredientsRepository;
import com.example.pro.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RecipeIngredientsServiceImpl implements RecipeIngredientsService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientsRepository recipeIngredientsRepository;
    private final IngredientServiceImpl ingredientServiceImpl;
    private final CartRepository cartRepository;

    @Override
    public List<RecipeIngredientsDTO> getRecipeIngredientsbyRecipeId(Long recipeId) {
        List<RecipeIngredientsEntity> recipeIngredientsEntityList = recipeIngredientsRepository.findByRecipeEntity_Id(recipeId);

        return recipeIngredientsEntityList.stream()
                .map(this::entityToDto)  // entityToDto 호출 시 null 체크 포함
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRecipeIngredient(Long id) {
        recipeIngredientsRepository.deleteById(id);
    }

    RecipeIngredientsEntity dtoToEntity(RecipeIngredientsDTO recipeIngredientsDTO) {
        RecipeIngredientsEntity recipeIngredientsEntity = new RecipeIngredientsEntity();
        recipeIngredientsEntity.setId(recipeIngredientsDTO.getId());
        recipeIngredientsEntity.setRecipeEntity(recipeRepository.findById(recipeIngredientsDTO.getRecipeId()).get());
        recipeIngredientsEntity.setIngredientEntity(ingredientServiceImpl.dtoToEntity(recipeIngredientsDTO.getIngredient()));
        recipeIngredientsEntity.setQuantity(recipeIngredientsDTO.getQuantity());

        // CartEntity가 null일 수 있으므로, null 체크 후 처리
        if (recipeIngredientsDTO.getCartId() != null) {
            recipeIngredientsEntity.setCartEntity(cartRepository.findById(recipeIngredientsDTO.getCartId()).get());
        } else {
            recipeIngredientsEntity.setCartEntity(null);
        }

        return recipeIngredientsEntity;
    }

    RecipeIngredientsDTO entityToDto(RecipeIngredientsEntity recipeIngredientsEntity) {
        RecipeIngredientsDTO recipeIngredientsDTO = new RecipeIngredientsDTO();
        recipeIngredientsDTO.setId(recipeIngredientsEntity.getId());
        recipeIngredientsDTO.setRecipeId(recipeIngredientsEntity.getRecipeEntity().getId());
        recipeIngredientsDTO.setIngredient(ingredientServiceImpl.entityToDto(recipeIngredientsEntity.getIngredientEntity()));
        recipeIngredientsDTO.setQuantity(recipeIngredientsEntity.getQuantity());

        // getCartEntity()가 null인 경우에만 null 처리
        if (recipeIngredientsEntity.getCartEntity() != null) {
            recipeIngredientsDTO.setCartId(recipeIngredientsEntity.getCartEntity().getId());
        } else {
            recipeIngredientsDTO.setCartId(null);
        }

        return recipeIngredientsDTO;
    }
}

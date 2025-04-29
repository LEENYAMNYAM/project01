package com.example.pro.service;

import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.entity.RecipeIngredientsEntity;
import com.example.pro.repository.IngredientRepository;
import com.example.pro.repository.RecipeIngredientsRepository;
import com.example.pro.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RecipeIngredientsServiceImpl implements RecipeIngredientsService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientsRepository recipeIngredientsRepository;
    private final IngredientServiceImpl ingredientServiceImpl;

    @Override
    public List<RecipeIngredientsDTO> getRecipeIngredientsbyRecipeId(Long recipeId) {
        List<RecipeIngredientsEntity> recipeIngredientsEntityList = recipeIngredientsRepository.findByRecipeEntity_Id(recipeId);

        List<RecipeIngredientsDTO> recipeIngredientsDTOList = recipeIngredientsEntityList.stream()
                .map(recipeIngredientsEntity -> entityToDto(recipeIngredientsEntity))
                .collect(Collectors.toList());
        return recipeIngredientsDTOList;
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
        return recipeIngredientsEntity;
    }

    RecipeIngredientsDTO entityToDto(RecipeIngredientsEntity recipeIngredientsEntity) {
        RecipeIngredientsDTO recipeIngredientsDTO = new RecipeIngredientsDTO();
        recipeIngredientsDTO.setId(recipeIngredientsEntity.getId());
        recipeIngredientsDTO.setRecipeId(recipeIngredientsEntity.getRecipeEntity().getId());
        recipeIngredientsDTO.setIngredient(ingredientServiceImpl.entityToDto(recipeIngredientsEntity.getIngredientEntity()));
        recipeIngredientsDTO.setQuantity(recipeIngredientsEntity.getQuantity());
        return recipeIngredientsDTO;
    }





}

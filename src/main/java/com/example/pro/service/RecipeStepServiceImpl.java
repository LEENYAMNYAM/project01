package com.example.pro.service;

import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.dto.RecipeStepDTO;
import com.example.pro.entity.RecipeIngredientsEntity;
import com.example.pro.entity.RecipeStepEntity;
import com.example.pro.repository.RecipeRepository;
import com.example.pro.repository.RecipeStepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RecipeStepServiceImpl implements RecipeStepService{

    private final RecipeStepRepository recipeStepRepository;
    private final RecipeRepository recipeRepository;

    @Override
    public void registerRecipeStep(Long recipeId, String step) {

    }

    @Override
    public List<RecipeStepDTO> getRecipeSteps() {
        return List.of();
    }

    @Override
    public List<RecipeStepDTO> getRecipeStepByRecipeId(Long recipeId) {
        List<RecipeStepEntity> recipeStepEntityList = recipeStepRepository.findByRecipe_Id(recipeId);

        List<RecipeStepDTO> recipeStepDTOList = recipeStepEntityList.stream()
                .map(recipeStepEntity -> entityToDto(recipeStepEntity))
                .collect(Collectors.toList());

        return recipeStepDTOList;
    }

    @Override
    public void updateRecipeStep(Long recipeId, Long stepId, String step) {

    }

    @Override
    public void deleteRecipeStep(Long recipeId, Long stepId) {

    }


    RecipeStepEntity dtoToEntity(RecipeStepDTO recipeStepDTO) {
        RecipeStepEntity recipeStepEntity = new RecipeStepEntity();
        recipeStepEntity.setId(recipeStepDTO.getId());
        recipeStepEntity.setRecipe( recipeRepository.findById(recipeStepDTO.getRecipeId()).get());
        recipeStepEntity.setStepNumber(recipeStepDTO.getStepNumber());
        recipeStepEntity.setContent(recipeStepDTO.getContent());
        recipeStepEntity.setImageName(recipeStepDTO.getImagePath());
        return recipeStepEntity;
    }

    RecipeStepDTO entityToDto(RecipeStepEntity recipeStepEntity) {
        RecipeStepDTO recipeStepDTO = new RecipeStepDTO();
        recipeStepDTO.setId(recipeStepEntity.getId());
        recipeStepDTO.setRecipeId(recipeStepEntity.getRecipe().getId());
        recipeStepDTO.setStepNumber(recipeStepEntity.getStepNumber());
        recipeStepDTO.setContent(recipeStepEntity.getContent());
        recipeStepDTO.setImagePath(recipeStepEntity.getImageName());
        return recipeStepDTO;
    }



}

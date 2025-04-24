package com.example.pro.service;

import com.example.pro.dto.RecipeStepDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class RecipeStepServiceImpl implements RecipeStepService{
    @Override
    public void registerRecipeStep(Long recipeId, String step) {

    }

    @Override
    public List<RecipeStepDTO> getRecipeSteps() {
        return List.of();
    }

    @Override
    public List<RecipeStepDTO> getRecipeStepByRecipeId(Long recipeId) {
        return List.of();
    }

    @Override
    public void updateRecipeStep(Long recipeId, Long stepId, String step) {

    }

    @Override
    public void deleteRecipeStep(Long recipeId, Long stepId) {

    }
}

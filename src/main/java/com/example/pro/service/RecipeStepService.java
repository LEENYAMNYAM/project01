package com.example.pro.service;


import com.example.pro.dto.RecipeStepDTO;

import java.util.List;

public interface RecipeStepService {
    void registerRecipeStep(Long recipeId, String step);
    List<RecipeStepDTO> getRecipeSteps();
    List<RecipeStepDTO> getRecipeStepByRecipeId(Long recipeId);
    void updateRecipeStep(Long recipeId, Long stepId, String step);
    void deleteRecipeStep(Long recipeId, Long stepId);

}

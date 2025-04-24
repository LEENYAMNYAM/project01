package com.example.pro.service;

import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    void registerRecipe(RecipeDTO recipeDTO, List<MultipartFile> recipeStepImages, Map<String, String> paramMap);
    List<RecipeDTO> getAllRecipe();
    RecipeDTO getRecipeById(Long id);
    void updateRecipe(RecipeDTO recipeDTO, UserDTO userDTO);
    void deleteRecipe(Long id);


}

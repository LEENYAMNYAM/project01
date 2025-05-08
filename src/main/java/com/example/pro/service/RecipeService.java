package com.example.pro.service;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    void registerRecipe(RecipeDTO recipeDTO, List<MultipartFile> recipeStepImages, Map<String, String> paramMap);
    List<RecipeDTO> getAllRecipe(String sortBy);
    List<RecipeDTO> searchRecipeByTitle(String keyword);
    List<RecipeDTO> findByUsername(String username);
    List<RecipeDTO> findByCategory(String category, String sortBy);
    List<RecipeDTO> searchRecipes(String searchType, String keyword, String sortBy);
    List<RecipeDTO> searchByCategoryAndKeyword(String category, String searchType, String keyword, String sortBy);
    RecipeDTO getRecipeById(Long id);
    void updateRecipe(RecipeDTO recipeDTO);
    void deleteRecipe(Long id);
    public String getRecipeTitleById(Long id);
    public List<RecipeDTO> getRecentRecipes();
    public List<RecipeDTO> getTopRatedRecipes(int limit);

}

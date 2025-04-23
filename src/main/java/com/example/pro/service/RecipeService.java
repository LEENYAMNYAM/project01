package com.example.pro.service;

import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.UserDTO;
import com.example.pro.entity.RecipeEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface RecipeService {
    void insertRecipe(RecipeDTO recipeDTO, Principal principal, Map<String, String> paramMap);
    List<RecipeDTO> getAllRecipe();
    RecipeEntity getRecipeById(Long id);
    void updateRecipe(RecipeEntity recipeEntity, UserDTO userDTO);
    void deleteRecipe(Long id);

    default RecipeEntity dtoToEntity(RecipeDTO recipeDTO) {
        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setTitle(recipeDTO.getTitle());
        recipeEntity.setCategory(recipeDTO.getCategory());
        recipeEntity.setMainImage(recipeDTO.getMainImage());
        recipeEntity.setYoutubeLink(recipeDTO.getYoutubeLink());
        return recipeEntity;
    }

    default RecipeDTO entityToDto(RecipeEntity recipeEntity){
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(recipeEntity.getId());
        recipeDTO.setTitle(recipeEntity.getTitle());
        recipeDTO.setCategory(recipeEntity.getCategory());
        recipeDTO.setMainImage(recipeEntity.getMainImage());
        recipeDTO.setYoutubeLink(recipeEntity.getYoutubeLink());
        recipeDTO.setUsername(recipeEntity.getUser().getUsername());
        recipeDTO.setCreatedAt(recipeEntity.getCreatedAt());
        recipeDTO.setLikeCount(recipeEntity.getLikeCount());
        return recipeDTO;
    }


}

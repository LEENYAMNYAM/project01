package com.example.pro.service;

import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.UserDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    @Override
    public void insertRecipe(RecipeDTO recipeDTO, @AuthenticationPrincipal Principal principal, Map<String, String> paramMap) {

    }

    @Override
    public List<RecipeDTO> getAllRecipe() {
           List<RecipeEntity> recipeEntityList = recipeRepository.findAll();
        List<RecipeDTO> recipeList = recipeEntityList.stream().map( recipe -> entityToDto(recipe))
                .collect(Collectors.toList());
        return recipeList;
    }

    @Override
    public RecipeEntity getRecipeById(Long id) {
        return null;
    }

    @Override
    public void updateRecipe(RecipeEntity recipeEntity, UserDTO userDTO) {

    }

    @Override
    public void deleteRecipe(Long id) {

    }
}


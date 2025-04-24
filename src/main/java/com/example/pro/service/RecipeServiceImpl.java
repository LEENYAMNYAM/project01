package com.example.pro.service;

import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.UserDTO;
import com.example.pro.entity.*;
import com.example.pro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final FileService fileService;
    private final UserRepository userRepository;

    @Override
    public void registerRecipe(RecipeDTO recipeDTO, List<MultipartFile> recipeStepImages, Map<String, String> paramMap) {

        // 1. RecipeEntity 저장
        RecipeEntity recipeEntity = dtoToEntity(recipeDTO);
        recipeRepository.save(recipeEntity);

        // 2. 재료 저장
        int ingredientIndex = 0;
        while (true) {
            String nameKey = "ingredients[" + ingredientIndex + "].ingredientName";
            String qtyKey = "ingredients[" + ingredientIndex + "].quantity";
            if (!paramMap.containsKey(nameKey)) break;

            String name = paramMap.get(nameKey);
            Long quantity = Long.parseLong(paramMap.getOrDefault(qtyKey, "1"));

            // 재료가 DB에 없을때 예외 처리
            IngredientEntity ingredient = ingredientRepository.findByIngredientName(name)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 재료입니다: " + name));

            RecipeIngredientsEntity recipeIng = RecipeIngredientsEntity.builder()
                    .recipeEntity(recipeEntity)
                    .ingredientEntity(ingredient)
                    .quantity(quantity)
                    .build();

            recipeIngredientRepository.save(recipeIng);
            ingredientIndex++;
        }

        // 3. 요리 순서 저장
        int stepIndex = 1; // steps[0]은 대표 이미지니까
        for (MultipartFile stepImage : recipeStepImages) {
            String contentKey = "steps[" + stepIndex + "].stepContent";
            if (!paramMap.containsKey(contentKey)) break;

            String content = paramMap.get(contentKey);
            String imageName = null;

            if (!stepImage.isEmpty()) {
                imageName = fileService.saveFile(stepImage);
            }

            RecipeStepEntity step = RecipeStepEntity.builder()
                    .recipe(recipeEntity)
                    .stepNumber(stepIndex)
                    .content(content)
                    .imageName(imageName)
                    .build();

            recipeStepRepository.save(step);
            stepIndex++;
        }

    }

    @Override
    public List<RecipeDTO> getAllRecipe() {
           List<RecipeEntity> recipeEntityList = recipeRepository.findAll();
        List<RecipeDTO> recipeListDTOs = recipeEntityList.stream()
                .map( recipeEntity -> entityToDto(recipeEntity))
                .collect(Collectors.toList());
        return recipeListDTOs;
    }

    @Override
    public RecipeDTO getRecipeById(Long id) {
        return null;
    }

    @Override
    public void updateRecipe(RecipeDTO recipeDTO, UserDTO userDTO) {

    }

    @Override
    public void deleteRecipe(Long id) {

    }


    RecipeEntity dtoToEntity(RecipeDTO recipeDTO) {
        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setTitle(recipeDTO.getTitle());
        recipeEntity.setCategory(recipeDTO.getCategory());
        recipeEntity.setYoutubeLink(recipeDTO.getYoutubeLink());
        recipeEntity.setUser(userRepository.findByUsername(recipeDTO.getUsername()).orElse(null));
        recipeEntity.setMainImage(recipeDTO.getMainImagePath());
        recipeEntity.setCreatedAt(LocalDateTime.now());
        recipeEntity.setLikeCount(recipeDTO.getLikeCount() != null ? recipeDTO.getLikeCount() : 0L);
        return recipeEntity;
    }

    RecipeDTO entityToDto(RecipeEntity recipeEntity){
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(recipeEntity.getId());
        recipeDTO.setTitle(recipeEntity.getTitle());
        recipeDTO.setCategory(recipeEntity.getCategory());
//        recipeDTO.setMainImage(recipeEntity.getMainImage());
        recipeDTO.setYoutubeLink(recipeEntity.getYoutubeLink());
        recipeDTO.setUsername(recipeEntity.getUser().getUsername());
        recipeDTO.setCreatedAt(recipeEntity.getCreatedAt());
        recipeDTO.setLikeCount(recipeEntity.getLikeCount());
        return recipeDTO;
    }



}


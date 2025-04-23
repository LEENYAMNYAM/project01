package com.example.pro.service;

import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.UserDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.entity.RecipeStepEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.RecipeRepository;
import com.example.pro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Override
    public void registerRecipe(RecipeDTO recipeDTO, String username) {
        // 사용자 조회
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        // DTO -> Entity 변환
        RecipeEntity recipeEntity = dtoToEntity(recipeDTO);
        recipeEntity.setMainImage(recipeDTO.getMainImageUrl());
        recipeEntity.setUser(user);
        recipeEntity.setCreatedAt(LocalDateTime.now());

        // Step DTO -> Step Entity 매핑
        if (recipeDTO.getSteps() != null) {
            List<RecipeStepEntity> stepEntities = recipeDTO.getSteps().stream().map(dto -> {
                RecipeStepEntity step = new RecipeStepEntity();
                step.setContent (dto.getContent());
                step.setImagename(dto.getImageName());
                step.setRecipe(recipeEntity);
                return step;
            }).collect(Collectors.toList());
            recipeEntity.setSteps(stepEntities);
        }

        recipeRepository.save(recipeEntity);
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


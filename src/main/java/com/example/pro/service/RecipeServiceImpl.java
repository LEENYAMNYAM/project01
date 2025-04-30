package com.example.pro.service;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.dto.RecipeStepDTO;
import com.example.pro.dto.UserDTO;
import com.example.pro.entity.*;
import com.example.pro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientsRepository recipeIngredientsRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final RecipeIngredientsServiceImpl recipeIngredientsServiceImpl;

    @Override
    public void registerRecipe(RecipeDTO recipeDTO, List<MultipartFile> recipeStepImages, Map<String, String> paramMap) {

        // 1. RecipeEntity 저장
        log.info("userRepository.findByUsername(recipeDTO.getUsername() : " + userRepository.findByUsername(recipeDTO.getUsername()));

        RecipeEntity recipeEntity = dtoToEntity(recipeDTO);

        UserEntity userEntity = userRepository.findByUsername(recipeDTO.getUsername()).orElseThrow(null);
        log.info("userEntity : " + userEntity);
        recipeEntity.setUser(userEntity);
        log.info("recipeEntity.getUser() : " + recipeEntity.getUser());
        log.info("recipeEntity.getUser().getUsername() : " + recipeEntity.getUser().getUsername());

        recipeRepository.save(recipeEntity);

        // 2. 재료 저장
        int ingredientIndex = 0;
        while (true) {
            String nameKey = "recipeIngredients[" + ingredientIndex + "].ingredient.ingredientName";
            String qtyKey = "recipeIngredients[" + ingredientIndex + "].quantity";
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

            recipeIngredientsRepository.save(recipeIng);
            ingredientIndex++;
        }

        // 3. 요리 순서 저장
        int stepIndex = 0;
        for (MultipartFile stepImage : recipeStepImages) {
            String contentKey = "steps[" + stepIndex + "].content";
            if (!paramMap.containsKey(contentKey)) break;

            String content = paramMap.get(contentKey);
            String imageName = null;

            if (!stepImage.isEmpty()) {
                imageName = fileService.saveFile(stepImage);
            }

            RecipeStepEntity step = RecipeStepEntity.builder()
                    .recipeEntity(recipeEntity)
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
//           log.info("recipeEntityList.getUser(): " + recipeEntityList.get(1).getUser().getUsername());
        List<RecipeDTO> recipeListDTOs = recipeEntityList.stream()
                .map( recipeEntity -> {
                    RecipeDTO recipeDTO = entityToDto(recipeEntity);
//                    log.info("recipeDTO.getUsername(): " + recipeDTO.getUsername());
                    return recipeDTO;
                })
                .collect(Collectors.toList());
        return recipeListDTOs;
    }

    @Override
    public RecipeDTO getRecipeById(Long id) {
        RecipeDTO recipeDTO = entityToDto(recipeRepository.findById(id).orElseThrow());
        return recipeDTO;
    }

    @Override
    public List<RecipeDTO> searchRecipeByTitle(String keyword) {
        List<RecipeEntity> entityList = recipeRepository.findByTitleContainingIgnoreCase(keyword);
        return entityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDTO> findByUsername(String username) {
        List<RecipeEntity> recipeEntityList = recipeRepository.findByUser_UsernameContainingIgnoreCase(username);
        List<RecipeDTO> recipeListDTOs = recipeEntityList.stream()
                .map( recipeEntity -> {
                    RecipeDTO recipeDTO = entityToDto(recipeEntity);
//                    log.info("recipeDTO.getUsername(): " + recipeDTO.getUsername());
                    return recipeDTO;
                })
                .collect(Collectors.toList());
        return recipeListDTOs;
    }

    @Override
    public List<RecipeDTO> searchRecipes(String searchType, String keyword) {
        List<RecipeEntity> entityList;

        if ("title".equals(searchType)) {
            entityList = recipeRepository.findByTitleContainingIgnoreCase(keyword);
        } else if ("writer".equals(searchType)) {
            entityList = recipeRepository.findByUser_UsernameContainingIgnoreCase(keyword);
        } else {
            entityList = List.of(); // 빈 리스트 반환
        }

        return entityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDTO> findByCategory(String category) {
        List<RecipeEntity> entityList = recipeRepository.findByCategory(category);
        return entityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDTO> searchByCategoryAndKeyword(String category, String searchType, String keyword) {
        List<RecipeEntity> entityList;

        if ("title".equals(searchType)) {
            entityList = recipeRepository.findByCategoryAndTitleContaining(category, keyword);
        } else if ("writer".equals(searchType)) {
            entityList = recipeRepository.findByCategoryAndUser_UsernameContaining(category, keyword);
        } else {
            entityList = List.of(); // 빈 리스트
        }

        return entityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }


    @Override
    public void updateRecipe(RecipeDTO updatedrecipeDTO) {

        // DB삭제
        recipeStepRepository.deleteByRecipeEntity_id(updatedrecipeDTO.getId());
        recipeIngredientsRepository.deleteByRecipeEntity_Id(updatedrecipeDTO.getId());

        // 1. 기존 레시피 찾아오기
        RecipeEntity recipe = recipeRepository.findById(updatedrecipeDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다."));
        log.info("updatedrecipeDTO : " + updatedrecipeDTO);
        log.info("recipe : " + recipe);

        // 2. 기본 정보 업데이트
        recipe.setTitle(updatedrecipeDTO.getTitle());
        recipe.setCategory(updatedrecipeDTO.getCategory());
        recipe.setYoutubeLink(updatedrecipeDTO.getYoutubeLink());
        recipe.setMainImage(updatedrecipeDTO.getMainImagePath());

        // 3. 재료 순서 업데이트
        List<RecipeIngredientsDTO> updatedRecipeIngredientList = updatedrecipeDTO.getRecipeIngredients();
        for( RecipeIngredientsDTO updatedRecipeIngredient : updatedRecipeIngredientList ) {
            recipeIngredientsRepository.save(recipeIngredientsServiceImpl.dtoToEntity(updatedRecipeIngredient));
        }

        // 4. 요리 순서 업데이트
        recipe.getSteps().clear();
        int stepNumber = 1;
        for (RecipeStepDTO stepDTO : updatedrecipeDTO.getSteps()) {
            RecipeStepEntity step = new RecipeStepEntity();
            step.setStepNumber(stepNumber++);
            step.setContent(stepDTO.getContent());
            step.setImageName(stepDTO.getImagePath());
            step.setRecipeEntity(recipe); // 양방향 매핑

            recipe.getSteps().add(step);
        }

        // 4. 레시피 저장
        recipeRepository.save(recipe);
    }

    @Override
    public void deleteRecipe(Long id) {
        /* DB 내용 삭제 */
        recipeStepRepository.deleteByRecipeEntity_id(id);
        recipeIngredientsRepository.deleteByRecipeEntity_Id(id);
        recipeRepository.deleteById(id);
    }


    RecipeEntity dtoToEntity(RecipeDTO recipeDTO) {
        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setTitle(recipeDTO.getTitle());
        recipeEntity.setCategory(recipeDTO.getCategory());
        recipeEntity.setYoutubeLink(recipeDTO.getYoutubeLink());
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
        recipeDTO.setMainImagePath(recipeEntity.getMainImage());
        recipeDTO.setYoutubeLink(recipeEntity.getYoutubeLink());
        recipeDTO.setUsername(recipeEntity.getUser().getUsername());
        recipeDTO.setCreatedAt(recipeEntity.getCreatedAt());
        recipeDTO.setLikeCount(recipeEntity.getLikeCount());
        return recipeDTO;
    }



}


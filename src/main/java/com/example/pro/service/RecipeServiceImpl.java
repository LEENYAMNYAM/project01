package com.example.pro.service;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.dto.RecipeStepDTO;
import com.example.pro.dto.UserDTO;
import com.example.pro.entity.*;
import com.example.pro.repository.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
@Builder
@ToString
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientsRepository recipeIngredientsRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final RecipeIngredientsServiceImpl recipeIngredientsServiceImpl;
    private final CartRepository cartRepository;



    @Override
    public void registerRecipe(RecipeDTO recipeDTO, List<MultipartFile> recipeStepImages, Map<String, String> paramMap) {
        // 레시피 등록 전에 로그 추가
        log.info("레시피 등록 시작: 유저 - {}, 레시피 제목 - {}", recipeDTO.getUsername(), recipeDTO.getTitle());


        // 1. 레시피 엔티티 저장
        RecipeEntity recipeEntity = dtoToEntity(recipeDTO);
        UserEntity userEntity = userRepository.findByUsername(recipeDTO.getUsername()).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        recipeEntity.setUser(userEntity);
        recipeRepository.save(recipeEntity);

        log.info("레시피 저장됨: {}", recipeEntity.getId());

        // 2. 장바구니와 레시피 재료 저장
        int ingredientIndex = 0;
        CartEntity cartEntity = new CartEntity();
        cartEntity.setUserEntity(userEntity);
        cartEntity.setTotalPrice(0L);
        cartEntity.setRecipeIngredients(new ArrayList<>());
        cartEntity = cartRepository.save(cartEntity);

        long totalPrice = 0L;

        while (true) {
            String nameKey = "recipeIngredients[" + ingredientIndex + "].ingredient.ingredientName";
            String qtyKey = "recipeIngredients[" + ingredientIndex + "].quantity";
            if (!paramMap.containsKey(nameKey)) break;

            String name = paramMap.get(nameKey);
            Long quantity = Long.parseLong(paramMap.getOrDefault(qtyKey, "1"));

            // 재료 조회
            IngredientEntity ingredient = ingredientRepository.findByIngredientName(name)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 재료입니다: " + name));

            RecipeIngredientsEntity recipeIng = new RecipeIngredientsEntity();
            recipeIng.setRecipeEntity(recipeEntity);
            recipeIng.setIngredientEntity(ingredient);
            recipeIng.setQuantity(quantity);
            recipeIng.setCartEntity(cartEntity);

            // RecipeIngredientsEntity 저장
            recipeIngredientsRepository.save(recipeIng);
            cartEntity.getRecipeIngredients().add(recipeIng);

            totalPrice += ingredient.getPrice() * quantity;
            ingredientIndex++;
        }

        cartEntity.setTotalPrice(totalPrice);
        cartRepository.save(cartEntity);
        log.info("장바구니 저장됨: {}", cartEntity.getId());


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
                    .stepNumber(stepIndex+1)
                    .content(content)
                    .imageName(imageName)
                    .build();

            recipeStepRepository.save(step);
            stepIndex++;
        }

        userEntity.addPoints(100);
        userRepository.save(userEntity);

    }




    @Override
    public List<RecipeDTO> getAllRecipe(String sortBy) {
        List<RecipeEntity> recipeEntityList = recipeRepository.findAll();
        List<RecipeDTO> recipeListDTOs = recipeEntityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        // Apply sorting based on sortBy parameter
        sortRecipesByParameter(recipeListDTOs, sortBy);

        return recipeListDTOs;
    }

    // Helper method to sort recipes based on the sortBy parameter
    private void sortRecipesByParameter(List<RecipeDTO> recipes, String sortBy) {
        if (sortBy != null) {
            if ("rating_high".equals(sortBy)) {
                recipes.sort((r1, r2) -> Double.compare(r2.getAverageRating(), r1.getAverageRating()));
            } else if ("rating_low".equals(sortBy)) {
                recipes.sort((r1, r2) -> Double.compare(r1.getAverageRating(), r2.getAverageRating()));
            } else if ("newest".equals(sortBy)) {
                recipes.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));
            } else if ("oldest".equals(sortBy)) {
                recipes.sort((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()));
            }
        } else {
            // Default sorting: highest rating first, then newest
            recipes.sort((r1, r2) -> {
                int ratingCompare = Double.compare(r2.getAverageRating(), r1.getAverageRating());
                return ratingCompare != 0 ? ratingCompare : r2.getCreatedAt().compareTo(r1.getCreatedAt());
            });
        }
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
    public List<RecipeDTO> searchRecipes(String searchType, String keyword, String sortBy) {
        List<RecipeEntity> entityList;

        if ("title".equals(searchType)) {
            entityList = recipeRepository.findByTitleContainingIgnoreCase(keyword);
        } else if ("writer".equals(searchType)) {
            entityList = recipeRepository.findByUser_UsernameContainingIgnoreCase(keyword);
        } else {
            entityList = List.of(); // 빈 리스트 반환
        }

        List<RecipeDTO> recipeDTOs = entityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        // Apply sorting
        sortRecipesByParameter(recipeDTOs, sortBy);

        return recipeDTOs;
    }

    @Override
    public List<RecipeDTO> findByCategory(String category, String sortBy) {
        List<RecipeEntity> entityList = recipeRepository.findByCategory(category);
        List<RecipeDTO> recipeDTOs = entityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        // Apply sorting
        sortRecipesByParameter(recipeDTOs, sortBy);

        return recipeDTOs;
    }

    @Override
    public List<RecipeDTO> getRecentRecipes() {
        List<RecipeEntity> recentEntities = recipeRepository.findTop4ByOrderByCreatedAtDesc();

        log.info("recentEntities: " + recentEntities);

        // 빈 리스트거나 null일 경우를 방어
        if (recentEntities == null || recentEntities.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }

        return recentEntities.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDTO> getTopRatedRecipes(int limit) {
        // Get all recipes
        List<RecipeEntity> allRecipes = recipeRepository.findAll();

        // Convert to DTOs and sort by rating
        List<RecipeDTO> recipeDTOs = allRecipes.stream()
                .map(this::entityToDto)
                .sorted((r1, r2) -> Double.compare(r2.getAverageRating(), r1.getAverageRating()))
                .limit(limit)
                .collect(Collectors.toList());

        return recipeDTOs;
    }

    @Override
    public List<RecipeDTO> searchByCategoryAndKeyword(String category, String searchType, String keyword, String sortBy) {
        List<RecipeEntity> entityList;

        if ("title".equals(searchType)) {
            entityList = recipeRepository.findByCategoryAndTitleContaining(category, keyword);
        } else if ("writer".equals(searchType)) {
            entityList = recipeRepository.findByCategoryAndUser_UsernameContaining(category, keyword);
        } else {
            entityList = List.of(); // 빈 리스트
        }

        List<RecipeDTO> recipeDTOs = entityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        // Apply sorting
        sortRecipesByParameter(recipeDTOs, sortBy);

        return recipeDTOs;
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

    @Override
    public String getRecipeTitleById(Long id) {
        return recipeRepository.findTitleById(id);
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

        // Calculate average rating from reviews
        List<ReviewEntity> reviews = recipeEntity.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            double sum = 0;
            for (ReviewEntity review : reviews) {
                sum += review.getRating();
            }
            double avgRating = sum / reviews.size();
            recipeDTO.setAverageRating(Math.round(avgRating * 10.0) / 10.0); // Round to 1 decimal place
        } else {
            recipeDTO.setAverageRating(0.0); // Default if no reviews
        }

        return recipeDTO;
    }



}

package com.example.pro.service;

import com.example.pro.dto.RecipeDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.RecipeRepository;
import com.example.pro.repository.RecipeStepRepository;
import com.example.pro.repository.RecipeIngredientsRepository;
import com.example.pro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeStepRepository recipeStepRepository;

    @Mock
    private RecipeIngredientsRepository recipeIngredientsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    private RecipeEntity testRecipeEntity;
    private RecipeDTO testRecipeDTO;
    private List<RecipeEntity> testRecipeEntities;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        // 테스트용 UserEntity 설정
        testUserEntity = new UserEntity();
        testUserEntity.setUsername("testUser");

        // 테스트용 RecipeEntity 설정
        testRecipeEntity = new RecipeEntity();
        testRecipeEntity.setId(1L);
        testRecipeEntity.setTitle("테스트 레시피");
        testRecipeEntity.setCategory("한식");
        testRecipeEntity.setMainImage("test-image.jpg");
        testRecipeEntity.setUser(testUserEntity);
        testRecipeEntity.setCreatedAt(LocalDateTime.now());
        testRecipeEntity.setLikeCount(0L);

        // 테스트용 RecipeDTO 설정
        testRecipeDTO = new RecipeDTO();
        testRecipeDTO.setId(1L);
        testRecipeDTO.setTitle("테스트 레시피");
        testRecipeDTO.setCategory("한식");
        testRecipeDTO.setMainImagePath("test-image.jpg");
        testRecipeDTO.setUsername("testUser");
        testRecipeDTO.setCreatedAt(LocalDateTime.now());
        testRecipeDTO.setLikeCount(0L);

        // 테스트용 RecipeEntity 리스트 설정
        testRecipeEntities = new ArrayList<>();
        testRecipeEntities.add(testRecipeEntity);
    }

    @Test
    @DisplayName("레시피 ID로 조회 테스트")
    void testGetRecipeById() {
        // given
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(testRecipeEntity));

        // when
        RecipeDTO result = recipeService.getRecipeById(1L);

        // then
        assertNotNull(result);
        assertEquals(testRecipeDTO.getId(), result.getId());
        assertEquals(testRecipeDTO.getTitle(), result.getTitle());
        assertEquals(testRecipeDTO.getCategory(), result.getCategory());
        assertEquals(testRecipeDTO.getUsername(), result.getUsername());
        assertEquals(testRecipeDTO.getMainImagePath(), result.getMainImagePath());

        verify(recipeRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("모든 레시피 조회 테스트")
    void testGetAllRecipe() {
        // given
        when(recipeRepository.findAll()).thenReturn(testRecipeEntities);

        // when
        List<RecipeDTO> results = recipeService.getAllRecipe();

        // then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testRecipeDTO.getTitle(), results.get(0).getTitle());

        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("제목으로 레시피 검색 테스트")
    void testSearchRecipeByTitle() {
        // given
        String keyword = "테스트";
        when(recipeRepository.findByTitleContainingIgnoreCase(keyword)).thenReturn(testRecipeEntities);

        // when
        List<RecipeDTO> results = recipeService.searchRecipeByTitle(keyword);

        // then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertTrue(results.get(0).getTitle().contains(keyword));

        verify(recipeRepository, times(1)).findByTitleContainingIgnoreCase(keyword);
    }

    @Test
    @DisplayName("카테고리로 레시피 검색 테스트")
    void testFindByCategory() {
        // given
        String category = "한식";
        when(recipeRepository.findByCategory(category)).thenReturn(testRecipeEntities);

        // when
        List<RecipeDTO> results = recipeService.findByCategory(category);

        // then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(category, results.get(0).getCategory());

        verify(recipeRepository, times(1)).findByCategory(category);
    }

    @Test
    @DisplayName("레시피 삭제 테스트")
    void testDeleteRecipe() {
        // given
        Long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipeEntity));
        doNothing().when(recipeRepository).delete(any(RecipeEntity.class));

        // when
        recipeService.deleteRecipe(recipeId);

        // then
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(recipeRepository, times(1)).delete(any(RecipeEntity.class));
    }
}

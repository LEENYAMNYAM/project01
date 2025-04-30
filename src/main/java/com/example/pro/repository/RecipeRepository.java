package com.example.pro.repository;

import com.example.pro.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {
    // 제목 검색
    List<RecipeEntity> findByTitleContainingIgnoreCase(String keyword);
    // 작성자 검색
    List<RecipeEntity> findByUser_UsernameContainingIgnoreCase(String username);
    List<RecipeEntity> findByCategory(String category);
    List<RecipeEntity> findByCategoryAndTitleContaining(String category, String keyword);
    List<RecipeEntity> findByCategoryAndUser_UsernameContaining(String category, String keyword);
    RecipeEntity findByTitle(String title);
    String findTitleById(Long id);
}

package com.example.pro.repository;

import com.example.pro.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {
    Optional<IngredientEntity> findByIngredientName(String ingredientName);
}

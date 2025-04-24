package com.example.pro.repository;

import com.example.pro.entity.RecipeStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeStepRepository extends JpaRepository<RecipeStepEntity, Long> {
}

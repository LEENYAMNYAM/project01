package com.example.pro.service;

import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public List<RecipeIngredientsDTO> findAllIngredient() {
        return ingredientRepository.findAll().stream()
                .map(this::entityToDto) // IngredientEntity → IngredientDTO
                .map(dto -> RecipeIngredientsDTO.builder()
                        .ingredientDTO(dto) // 여기서 IngredientEntity가 아니라 DTO를 넣자!
                        .build())
                .toList();
    }
}

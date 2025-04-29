package com.example.pro.service;

import com.example.pro.dto.IngredientDTO;
import com.example.pro.entity.IngredientEntity;
import com.example.pro.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientServiceImpl implements IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public List<IngredientDTO> findAllIngredient() {
        List<IngredientEntity> ingredientEntities = ingredientRepository.findAll();
        List<IngredientDTO> ingredientDTOs = ingredientEntities.stream()
                .map(ingredientEntity -> entityToDto(ingredientEntity))
                .collect(Collectors.toList());
        return ingredientDTOs;
    }

}

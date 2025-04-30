package com.example.pro.service;

import com.example.pro.dto.IngredientDTO;
import com.example.pro.entity.IngredientEntity;
import com.example.pro.repository.IngredientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Builder
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

    @Override
    public void saveIngredient(IngredientDTO ingredientDTO) {
        IngredientEntity ingredient = IngredientEntity.builder()
                .ingredientName(ingredientDTO.getIngredientName())
                .price(ingredientDTO.getPrice())
                .detail(ingredientDTO.getDetail())
                .imageUrl(ingredientDTO.getImageUrl())
                .build();
        ingredientRepository.save(ingredient);
    }

    @Override
    public void updateIngredient(Long id, IngredientDTO ingredientDTO) {
        IngredientEntity ingredient = ingredientRepository.findById(id).get();
        ingredient.setIngredientName(ingredientDTO.getIngredientName());
        ingredient.setPrice(ingredientDTO.getPrice());
        ingredient.setDetail(ingredientDTO.getDetail());
        ingredient.setImageUrl(ingredientDTO.getImageUrl());
        ingredientRepository.save(ingredient);
    }

    @Override
    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);

    }

    @Override
    public IngredientDTO findIngredientById(Long id) {
        IngredientEntity ingredientEntity = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 재료를 찾을 수 없습니다: " + id));

        return entityToDto(ingredientEntity);
    }
}

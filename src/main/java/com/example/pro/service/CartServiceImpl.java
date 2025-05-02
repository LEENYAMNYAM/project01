package com.example.pro.service;

import com.example.pro.dto.CartDTO;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.entity.CartEntity;
import com.example.pro.entity.RecipeIngredientsEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.CartRepository;
import com.example.pro.repository.RecipeIngredientsRepository;
import com.example.pro.repository.RecipeRepository;
import com.example.pro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final RecipeIngredientsServiceImpl recipeIngredientsServiceImpl;
    private final RecipeIngredientsRepository recipeIngredientsRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeServiceImpl recipeServiceImpl;


    public List<CartDTO> getAllCartsByUsername(String username) {
        List<CartEntity> cartEntities = cartRepository.findByUserEntity_Username(username);

        List<CartDTO> cartDTOs = new ArrayList<>();
        for (CartEntity cartEntity : cartEntities) {
            cartDTOs.add(entityToDto(cartEntity));
        }
        return cartDTOs;
    }

    @Override
    public void deleteCartItemById(Long id, String username) {
        RecipeIngredientsEntity ingredient = recipeIngredientsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 재료가 없습니다: " + id));

        CartEntity cart = ingredient.getCartEntity();

        if (cart == null || cart.getUserEntity() == null) {
            throw new IllegalStateException("잘못된 장바구니 정보입니다.");
        }

        if (!cart.getUserEntity().getUsername().equals(username)) {
            throw new SecurityException("본인의 장바구니가 아닙니다.");
        }

        // 👉 관계 끊기
        cart.getRecipeIngredients().remove(ingredient);
        ingredient.setCartEntity(null);

        // 저장 (관계 해제된 상태 저장)
        recipeIngredientsRepository.save(ingredient);

        long priceToRemove = ingredient.getIngredientEntity().getPrice() * ingredient.getQuantity();
        cart.setTotalPrice(cart.getTotalPrice() - priceToRemove);  // 기존 가격에서 삭제된 재료 가격을 빼기

        cartRepository.save(cart);

        // 👉 장바구니에 재료가 하나도 없으면, 장바구니 삭제
        if (cart.getRecipeIngredients().isEmpty()) {
            cartRepository.delete(cart);
        }
    }



    @Override
    public void deleteCartById(Long cartId, String username) {
        // 장바구니 조회
        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장바구니가 없습니다: " + cartId));

        // 장바구니 소유자 확인
        if (!cart.getUserEntity().getUsername().equals(username)) {
            throw new SecurityException("본인의 장바구니가 아닙니다.");
        }

        // 장바구니에 포함된 재료들의 외래 키 연결을 끊거나 삭제
        for (RecipeIngredientsEntity ingredient : cart.getRecipeIngredients()) {
            ingredient.setCartEntity(null);  // 장바구니와의 연결을 끊음
        }

        // 장바구니 삭제
        cartRepository.delete(cart);
    }


    CartEntity dtoToEntity(CartDTO cartDTO) {
        CartEntity cartEntity = new CartEntity();
        cartEntity.setUserEntity(userRepository.findByUsername(cartDTO.getUsername()).get());
        cartEntity.setTotalPrice(cartDTO.getTotalPrice());
        cartEntity.setCreatedAt(cartDTO.getCreatedAt() != null ? cartDTO.getCreatedAt() : LocalDateTime.now());


        List<RecipeIngredientsEntity> recipeIngredientsEntityList = new ArrayList<>();
        for (RecipeIngredientsDTO recipeIngredientDTO : cartDTO.getRecipeIngredients()) {
            RecipeIngredientsEntity entity = recipeIngredientsServiceImpl.dtoToEntity(recipeIngredientDTO);
            entity.setCartEntity(cartEntity);
            recipeIngredientsEntityList.add(entity);
        }

        cartEntity.setRecipeIngredients(recipeIngredientsEntityList);
        return cartEntity;
    }

    CartDTO entityToDto(CartEntity cartEntity) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cartEntity.getId());
        cartDTO.setUsername(cartEntity.getUserEntity().getUsername());
        cartDTO.setTotalPrice(cartEntity.getTotalPrice());
        cartDTO.setCreatedAt(cartEntity.getCreatedAt());

        List<RecipeIngredientsDTO> dtoList = (cartEntity.getRecipeIngredients() != null) ?
                cartEntity.getRecipeIngredients().stream()
                        .map(recipeIngredientsServiceImpl::entityToDto)
                        .collect(Collectors.toList()) : new ArrayList<>();
        cartDTO.setRecipeIngredients(dtoList);
        return cartDTO;
    }

}

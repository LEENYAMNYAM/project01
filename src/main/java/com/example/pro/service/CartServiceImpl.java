package com.example.pro.service;

import com.example.pro.dto.CartDTO;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.entity.CartEntity;
import com.example.pro.entity.RecipeIngredientsEntity;
import com.example.pro.repository.CartRepository;
import com.example.pro.repository.RecipeIngredientsRepository;
import com.example.pro.repository.RecipeRepository;
import com.example.pro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final RecipeIngredientsServiceImpl recipeIngredientsServiceImpl;
    private final RecipeIngredientsRepository recipeIngredientRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeServiceImpl recipeServiceImpl;

    @Override
    @Transactional
    public CartDTO createCart(RecipeDTO recipeDTO) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUsername(recipeDTO.getUsername());
        cartDTO.setRecipeIngredients(recipeDTO.getRecipeIngredients());

        // 총합 계산 (단가 * 수량)
        long totalPrice = 0;
        for (RecipeIngredientsDTO dto : recipeDTO.getRecipeIngredients()) {
            totalPrice += dto.getIngredient().getPrice() * dto.getQuantity();
        }
        cartDTO.setTotalPrice(totalPrice);
        cartDTO.setRecipeTitle(recipeDTO.getTitle());

        // 엔티티 변환 및 연관관계 정리
        CartEntity cartEntity = dtoToEntity(cartDTO);
        for (RecipeIngredientsEntity e : cartEntity.getRecipeIngredients()) {
            e.setCartEntity(cartEntity);
        }

        CartEntity savedCart =  cartRepository.save(cartEntity);

        // 저장된 엔티티로 DTO 반환
        CartDTO savedCartDTO = entityToDto(savedCart);
        savedCartDTO.setId(savedCart.getId()); // 여기서 ID 세팅 ✅
        return savedCartDTO;


    }

    // 개별재료 삭제
    public void removeIngredientFromCart(Long ingredientId) {
        recipeIngredientRepository.deleteById(ingredientId);
    }

    //레시피재료 전체 삭제
    public void removeRecipeFromCart(Long recipeId, String username) {
        List<RecipeIngredientsEntity> items = recipeIngredientRepository.findByRecipeEntity_IdAndCartEntity_UserEntity_Username(recipeId, username);
        recipeIngredientRepository.deleteAll(items);
    }


    @Override
    public List<CartDTO> getAllCartsByUsername (String username) {
        return List.of();
    }

    @Override
    public void deleteCart(Long cartId) {

    }

    @Override
    public void updateCart(Long cartId, List<Long> recipeIds) {

    }

    @Override
    public void addCart(Long cartId, List<Long> recipeIds) {

    }

    CartEntity dtoToEntity(CartDTO cartDTO) {
        CartEntity cartEntity = new CartEntity();
        cartEntity.setUserEntity(userRepository.findByUsername(cartDTO.getUsername()).get());
        cartEntity.setTotalPrice(cartDTO.getTotalPrice());
        cartEntity.setCreatedAt(cartDTO.getCreatedAt() != null ? cartDTO.getCreatedAt() : LocalDateTime.now());
        cartEntity.setRecipe(recipeRepository.findByTitle(cartDTO.getRecipeTitle()));

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
        cartDTO.setUsername(cartEntity.getUserEntity().getUsername());
        cartDTO.setTotalPrice(cartEntity.getTotalPrice());
        cartDTO.setCreatedAt(cartEntity.getCreatedAt());
        cartDTO.setRecipeTitle(cartEntity.getRecipe().getTitle());

        List<RecipeIngredientsDTO> dtoList = cartEntity.getRecipeIngredients().stream()
                .map(recipeIngredientsServiceImpl::entityToDto)
                .collect(Collectors.toList());
        cartDTO.setRecipeIngredients(dtoList);

        return cartDTO;
    }

}

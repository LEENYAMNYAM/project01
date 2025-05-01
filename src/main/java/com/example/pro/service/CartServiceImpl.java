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
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final RecipeIngredientsServiceImpl recipeIngredientsServiceImpl;
    private final RecipeIngredientsRepository recipeIngredientRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeServiceImpl recipeServiceImpl;


//
//    public CartDTO createCart(RecipeDTO recipeDTO) {
//        // 현재 인증된 사용자 정보를 SecurityContext에서 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();  // 사용자 이름 (username) 추출
//
//        // 유저가 존재하는지 확인
//        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
//
//        CartDTO cartDTO = new CartDTO();
//        cartDTO.setUsername(username);  // 현재 인증된 유저의 이름 설정
//        cartDTO.setRecipeIngredients(recipeDTO.getRecipeIngredients());
//
//        // 총합 계산 (단가 * 수량)
//        long totalPrice = 0;
//        for (RecipeIngredientsDTO dto : recipeDTO.getRecipeIngredients()) {
//            totalPrice += dto.getIngredient().getPrice() * dto.getQuantity();
//        }
//        cartDTO.setTotalPrice(totalPrice);
//        cartDTO.setRecipeTitle(recipeDTO.getTitle());
//
//        // 엔티티 변환 및 연관관계 정리
//        CartEntity cartEntity = dtoToEntity(cartDTO);
//        cartEntity.setUserEntity(userEntity);  // 유저와 카트를 연결
//
//        // 레시피 재료와 카트 항목 연관 정리
//        for (RecipeIngredientsEntity e : cartEntity.getRecipeIngredients()) {
//            e.setCartEntity(cartEntity);
//        }
//
//        // 카트 저장
//        CartEntity savedCart = cartRepository.save(cartEntity);
//
//        // 저장된 엔티티로 DTO 반환
//        CartDTO savedCartDTO = entityToDto(savedCart);
//        savedCartDTO.setId(savedCart.getId()); // 저장된 카트 ID 세팅
//
//        return savedCartDTO;
//    }


    // 개별재료 삭제
    public void removeIngredientFromCart(Long ingredientId) {
        recipeIngredientRepository.deleteById(ingredientId);
    }

    //레시피재료 전체 삭제
    public void removeRecipeFromCart(Long recipeId, String username) {
        List<RecipeIngredientsEntity> items = recipeIngredientRepository.findByRecipeEntity_IdAndCartEntity_UserEntity_Username(recipeId, username);
        recipeIngredientRepository.deleteAll(items);
    }

    public List<CartDTO> getAllCartsByUsername(String username) {
        List<CartEntity> cartEntities = cartRepository.findByUserEntity_Username(username);

        List<CartDTO> cartDTOs = new ArrayList<>();
        for (CartEntity cartEntity : cartEntities) {
            cartDTOs.add(entityToDto(cartEntity));
        }
        return cartDTOs;
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


        List<RecipeIngredientsDTO> dtoList = (cartEntity.getRecipeIngredients() != null) ?
                cartEntity.getRecipeIngredients().stream()
                        .map(recipeIngredientsServiceImpl::entityToDto)
                        .collect(Collectors.toList()) : new ArrayList<>();
        cartDTO.setRecipeIngredients(dtoList);

        return cartDTO;
    }

}

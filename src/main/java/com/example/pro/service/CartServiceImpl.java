package com.example.pro.service;

import com.example.pro.dto.CartDTO;
import com.example.pro.dto.CartItemDTO;
import com.example.pro.dto.IngredientDTO;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.entity.*;
import com.example.pro.repository.CartItemRepository;
import com.example.pro.repository.CartRepository;
import com.example.pro.repository.IngredientRepository;
import com.example.pro.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientService ingredientService;
    private final RecipeService recipeService;

    @Override
    @Transactional
    public CartEntity getOrCreateCart(UserEntity user) {
        Optional<CartEntity> existingCart = cartRepository.findByUser(user);
        
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        
        CartEntity newCart = new CartEntity();
        newCart.setUser(user);
        newCart.setTotalPrice(0L);
        newCart.setCreatedAt(LocalDateTime.now());
        
        return cartRepository.save(newCart);
    }

    @Override
    @Transactional
    public CartItemDTO addToCart(Long ingredientId, Long recipeId, Long quantity, UserEntity user) {
        CartEntity cart = getOrCreateCart(user);
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        
        RecipeEntity recipe = null;
        if (recipeId != null) {
            recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new RuntimeException("Recipe not found"));
        }
        
        // Check if the item already exists in the cart
        Optional<CartItemEntity> existingItem;
        if (recipe != null) {
            existingItem = cartItemRepository.findByCartEntityAndIngredientEntityAndRecipeEntity(cart, ingredient, recipe);
        } else {
            existingItem = cartItemRepository.findByCartEntityAndIngredientEntity(cart, ingredient);
        }
        
        CartItemEntity cartItem;
        if (existingItem.isPresent()) {
            // Update quantity if item already exists
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Create new cart item
            cartItem = new CartItemEntity();
            cartItem.setCartEntity(cart);
            cartItem.setIngredientEntity(ingredient);
            cartItem.setRecipeEntity(recipe);
            cartItem.setQuantity(quantity);
        }
        
        cartItem = cartItemRepository.save(cartItem);
        
        // Update cart total price
        updateCartTotalPrice(cart);
        
        return convertToCartItemDTO(cartItem);
    }

    @Override
    @Transactional
    public CartItemDTO updateCartItem(Long cartItemId, Long change, UserEntity user) {
        CartEntity cart = getOrCreateCart(user);
        
        CartItemEntity cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Verify the cart item belongs to the user
        if (!cartItem.getCartEntity().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to the user");
        }
        
        // Update quantity
        Long newQuantity = cartItem.getQuantity() + change;
        if (newQuantity <= 0) {
            cartItemRepository.delete(cartItem);
            updateCartTotalPrice(cart);
            return null;
        }
        
        cartItem.setQuantity(newQuantity);
        cartItem = cartItemRepository.save(cartItem);
        
        // Update cart total price
        updateCartTotalPrice(cart);
        
        return convertToCartItemDTO(cartItem);
    }

    @Override
    @Transactional
    public void removeFromCart(Long cartItemId, UserEntity user) {
        CartEntity cart = getOrCreateCart(user);
        
        CartItemEntity cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Verify the cart item belongs to the user
        if (!cartItem.getCartEntity().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to the user");
        }
        
        cartItemRepository.delete(cartItem);
        
        // Update cart total price
        updateCartTotalPrice(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartItems(UserEntity user) {
        CartEntity cart = getOrCreateCart(user);
        List<CartItemEntity> cartItems = cartItemRepository.findByCartEntity(cart);
        
        return cartItems.stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long calculateTotalPrice(UserEntity user) {
        CartEntity cart = getOrCreateCart(user);
        List<CartItemEntity> cartItems = cartItemRepository.findByCartEntity(cart);
        
        Long totalPrice = 0L;
        for (CartItemEntity item : cartItems) {
            totalPrice += item.getIngredientEntity().getPrice() * item.getQuantity();
        }
        
        return totalPrice;
    }

    @Override
    @Transactional
    public void clearCart(UserEntity user) {
        CartEntity cart = getOrCreateCart(user);
        cartItemRepository.deleteByCartEntity(cart);
        
        // Reset cart total price
        cart.setTotalPrice(0L);
        cartRepository.save(cart);
    }

    @Override
    public CartDTO entityToDto(CartEntity cartEntity) {
        if (cartEntity == null) {
            return null;
        }
        
        return CartDTO.builder()
                .id(cartEntity.getId())
                .username(cartEntity.getUser().getUsername())
                .totalPrice(cartEntity.getTotalPrice())
                .createdAt(cartEntity.getCreatedAt())
                .build();
    }

    @Override
    public CartEntity dtoToEntity(CartDTO cartDTO) {
        if (cartDTO == null) {
            return null;
        }
        
        CartEntity cartEntity = new CartEntity();
        cartEntity.setId(cartDTO.getId());
        cartEntity.setTotalPrice(cartDTO.getTotalPrice());
        cartEntity.setCreatedAt(cartDTO.getCreatedAt());
        
        return cartEntity;
    }
    
    // Helper method to convert CartItemEntity to CartItemDTO
    private CartItemDTO convertToCartItemDTO(CartItemEntity cartItem) {
        IngredientDTO ingredientDTO = ingredientService.entityToDto(cartItem.getIngredientEntity());
        RecipeDTO recipeDTO = null;
        if (cartItem.getRecipeEntity() != null) {
            recipeDTO = recipeService.getRecipeById(cartItem.getRecipeEntity().getId());
        }
        
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .ingredientId(cartItem.getIngredientEntity().getId())
                .recipeId(cartItem.getRecipeEntity() != null ? cartItem.getRecipeEntity().getId() : null)
                .cartId(cartItem.getCartEntity().getId())
                .quantity(cartItem.getQuantity())
                .ingredient(ingredientDTO)
                .recipe(recipeDTO)
                .build();
    }
    
    // Helper method to update cart total price
    private void updateCartTotalPrice(CartEntity cart) {
        Long totalPrice = calculateTotalPrice(cart.getUser());
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }
}
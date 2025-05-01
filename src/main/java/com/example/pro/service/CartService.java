package com.example.pro.service;

import com.example.pro.dto.CartDTO;
import com.example.pro.dto.CartItemDTO;
import com.example.pro.entity.CartEntity;
import com.example.pro.entity.UserEntity;

import java.util.List;

public interface CartService {
    
    // Get or create a cart for the user
    CartEntity getOrCreateCart(UserEntity user);
    
    // Add an ingredient to the cart
    CartItemDTO addToCart(Long ingredientId, Long recipeId, Long quantity, UserEntity user);
    
    // Update the quantity of an item in the cart
    CartItemDTO updateCartItem(Long cartItemId, Long change, UserEntity user);
    
    // Remove an item from the cart
    void removeFromCart(Long cartItemId, UserEntity user);
    
    // Get all items in the user's cart
    List<CartItemDTO> getCartItems(UserEntity user);
    
    // Calculate the total price of the cart
    Long calculateTotalPrice(UserEntity user);
    
    // Clear the cart after payment
    void clearCart(UserEntity user);
    
    // Convert CartEntity to CartDTO
    CartDTO entityToDto(CartEntity cartEntity);
    
    // Convert CartDTO to CartEntity
    CartEntity dtoToEntity(CartDTO cartDTO);
}
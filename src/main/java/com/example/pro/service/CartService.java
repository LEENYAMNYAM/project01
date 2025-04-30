package com.example.pro.service;

import com.example.pro.dto.CartDTO;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.entity.CartEntity;
import com.example.pro.repository.UserRepository;

import java.util.List;

public interface CartService {
    CartDTO createCart(RecipeDTO recipeDTO);
    List<CartDTO> getAllCartsByUsername (String username);
    void deleteCart(Long cartId);
    void updateCart(Long cartId, List<Long> recipeIds);
    void addCart(Long cartId, List<Long> recipeIds);
    public void removeIngredientFromCart(Long ingredientId);
    public void removeRecipeFromCart(Long recipeId, String username);

}

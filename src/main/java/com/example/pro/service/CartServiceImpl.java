package com.example.pro.service;

import com.example.pro.dto.CartDTO;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private final CartRepository cartRepository;

    @Override
    public CartDTO createCart(RecipeDTO recipeDTO) {



        return null;
    }

    @Override
    public List<CartDTO> getCartsByCartId(Long cartId) {
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
}

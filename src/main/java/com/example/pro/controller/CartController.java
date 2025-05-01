package com.example.pro.controller;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.dto.CartDTO;
import com.example.pro.dto.CartItemDTO;
import com.example.pro.entity.UserEntity;
import com.example.pro.service.CartService;
import com.example.pro.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
        if (principalDetail == null) {
            return "redirect:/userinfo/login";
        }

        UserEntity user = principalDetail.getUser();
        List<CartItemDTO> cartItems = cartService.getCartItems(user);
        Long totalPrice = cartService.calculateTotalPrice(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "cart/list";
    }

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
        if (principalDetail == null) {
            return "redirect:/userinfo/login";
        }

        UserEntity user = principalDetail.getUser();
        List<CartItemDTO> cartItems = cartService.getCartItems(user);
        Long totalPrice = cartService.calculateTotalPrice(user);
        Long shippingFee = 3000L; // 기본 배송비

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("user", user);

        return "cart/checkout";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Long ingredientId,
            @RequestParam(required = false) Long recipeId,
            @RequestParam(defaultValue = "1") Long quantity,
            @AuthenticationPrincipal PrincipalDetail principalDetail) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (principalDetail == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.ok(response);
            }

            UserEntity user = principalDetail.getUser();
            CartItemDTO cartItem = cartService.addToCart(ingredientId, recipeId, quantity, user);

            response.put("success", true);
            response.put("message", "장바구니에 추가되었습니다.");
            response.put("cartItem", cartItem);

        } catch (Exception e) {
            log.error("장바구니 추가 중 오류 발생: " + e.getMessage());
            response.put("success", false);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @PathVariable Long id,
            @RequestParam Long change,
            @AuthenticationPrincipal PrincipalDetail principalDetail) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (principalDetail == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.ok(response);
            }

            UserEntity user = principalDetail.getUser();
            CartItemDTO cartItem = cartService.updateCartItem(id, change, user);

            response.put("success", true);
            if (cartItem == null) {
                response.put("message", "상품이 장바구니에서 제거되었습니다.");
                response.put("removed", true);
            } else {
                response.put("message", "수량이 변경되었습니다.");
                response.put("cartItem", cartItem);
            }

        } catch (Exception e) {
            log.error("장바구니 수정 중 오류 발생: " + e.getMessage());
            response.put("success", false);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetail principalDetail) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (principalDetail == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.ok(response);
            }

            UserEntity user = principalDetail.getUser();
            cartService.removeFromCart(id, user);

            response.put("success", true);
            response.put("message", "상품이 장바구니에서 제거되었습니다.");

        } catch (Exception e) {
            log.error("장바구니 삭제 중 오류 발생: " + e.getMessage());
            response.put("success", false);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCart(
            @AuthenticationPrincipal PrincipalDetail principalDetail) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (principalDetail == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.ok(response);
            }

            UserEntity user = principalDetail.getUser();
            cartService.clearCart(user);

            response.put("success", true);
            response.put("message", "장바구니가 비워졌습니다.");

        } catch (Exception e) {
            log.error("장바구니 비우기 중 오류 발생: " + e.getMessage());
            response.put("success", false);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}

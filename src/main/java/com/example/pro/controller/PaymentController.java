package com.example.pro.controller;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.dto.CartItemDTO;
import com.example.pro.entity.CartEntity;
import com.example.pro.entity.PaymentEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.CartRepository;
import com.example.pro.repository.PaymentRepository;
import com.example.pro.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
@Log4j2
public class PaymentController {

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;

    @PostMapping("/process")
    public String processPayment(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false) String addressDetail,
            @RequestParam(required = false) String deliveryRequest,
            @RequestParam(required = false) String deliveryRequestDirect,
            @RequestParam String paymentMethod,
            @RequestParam(defaultValue = "0") Long usePoints,
            @AuthenticationPrincipal PrincipalDetail principalDetail,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (principalDetail == null) {
            return "redirect:/userinfo/login";
        }

        try {
            UserEntity user = principalDetail.getUser();
            
            // Get cart items and total price
            List<CartItemDTO> cartItems = cartService.getCartItems(user);
            if (cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "장바구니가 비어있습니다.");
                return "redirect:/cart/list";
            }
            
            Long totalPrice = cartService.calculateTotalPrice(user);
            Long shippingFee = 3000L; // 기본 배송비
            Long finalPrice = totalPrice + shippingFee - usePoints;
            
            if (finalPrice < 0) {
                finalPrice = 0L;
            }
            
            // Get user's cart
            CartEntity cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
            
            // Create payment record
            PaymentEntity payment = new PaymentEntity();
            payment.setCartEntity(cart);
            payment.setTotalPrice(finalPrice);
            payment.setStatus("COMPLETED");
            payment.setCreatedAt(LocalDateTime.now());
            
            // Save payment
            paymentRepository.save(payment);
            
            // Clear cart (but keep the cart entity for the payment record)
            cartService.clearCart(user);
            
            // Add success message
            redirectAttributes.addFlashAttribute("success", "결제가 완료되었습니다.");
            redirectAttributes.addFlashAttribute("orderId", payment.getId());
            
            return "redirect:/payment/complete";
            
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/cart/checkout";
        }
    }
    
    @RequestMapping("/complete")
    public String paymentComplete(Model model) {
        return "payment/complete";
    }
}
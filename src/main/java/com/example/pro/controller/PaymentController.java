package com.example.pro.controller;

import com.example.pro.dto.CartDTO;
import com.example.pro.dto.OrderDTO;
import com.example.pro.dto.UserDTO;
import com.example.pro.entity.UserEntity;
import com.example.pro.service.CartService;
import com.example.pro.service.OrderService;
import com.example.pro.service.RecipeIngredientsServiceImpl;
import com.example.pro.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;
    private final RecipeIngredientsServiceImpl recipeIngredientsServiceImpl;

    public PaymentController(CartService cartService, OrderService orderService, UserService userService, RecipeIngredientsServiceImpl recipeIngredientsServiceImpl) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
        this.recipeIngredientsServiceImpl = recipeIngredientsServiceImpl;
    }

    @PostMapping("/process")
    public String processPayment(
            @RequestParam Long cartId,
            @RequestParam Long recipeId,
            @RequestParam int totalAmount,  // 상품 + 배송비
            @RequestParam int shippingFee,
            @RequestParam int usedPoints,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String shippingAddress,
            @RequestParam String shippingAddressDetail,
            @RequestParam String deliveryRequest,
            @RequestParam(required = false) String deliveryRequestDirect,
            @RequestParam String paymentMethod,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        // 1. 로그인 사용자 정보 가져오기
        String username = principal.getName();
        UserDTO user = userService.findByUsername(username);

        // 2. 실제 결제 금액 계산 (포인트 차감)
        int finalAmount = totalAmount - usedPoints;

        // 3. 주문 번호 생성
        String orderNumber = "ORD-" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 8);

        // 4. 주문 객체 생성
        OrderDTO order = new OrderDTO();
        order.setOrderNumber(orderNumber);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(finalAmount);
        order.setItemsAmount(totalAmount - shippingFee);
        order.setShippingFee(shippingFee);
        order.setUsedPoints(usedPoints);
        order.setEarnedPoints((int)(finalAmount * 0.01)); // 예: 결제금액의 1%
        order.setPaymentMethod(paymentMethod);
        order.setShippingAddress(shippingAddress);
        order.setShippingAddressDetail(shippingAddressDetail);
        order.setDeliveryRequest("direct".equals(deliveryRequest) ? deliveryRequestDirect : deliveryRequest);

        List<CartDTO> cartDTOList = cartService.getAllCartsByUsername(username); // 전체 장바구니 재료


        Optional<CartDTO> optionalCart = cartDTOList.stream()
                .filter(c -> c.getId() != null && c.getId().equals(cartId))  // null 체크 추가
                .filter(c -> c.getRecipeIngredients() != null &&
                        c.getRecipeIngredients().stream().anyMatch(i -> i.getRecipeId().equals(recipeId)))
                .findFirst();

        order.setOrderItems(optionalCart.get().getRecipeIngredients());

        // 5. 사용자 포인트 차감 및 적립
        user.setPoints(user.getPoints() - usedPoints + order.getEarnedPoints());
        userService.updateUser(username, user);

        // 6. 장바구니 삭제
        cartService.deleteCartById(cartId, username);

        // 7. 주문 정보 전달
        redirectAttributes.addFlashAttribute("order", order);
        redirectAttributes.addFlashAttribute("user", user);

        return "redirect:/payment/complete";
    }

    @GetMapping("/complete")
    public String showCompletePage() {
        return "payment/complete"; // complete.html
    }
}
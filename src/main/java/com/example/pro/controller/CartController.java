package com.example.pro.controller;

import com.example.pro.dto.CartDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.dto.UserDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.repository.RecipeRepository;
import com.example.pro.service.CartService;
import com.example.pro.service.RecipeService;
import com.example.pro.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartService cartService;
    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;
    private final UserService userService;

    @GetMapping("/list")
    public String cartList(Model model, Principal principal) {
        if (principal == null) {
            log.warn("비로그인 사용자가 장바구니에 접근 시도");
            return "redirect:/login"; // 로그인 페이지로 리디렉션
        }

        String username = principal.getName();
        log.info("장바구니 조회 시작: {}", username);

        List<CartDTO> carts = cartService.getAllCartsByUsername(username);

        log.info("조회된 장바구니 개수: {}", carts.size());  // 장바구니 개수 로그 추가

        if (carts.isEmpty()) {
            log.warn("장바구니가 비어 있습니다.");
        }

        Map<Long, String> recipeIdToTitleMap = new HashMap<>();
        Map<String, List<RecipeIngredientsDTO>> groupedByTitle = new LinkedHashMap<>();
        Map<String, Long> totalPriceMap = new HashMap<>();

        for (CartDTO cart : carts) {
            log.info("카트 ID: {}, 재료 개수: {}", cart.getId(), cart.getRecipeIngredients().size());  // 각 카트의 재료 수 로그 추가
            for (RecipeIngredientsDTO ingredient : cart.getRecipeIngredients()) {
                Long recipeId = ingredient.getRecipeId();

                // 레시피 제목 가져오기 (1회만)
                String recipeTitle = recipeIdToTitleMap.computeIfAbsent(recipeId, id -> {
                    RecipeEntity recipe = recipeRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 레시피 없음"));
                    return recipe.getTitle(); // 제목을 반환하여 맵에 저장
                });

                groupedByTitle.computeIfAbsent(recipeTitle, k -> new ArrayList<>()).add(ingredient);

                long price = ingredient.getIngredient().getPrice() * ingredient.getQuantity();
                totalPriceMap.put(recipeTitle,
                        totalPriceMap.getOrDefault(recipeTitle, 0L) + price);
            }
        }

        Map<String, Long> recipeIdMap = new HashMap<>();
        recipeIdToTitleMap.forEach((id, title) -> recipeIdMap.put(title, id));

        model.addAttribute("groupedByRecipe", groupedByTitle);
        model.addAttribute("recipeTotalPrice", totalPriceMap);
        model.addAttribute("recipeIdMap", recipeIdMap);

        log.info("장바구니 렌더링 완료: {}", username);
        return "/cart/list";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam Long recipeId,
                           @RequestParam Long cartId,
                           Principal principal,
                           Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();

        // 1. 해당 유저의 장바구니 중 recipeId, cartId가 일치하는 CartDTO만 추출
        List<CartDTO> cartDTOList = cartService.getAllCartsByUsername(username); // 전체 장바구니 재료

        Optional<CartDTO> optionalCart = cartDTOList.stream()
                .filter(c -> c.getId() != null && c.getId().equals(cartId))  // null 체크 추가
                .filter(c -> c.getRecipeIngredients() != null &&
                        c.getRecipeIngredients().stream().anyMatch(i -> i.getRecipeId().equals(recipeId)))
                .findFirst();

        if (optionalCart.isEmpty()) {
            throw new IllegalArgumentException("해당 레시피와 카트가 일치하는 항목이 없습니다.");
        }

        // 2. 해당 CartDTO에서 recipeId에 해당하는 재료만 추출
        List<RecipeIngredientsDTO> recipeIngredientDTOList = optionalCart.get()
                .getRecipeIngredients()
                .stream()
                .filter(i -> i.getRecipeId().equals(recipeId))
                .toList();

        // 3. 총 가격 계산
        long totalPrice = recipeIngredientDTOList.stream()
                .mapToLong(i -> i.getIngredient().getPrice() * i.getQuantity())
                .sum();

        long shippingFee = 3000;

        // 4. 사용자 정보 가져오기
        UserDTO userDTO = userService.findByUsername(username);

        // 5. 모델에 담기
        model.addAttribute("cartItems", recipeIngredientDTOList);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("user", userDTO);

        return "cart/checkout";
    }


}



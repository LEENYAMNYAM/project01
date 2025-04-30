package com.example.pro.controller;

import com.example.pro.dto.CartDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.service.CartService;
import com.example.pro.service.RecipeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final RecipeService recipeService;

    @GetMapping("/list")
    public String cartList(Model model, Principal principal) {
        String username = principal.getName();
        List<CartDTO> carts = cartService.getAllCartsByUsername(username);

        // 레시피별 그룹핑
        Map<Long, String> recipeIdToTitleMap = new HashMap<>();
        Map<String, List<RecipeIngredientsDTO>> groupedByTitle = new LinkedHashMap<>();
        Map<String, Long> totalPriceMap = new HashMap<>();
        Map<String, Integer> ingredientCountMap = new HashMap<>();


        for (CartDTO cart : carts) {
            for (RecipeIngredientsDTO ingredient : cart.getRecipeIngredients()) {
                Long recipeId = ingredient.getRecipeId();

                // 레시피 제목 가져오기 (한 번만)
                String recipeTitle = recipeIdToTitleMap.computeIfAbsent(recipeId, id -> {
                    return recipeService.getRecipeTitleById(id); // 또는 직접 RecipeRepository 사용
                });

                // 재료 리스트 그룹핑
                groupedByTitle
                        .computeIfAbsent(recipeTitle, k -> new ArrayList<>())
                        .add(ingredient);

                // 가격 합산
                long price = ingredient.getIngredient().getPrice() * ingredient.getQuantity();
                totalPriceMap.put(recipeTitle, totalPriceMap.getOrDefault(recipeTitle, 0L) + price);

            }
        }

        // DTO에 레시피 ID 매핑
        Map<String, Long> recipeIdMap = new HashMap<>();
        recipeIdToTitleMap.forEach((id, title) -> recipeIdMap.put(title, id));
        model.addAttribute("recipeIdMap", recipeIdMap);

        // 모델 데이터 세팅
        model.addAttribute("groupedByRecipe", groupedByTitle);
        model.addAttribute("recipeTotalPrice", totalPriceMap);
        model.addAttribute("ingredientCountMap", ingredientCountMap);

        return "cart/list"; // 뷰 이름
    }

    // 개별재료 삭제
    @PostMapping("/cart/delete/item/{ingredientId}")
    public String deleteIngredientFromCart(@PathVariable Long ingredientId) {
        cartService.removeIngredientFromCart(ingredientId);
        return "redirect:/cart/list";
    }

    // 레시피별 장바구니 전체 삭제
    @PostMapping("/cart/delete/recipe/{recipeId}")
    public String deleteRecipeFromCart(@PathVariable Long recipeId, Principal principal) {
        cartService.removeRecipeFromCart(recipeId, principal.getName());
        return "redirect:/cart/list";
    }



    @GetMapping("/checkout")
    public String checkout(Model model) {
        // 임시로 빈 카트 객체 추가
        model.addAttribute("cart", new CartDTO());  // 빈 CartDTO 추가

        return "/cart/checkout";  // checkout.html로 이동
    }

}

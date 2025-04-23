package com.example.pro.controller;

import com.example.pro.dto.IngredientDTO;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.service.IngredientService;
import com.example.pro.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recipe")
@Log4j2
@RequiredArgsConstructor
public class RecipeController {

    private final IngredientService ingredientService;
    private final RecipeService recipeService;

    @GetMapping("/register")
    public String recipeRegister(Model model) {
        model.addAttribute("recipeDTO", new RecipeDTO());
        model.addAttribute("ingredients", ingredientService.findAllIngredient());
        model.addAttribute("categories", List.of("밥", "국", "메인반찬", "밑반찬", "면"));
        return "recipe/register"; // 명시적으로 반환
    }

    @PostMapping("/register")
    public String recipeRegister(@ModelAttribute RecipeDTO recipeDTO,
                                 @RequestParam("fileNames") List<MultipartFile> files,
                                 @RequestParam Map<String, String> paramMap, // step[], ingredients[]
                                 Principal principal) {
        recipeDTO.setUsername("홍길동");
//        recipeDTO.setUsername(principal.getName());
        recipeService.insertRecipe(recipeDTO, principal, paramMap);
        return "recipe/list";
    }

    @GetMapping("/list")
    public void recipeList(Model model){
        List<RecipeDTO> recipeList = recipeService.getAllRecipe();
        model.addAttribute("recipeList", recipeList);
    }

}
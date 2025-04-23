package com.example.pro.controller;

import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.RecipeStepDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.entity.RecipeStepEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.UserRepository;
import com.example.pro.service.FileService;
import com.example.pro.service.IngredientService;
import com.example.pro.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recipe")
@Log4j2
@RequiredArgsConstructor
public class RecipeController {

    private final FileService fileService;
    private final UserRepository userRepository;
    private final IngredientService ingredientService;
    private final RecipeService recipeService;

    private static final String UPLOAD_DIR = "D:/JMT/Project01/projerct01/src/main/resources/static/assets";

    @GetMapping("/register")
    public void recipeRegister(Model model) {
        model.addAttribute("recipeDTO", new RecipeDTO());
        model.addAttribute("categories", List.of("밥", "국", "메인반찬", "밑반찬", "면"));
        model.addAttribute("ingredients", ingredientService.findAllIngredient());
    }

    @PostMapping("/register")
    public String recipeRegister(@ModelAttribute RecipeDTO recipeDTO,
                                 @RequestParam("mainImage") MultipartFile mainImage,
                                 @RequestParam Map<String, String> paramMap,
                                 @RequestParam("stepImages") List<MultipartFile> stepImages,
                                 Principal principal,
                                 Model model) throws IOException {

            // 대표 이미지 저장
            if (!mainImage.isEmpty()) {
                String savedName = fileService.saveFile(mainImage);
                recipeDTO.setMainImageUrl(savedName);
            }

            // 요리 순서 이미지 저장
            List<RecipeStepDTO> stepDTOs = recipeDTO.getSteps();
            if (stepDTOs != null) {
                for (int i = 0; i < stepDTOs.size(); i++) {
                    MultipartFile file = stepImages.get(i);
                    if (!file.isEmpty()) {
                        String savedName = fileService.saveFile(file);
                        stepDTOs.get(i).setImageName(savedName);
                    }
                }
            }

            recipeService.registerRecipe(recipeDTO, principal.getName());

            return "redirect:/recipe/list";
    }

    @GetMapping("/list")
    public void recipeList(Model model){
        List<RecipeDTO> recipeList = recipeService.getAllRecipe();
        model.addAttribute("recipeList", recipeList);
    }

}
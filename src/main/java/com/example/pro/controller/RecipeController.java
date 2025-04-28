package com.example.pro.controller;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.RecipeIngredientsDTO;
import com.example.pro.dto.RecipeStepDTO;
import com.example.pro.entity.ReviewEntity;
import com.example.pro.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/recipe")
@Log4j2
@RequiredArgsConstructor
public class RecipeController {

    private final FileService fileService;
    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final RecipeStepService recipeStepService;
    private final RecipeIngredientsServiceImpl recipeIngredientsService;
    private final ReviewService reviewService;

    @GetMapping("/register")
    public void recipeRegister(Model model) {
        model.addAttribute("recipeDTO", new RecipeDTO());
        model.addAttribute("categories", List.of("밥", "국", "메인반찬", "밑반찬", "면"));
        model.addAttribute("ingredients", ingredientService.findAllIngredient());
    }


    @PostMapping("/register")
    public String recipeRegister(@ModelAttribute RecipeDTO recipeDTO,
                                 @AuthenticationPrincipal PrincipalDetail principalDetail,
                                 @RequestParam Map<String, String> paramMap,
                                 MultipartHttpServletRequest request,
                                 Model model) throws IOException {

        //1. 로그인 사용자 정보 세팅
        if (principalDetail != null) {
            recipeDTO.setUsername(principalDetail.getUsername());
        }
//        recipeDTO.setUsername("hong");

        // 2. steps[0].imagePath → 대표 이미지
        MultipartFile mainImage = request.getFile("steps[0].imageFile");
        if (mainImage != null && !mainImage.isEmpty()) {
            String mainImageUrl = fileService.saveFile(mainImage);
            recipeDTO.setMainImagePath(mainImageUrl); // RecipeDTO에 setter 있어야 함
        }

        // 3. steps[1..].stepImage → 요리 순서 이미지
        List<MultipartFile> recipeStepImages = request.getFileMap().entrySet().stream()
                .filter(entry -> entry.getKey().matches("steps\\[\\d+\\]\\.imageFile"))
                .sorted(Comparator.comparing(e -> extractStepIndex(e.getKey()))) // 순서 정렬
                .skip(1) // steps[0]은 대표 이미지니까 제외
                .map(Map.Entry::getValue)
                .toList();

        // 4. 레시피 저장 서비스 호출
        recipeService.registerRecipe(recipeDTO, recipeStepImages, paramMap);

            return "redirect:/recipe/list";
    }

    @GetMapping("/list")
    public String recipeList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "searchType", defaultValue = "title") String searchType,
            @RequestParam(value = "category", required = false) String category,
            Model model){

        List<RecipeDTO> recipeList;

        if (category != null && !category.isEmpty()) {
            if (searchType != null && keyword != null && !keyword.isEmpty()) {
                recipeList = recipeService.searchByCategoryAndKeyword(category, searchType, keyword);
            } else {
                recipeList = recipeService.findByCategory(category);
            }
        } else {
            if (searchType != null && keyword != null && !keyword.isEmpty()) {
                recipeList = recipeService.searchRecipes(searchType, keyword);
            } else {
                recipeList = recipeService.getAllRecipe();
            }
        }
        model.addAttribute("recipeList", recipeList);
        model.addAttribute("category", category);
        return "/recipe/list";
    }

    @GetMapping("/view")
    public void recipeRead(@RequestParam("id") Long recipe_id,
                            @RequestParam(value = "sort", required = false, defaultValue = "newest") String sortBy,
                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                            @RequestParam(value = "replyError", required = false) String replyError,
                            Model model) {
        RecipeDTO recipe = recipeService.getRecipeById(recipe_id);
        List<RecipeStepDTO> recipeSteps = recipeStepService.getRecipeStepByRecipeId(recipe.getId());
        List<RecipeIngredientsDTO> recipeIngredientsDTOList = recipeIngredientsService.getRecipeIngredientsbyRecipeId(recipe.getId());

        // Create Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);

        // Get paginated reviews
        Page<ReviewEntity> reviewPage = reviewService.getReviewsByRecipePaged(recipe_id, sortBy, pageable);

        double averageRating = reviewService.calculateAverageRating(recipe_id);
        int reviewCount = (int) reviewPage.getTotalElements();

        model.addAttribute("recipe", recipe);
        model.addAttribute("recipeSteps", recipeSteps);
        model.addAttribute("recipeIngredientsDTOList", recipeIngredientsDTOList);
        model.addAttribute("categories", List.of("밥", "국", "메인반찬", "밑반찬", "면"));
        model.addAttribute("ingredients", ingredientService.findAllIngredient());
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("currentSort", sortBy);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        }

    @GetMapping("/update")
    public String recipeUpdateview(@RequestParam("id") Long recipe_id, Model model) {
        RecipeDTO recipe = recipeService.getRecipeById(recipe_id);
        List<RecipeStepDTO> recipeSteps = recipeStepService.getRecipeStepByRecipeId(recipe.getId());
        List<RecipeIngredientsDTO> recipeIngredientsDTOList = recipeIngredientsService.getRecipeIngredientsbyRecipeId(recipe.getId());

        model.addAttribute("recipe", recipe);
        model.addAttribute("recipeSteps", recipeSteps);
        model.addAttribute("recipeIngredientsDTOList", recipeIngredientsDTOList);
        model.addAttribute("categories", List.of("밥", "국", "메인반찬", "밑반찬", "면"));
        model.addAttribute("ingredients", ingredientService.findAllIngredient());

        return "/recipe/update";
    }


    @PostMapping("/update")
    public String recipeUpdate(@ModelAttribute RecipeDTO recipeDTO,
                               @AuthenticationPrincipal PrincipalDetail principalDetail,
                               @RequestParam Map<String, String> paramMap,
                               MultipartHttpServletRequest request,
                               Model model) throws IOException {

        // 1. 기존 대표 이미지 경로 가져오기
        String currentMainImage = paramMap.get("currentMainImage");

        // 2. 대표 이미지 파일 처리
        MultipartFile mainImageFile = request.getFile("mainImageFile");
        if (mainImageFile != null && !mainImageFile.isEmpty()) {
            // 새 파일 업로드 했으면 저장
            String newMainImagePath = saveFile(mainImageFile);
            recipeDTO.setMainImagePath(newMainImagePath);
        } else {
            // 새 파일 없으면 기존 파일 유지
            recipeDTO.setMainImagePath(currentMainImage);
        }

        // 3. 요리 순서 이미지들 처리
        List<RecipeStepDTO> updatedSteps = new ArrayList<>();

        int stepIndex = 0;
        while (true) {
            String contentKey = "steps[" + stepIndex + "].stepContent";
            String currentImageKey = "steps[" + stepIndex + "].currentImage";
            MultipartFile stepImageFile = request.getFile("steps[" + stepIndex + "].stepImage");

            if (!paramMap.containsKey(contentKey)) {
                break;
            }

            RecipeStepDTO stepDTO = new RecipeStepDTO();
            stepDTO.setContent(paramMap.get(contentKey));

            if (stepImageFile != null && !stepImageFile.isEmpty()) {
                String stepImagePath = saveFile(stepImageFile);
                stepDTO.setImagePath(stepImagePath);
            } else {
                stepDTO.setImagePath(paramMap.get(currentImageKey));
            }

            updatedSteps.add(stepDTO);
            stepIndex++;
        }

        recipeDTO.setSteps(updatedSteps);

        // 4. Service 호출
        recipeService.updateRecipe(recipeDTO, principalDetail);

        return "redirect:/recipe/view?id=" + recipeDTO.getId();
    }


    private int extractStepIndex(String key) {
        Matcher matcher = Pattern.compile("steps\\[(\\d+)]\\.stepImage").matcher(key);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return Integer.MAX_VALUE;
    }

    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "D:/JMT/Project01/project01/src/main/resources/static/assets/uploads/"; // 경로 맞춰줘야 함
        String originalFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String newFilename = uuid + "_" + originalFilename;

        File saveFile = new File(uploadDir + newFilename);
        file.transferTo(saveFile);

        return newFilename;
    }


}
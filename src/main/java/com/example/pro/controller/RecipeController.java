package com.example.pro.controller;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.dto.*;
import com.example.pro.entity.*;
import com.example.pro.repository.CartRepository;
import com.example.pro.repository.IngredientRepository;
import com.example.pro.repository.RecipeIngredientsRepository;
import com.example.pro.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
import java.nio.file.Files;
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
    private final IngredientRepository ingredientRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final RecipeIngredientsRepository recipeIngredientsRepository;

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
        UserEntity userEntity = principalDetail.getUser(); // PrincipalDetail에서 유저 정보를 가져옴
        recipeDTO.setUsername(userEntity.getUsername()); // 로그인된 유저의 정보로 recipeDTO 설정

        // 1. 로그인 사용자 정보 세팅
        if (principalDetail != null) {
            recipeDTO.setUsername(principalDetail.getUsername());
        }

        // 2. mainImageFile → 대표 이미지
        MultipartFile mainImage = request.getFile("mainImageFile");
        if (mainImage != null && !mainImage.isEmpty()) {
            String mainImageUrl = fileService.saveFile(mainImage);
            recipeDTO.setMainImagePath(mainImageUrl); // RecipeDTO에 setter 있어야 함
        }

        // 3. steps[0..].stepImage → 요리 순서 이미지
        List<MultipartFile> recipeStepImages = request.getFileMap().entrySet().stream()
                .filter(entry -> entry.getKey().matches("steps\\[\\d+\\]\\.imageFile"))
                .sorted(Comparator.comparing(e -> extractStepIndex(e.getKey()))) // 순서 정렬
                .map(Map.Entry::getValue)
                .toList();

        // 4. 레시피 저장 서비스 호출
        recipeService.registerRecipe(recipeDTO, recipeStepImages, paramMap); // 레시피 저장 및 카트 저장은 서비스에서 처리

        // 레시피 등록 후 재료 정보 업데이트
        recipeDTO.setSteps(recipeStepService.getRecipeStepByRecipeId(recipeDTO.getId()));
        recipeDTO.setRecipeIngredients(recipeIngredientsService.getRecipeIngredientsbyRecipeId(recipeDTO.getId()));

        // 5. 추가한 재료가 있으면 장바구니리스트로, 없으면 레시피리스트로
        // 재료가 있는지 확인하고 카트로 보내기
        return "redirect:/cart/list"; // 장바구니로

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
        log.info("recipeDTO : " + recipeDTO);
        log.info("paramMap.toString()" + paramMap.toString() );
        // 1. 기존 대표 이미지 경로 가져오기
        String currentMainImage = paramMap.get("currentMainImage");

        // 2. 대표 이미지 파일 처리
        MultipartFile mainImageFile = request.getFile("mainImageFile");
        if (mainImageFile != null && !mainImageFile.isEmpty()) {
            // 새 파일 업로드 했으면 저장
            String newMainImagePath = fileService.saveFile(mainImageFile);
            recipeDTO.setMainImagePath(newMainImagePath);
            removeFile(currentMainImage);
        } else {
            // 새 파일 없으면 기존 파일 유지
            recipeDTO.setMainImagePath(currentMainImage);
        }
        // 3. 재료 처리
        long ingredientCount = paramMap.keySet().stream()
                .filter(key -> key.matches("ingredients\\[\\d+\\]\\.ingredientName"))
                .count();
        log.info("ingredientCount : " + ingredientCount);

        // 새로 만들어질 리스트
        List<RecipeIngredientsDTO> updatedIngredients = new ArrayList<>();


        for (int i = 0; i < ingredientCount; i++){
            String ingredientNameKey = "ingredients[" + i + "].ingredientName";
            String ingredientQuantityKey = "ingredients[" + i + "].quantity";

            RecipeIngredientsDTO ingredientsDTO = new RecipeIngredientsDTO();

            ingredientsDTO.setIngredient(ingredientService.entityToDto(ingredientRepository.findByIngredientName(paramMap.get(ingredientNameKey)).get()));
            ingredientsDTO.setQuantity(Long.parseLong(paramMap.get(ingredientQuantityKey)));
            ingredientsDTO.setRecipeId(recipeDTO.getId());

            updatedIngredients.add(ingredientsDTO);

        }

        // 업데이트된 리스트를 DTO에 세팅
        recipeDTO.setRecipeIngredients(updatedIngredients);

        // 4. 요리 순서 이미지들 처리
        long stepCount = paramMap.keySet().stream()
                .filter(key -> key.matches("steps\\[\\d+\\]\\.stepContent"))
                .count();
        log.info("stepCount : " + stepCount);
        List<RecipeStepDTO> updatedSteps = new ArrayList<>();


        for (int i = 0 ; i < stepCount ; i++) {
            String contentKey = "steps[" + i + "].stepContent";
            String currentImageKey = "steps[" + i + "].currentImage";
            MultipartFile stepImageFile = request.getFile("steps[" + i + "].stepImage");

            RecipeStepDTO stepDTO = new RecipeStepDTO();

            log.info("contentKey : " + contentKey);
            log.info("currentImageKey : " + currentImageKey);
            log.info("stepImageFile : " + stepImageFile);


            // stepDTO 셋팅
            stepDTO.setRecipeId(recipeDTO.getId());
            log.info("stepDTO.getRecipeId() : " + stepDTO.getRecipeId());
            stepDTO.setStepNumber(i + 1);
            log.info("stepDTO.getStepNumber() : " + stepDTO.getStepNumber());
            stepDTO.setContent(paramMap.get(contentKey));
            log.info("stepDTO.getContent() : " + stepDTO.getContent());

                // 이미지 주소 설정
            if (stepImageFile != null && !stepImageFile.isEmpty()) {
                String stepImagePath = fileService.saveFile(stepImageFile);
                stepDTO.setImagePath(stepImagePath);
                removeFile(paramMap.get(currentImageKey));
            } else {
                stepDTO.setImagePath(paramMap.get(currentImageKey));
            }

            updatedSteps.add(stepDTO);

        }

        // 작성자 설정
        recipeDTO.setUsername(principalDetail.getUsername());

        recipeDTO.setSteps(updatedSteps);

        // 4. Service 호출
        recipeService.updateRecipe(recipeDTO);

        return "redirect:/recipe/view?id=" + recipeDTO.getId();
    }

    @GetMapping("/delete")
    public String recipeDelete(@RequestParam("id") Long recipe_id, Model model) {
        // 대표이미지&요리순서이미지 모두 파일 삭제
        removeFile(recipeService.getRecipeById(recipe_id).getMainImagePath());
        for(int i = 0; i < recipeStepService.getRecipeStepByRecipeId(recipe_id).size(); i++) {
            removeFile(recipeStepService.getRecipeStepByRecipeId(recipe_id).get(i).getImagePath());
        }
        recipeService.deleteRecipe(recipe_id);
        return "redirect:/recipe/list";
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

    private String removeFile(String filepath){
        String uploadDir = "D:/JMT/Project01/project01/src/main/resources/static";
        Resource resource = new FileSystemResource(uploadDir + filepath);
        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;
        try{
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed=resource.getFile().delete();
        }catch (Exception e){
            e.printStackTrace();
        }
        resultMap.put("result",removed);
        return "/upload/uploadForm";

    }


}
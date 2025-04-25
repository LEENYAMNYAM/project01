package com.example.pro.controller;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.ReviewDTO;
import com.example.pro.entity.ReviewEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.service.FileService;
import com.example.pro.service.RecipeService;
import com.example.pro.service.ReviewService;
import com.example.pro.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@Log4j2
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private FileService fileService;

    // 리뷰 목록 페이지
    @GetMapping("/list/{recipeId}")
    public String listReviews(@PathVariable Long recipeId, Model model) {
        try {
            // 레시피 존재 여부 확인
            RecipeDTO recipe = recipeService.getRecipeById(recipeId);
            if (recipe == null) {
                log.warn("Recipe not found with id: " + recipeId);
                return "redirect:/recipe/list";
            }

            List<ReviewEntity> reviews = reviewService.getReviewsByRecipe(recipeId);

            model.addAttribute("reviews", reviews);
            model.addAttribute("recipe", recipe);
            model.addAttribute("recipeId", recipeId);

            return "reviews/list";
        } catch (Exception e) {
            log.error("Error listing reviews: " + e.getMessage());
            return "redirect:/recipe/list";
        }
    }

    // 리뷰 작성 폼
    @GetMapping("/register/{recipeId}")
    public String reviewForm(@PathVariable Long recipeId, Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        try {
            // 로그인 확인
            if (principalDetail == null) {
                return "redirect:/login";
            }

            // 레시피 존재 여부 확인
            RecipeDTO recipe = recipeService.getRecipeById(recipeId);
            if (recipe == null) {
                log.warn("Recipe not found with id: " + recipeId);
                return "redirect:/recipe/list";
            }

            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setRecipeId(recipeId);

            model.addAttribute("recipe", recipe);
            model.addAttribute("reviewDTO", reviewDTO);

            return "reviews/reviewregister";
        } catch (Exception e) {
            log.error("Error accessing review form: " + e.getMessage());
            return "redirect:/recipe/list";
        }
    }

    // 리뷰 등록 처리
    @PostMapping("/register")
    public String addReview(@ModelAttribute ReviewDTO reviewDTO,
                           @RequestParam(value = "image", required = false) MultipartFile image,
                           @AuthenticationPrincipal PrincipalDetail principalDetail,
                           Model model) {

        // 로그인한 사용자 정보 설정
        if (principalDetail != null) {
            UserEntity currentUser = principalDetail.getUser();
            reviewDTO.setBuyer(currentUser);
        } else {
            // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        // 입력값 검증
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            log.warn("Invalid rating value: " + reviewDTO.getRating());
            model.addAttribute("error", "별점은 1~5 사이의 값이어야 합니다.");
            model.addAttribute("reviewDTO", reviewDTO);
            model.addAttribute("recipe", recipeService.getRecipeById(reviewDTO.getRecipeId()));
            return "reviews/reviewregister";
        }

        if (reviewDTO.getContent() == null || reviewDTO.getContent().trim().isEmpty()) {
            log.warn("Empty review content");
            model.addAttribute("error", "리뷰 내용을 입력해주세요.");
            model.addAttribute("reviewDTO", reviewDTO);
            model.addAttribute("recipe", recipeService.getRecipeById(reviewDTO.getRecipeId()));
            return "reviews/reviewregister";
        }

        if (reviewDTO.getContent().length() > 1000) {
            log.warn("Review content too long: " + reviewDTO.getContent().length());
            model.addAttribute("error", "리뷰 내용은 1000자 이내로 작성해주세요.");
            model.addAttribute("reviewDTO", reviewDTO);
            model.addAttribute("recipe", recipeService.getRecipeById(reviewDTO.getRecipeId()));
            return "reviews/reviewregister";
        }

        // 이미지 파일이 있으면 저장
        if (image != null && !image.isEmpty()) {
            try {
                String imagePath = fileService.saveFile(image);
                reviewDTO.setImagePath(imagePath);
            } catch (Exception e) {
                log.error("이미지 저장 중 오류 발생: " + e.getMessage());
            }
        }

        reviewService.registerReview(reviewDTO);
        return "redirect:/reviews/list/" + reviewDTO.getRecipeId();
    }

    // 리뷰 상세 보기
    @GetMapping("/view/{id}")
    public String viewReview(@PathVariable Long id, Model model) {
        try {
            ReviewEntity review = reviewService.readReview(id);
            if (review == null) {
                log.warn("Review not found with id: " + id);
                return "redirect:/recipe/list";
            }

            RecipeDTO recipe = recipeService.getRecipeById(review.getRecipe().getId());

            model.addAttribute("review", review);
            model.addAttribute("recipe", recipe);

            return "reviews/view";
        } catch (Exception e) {
            log.error("Error viewing review: " + e.getMessage());
            return "redirect:/recipe/list";
        }
    }

    // 리뷰 수정 폼
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        try {
            // 로그인 확인
            if (principalDetail == null) {
                return "redirect:/login";
            }

            ReviewEntity review = reviewService.readReview(id);
            if (review == null) {
                log.warn("Review not found with id: " + id);
                return "redirect:/recipe/list";
            }

            // 리뷰 작성자 확인 (본인 리뷰만 수정 가능)
            if (review.getBuyer() == null ||
                !review.getBuyer().getUsername().equals(principalDetail.getUsername())) {
                log.warn("Unauthorized review update form access: " + principalDetail.getUsername());
                return "redirect:/reviews/list/" + review.getRecipe().getId();
            }

            RecipeDTO recipe = recipeService.getRecipeById(review.getRecipe().getId());
            ReviewDTO reviewDTO = reviewService.entityToDto(review);

            model.addAttribute("reviewDTO", reviewDTO);
            model.addAttribute("recipe", recipe);
            model.addAttribute("review", review);  // Add the review entity to the model

            return "reviews/update";
        } catch (Exception e) {
            log.error("Error accessing update form: " + e.getMessage());
            return "redirect:/recipe/list";
        }
    }

    // 리뷰 수정 처리
    @PostMapping("/update/{id}")
    public String updateReview(@PathVariable Long id,
                              @ModelAttribute ReviewDTO reviewDTO,
                              @RequestParam(value = "image", required = false) MultipartFile image,
                              @RequestParam(value = "deleteImage", required = false) Boolean deleteImage,
                              @AuthenticationPrincipal PrincipalDetail principalDetail,
                              Model model) {

        // 로그인 확인
        if (principalDetail == null) {
            return "redirect:/login";
        }

        // 기존 리뷰 정보 가져오기
        ReviewEntity existingReview = reviewService.readReview(id);

        // 리뷰 작성자 확인 (본인 리뷰만 수정 가능)
        if (existingReview.getBuyer() == null ||
            !existingReview.getBuyer().getUsername().equals(principalDetail.getUsername())) {
            log.warn("Unauthorized review update attempt: " + principalDetail.getUsername());
            return "redirect:/reviews/list/" + existingReview.getRecipe().getId();
        }

        // 입력값 검증
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            log.warn("Invalid rating value: " + reviewDTO.getRating());
            model.addAttribute("error", "별점은 1~5 사이의 값이어야 합니다.");
            model.addAttribute("reviewDTO", reviewDTO);
            model.addAttribute("recipe", recipeService.getRecipeById(existingReview.getRecipe().getId()));
            model.addAttribute("review", existingReview);
            return "reviews/update";
        }

        if (reviewDTO.getContent() == null || reviewDTO.getContent().trim().isEmpty()) {
            log.warn("Empty review content");
            model.addAttribute("error", "리뷰 내용을 입력해주세요.");
            model.addAttribute("reviewDTO", reviewDTO);
            model.addAttribute("recipe", recipeService.getRecipeById(existingReview.getRecipe().getId()));
            model.addAttribute("review", existingReview);
            return "reviews/update";
        }

        if (reviewDTO.getContent().length() > 1000) {
            log.warn("Review content too long: " + reviewDTO.getContent().length());
            model.addAttribute("error", "리뷰 내용은 1000자 이내로 작성해주세요.");
            model.addAttribute("reviewDTO", reviewDTO);
            model.addAttribute("recipe", recipeService.getRecipeById(existingReview.getRecipe().getId()));
            model.addAttribute("review", existingReview);
            return "reviews/update";
        }

        // 이미지 삭제 체크박스가 선택된 경우
        if (deleteImage != null && deleteImage) {
            reviewDTO.setImagePath(null);
        }
        // 새 이미지가 업로드된 경우
        else if (image != null && !image.isEmpty()) {
            try {
                String imagePath = fileService.saveFile(image);
                reviewDTO.setImagePath(imagePath);
            } catch (Exception e) {
                log.error("이미지 저장 중 오류 발생: " + e.getMessage());
            }
        }
        // 이미지 변경이 없는 경우 기존 이미지 유지
        else if (reviewDTO.getImagePath() == null) {
            reviewDTO.setImagePath(existingReview.getImagePath());
        }

        reviewService.updateReview(id, reviewDTO);
        return "redirect:/reviews/view/" + id;
    }

    // 리뷰 삭제
    @GetMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        // 로그인 확인
        if (principalDetail == null) {
            return "redirect:/login";
        }

        ReviewEntity review = reviewService.readReview(id);
        Long recipeId = review.getRecipe().getId();

        // 리뷰 작성자 확인 (본인 리뷰만 삭제 가능)
        if (review.getBuyer() == null ||
            !review.getBuyer().getUsername().equals(principalDetail.getUsername())) {
            log.warn("Unauthorized review delete attempt: " + principalDetail.getUsername());
            return "redirect:/reviews/list/" + recipeId;
        }

        reviewService.deleteReview(id);
        return "redirect:/reviews/list/" + recipeId;
    }

    // 리뷰에 답변 추가
    @PostMapping("/reply/{id}")
    public String addReplyToReview(@PathVariable Long id,
                                  @RequestParam("reply") String reply,
                                  @AuthenticationPrincipal PrincipalDetail principalDetail,
                                  Model model) {
        try {
            // 로그인 확인
            if (principalDetail == null) {
                return "redirect:/login";
            }

            ReviewEntity review = reviewService.readReview(id);
            Long recipeId = review.getRecipe().getId();

            // 입력값 검증
            if (reply == null || reply.trim().isEmpty()) {
                log.warn("Empty reply content");
                model.addAttribute("replyError", "답변 내용을 입력해주세요.");
                return "redirect:/recipe/view?id=" + recipeId + "&replyError=답변 내용을 입력해주세요.";
            }

            if (reply.length() > 1000) {
                log.warn("Reply content too long: " + reply.length());
                return "redirect:/recipe/view?id=" + recipeId + "&replyError=답변 내용은 1000자 이내로 작성해주세요.";
            }

            // 레시피 작성자 확인 (레시피 작성자만 답변 가능)
            reviewService.addReplyToReview(id, reply, principalDetail.getUsername());

            return "redirect:/recipe/view?id=" + recipeId;
        } catch (RuntimeException e) {
            log.error("Error adding reply to review: " + e.getMessage());
            return "redirect:/recipe/list";
        }
    }
}

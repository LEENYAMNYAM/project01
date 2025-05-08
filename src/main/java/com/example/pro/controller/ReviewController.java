package com.example.pro.controller;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.dto.RecipeDTO;
import com.example.pro.dto.ReviewDTO;
import com.example.pro.entity.ReviewEntity;
import com.example.pro.entity.ReviewReportEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.service.ContentFilterService;
import com.example.pro.service.FileService;
import com.example.pro.service.RecipeService;
import com.example.pro.service.ReviewReportService;
import com.example.pro.service.ReviewService;
import com.example.pro.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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
    @Autowired
    private ContentFilterService contentFilterService;
    @Autowired
    private ReviewReportService reviewReportService;

    // 리뷰 목록 페이지
    @GetMapping("/list/{recipeId}")
    public String listReviews(@PathVariable Long recipeId,
                             @RequestParam(required = false) String searchType,
                             @RequestParam(required = false) String keyword,
                             Model model) {
        try {
            // 레시피 존재 여부 확인
            RecipeDTO recipe = recipeService.getRecipeById(recipeId);
            if (recipe == null) {
                log.warn("Recipe not found with id: " + recipeId);
                return "redirect:/recipe/list";
            }

            List<ReviewEntity> reviews;

            // 검색 조건이 있는 경우
            if (keyword != null && !keyword.trim().isEmpty()) {
                if ("writer".equals(searchType)) {
                    // 작성자로 검색
                    reviews = reviewService.getReviewsByRecipeAndWriter(recipeId, keyword);
                } else {
                    // 내용으로 검색 (기본값)
                    reviews = reviewService.getReviewsByRecipeAndContent(recipeId, keyword);
                }
            } else {
                // 검색 조건이 없는 경우 전체 리뷰 조회
                reviews = reviewService.getReviewsByRecipe(recipeId);
            }

            model.addAttribute("reviewList", reviews);
            model.addAttribute("recipe", recipe);
            model.addAttribute("recipeId", recipeId);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);

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

        if (reviewDTO.getTitle() == null || reviewDTO.getTitle().trim().isEmpty()) {
            log.warn("Empty review title");
            model.addAttribute("error", "리뷰 제목을 입력해주세요.");
            model.addAttribute("reviewDTO", reviewDTO);
            model.addAttribute("recipe", recipeService.getRecipeById(reviewDTO.getRecipeId()));
            return "reviews/reviewregister";
        }

        if (reviewDTO.getTitle().length() > 100) {
            log.warn("Review title too long: " + reviewDTO.getTitle().length());
            model.addAttribute("error", "리뷰 제목은 100자 이내로 작성해주세요.");
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

        // 콘텐츠 필터링 검사
        ContentFilterService.ContentFilterResult filterResult = 
            contentFilterService.validateReview(reviewDTO, principalDetail.getUser());

        if (!filterResult.isValid()) {
            log.warn("Content filter validation failed: " + filterResult.getErrorMessage());
            model.addAttribute("error", filterResult.getErrorMessage());
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

        if (reviewDTO.getTitle() == null || reviewDTO.getTitle().trim().isEmpty()) {
            log.warn("Empty review title");
            model.addAttribute("error", "리뷰 제목을 입력해주세요.");
            model.addAttribute("reviewDTO", reviewDTO);
            model.addAttribute("recipe", recipeService.getRecipeById(existingReview.getRecipe().getId()));
            model.addAttribute("review", existingReview);
            return "reviews/update";
        }

        if (reviewDTO.getTitle().length() > 100) {
            log.warn("Review title too long: " + reviewDTO.getTitle().length());
            model.addAttribute("error", "리뷰 제목은 100자 이내로 작성해주세요.");
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

        // 콘텐츠 필터링 검사
        ContentFilterService.ContentFilterResult filterResult = 
            contentFilterService.validateReview(reviewDTO, principalDetail.getUser());

        if (!filterResult.isValid()) {
            log.warn("Content filter validation failed: " + filterResult.getErrorMessage());
            model.addAttribute("error", filterResult.getErrorMessage());
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

    // 리뷰 좋아요 토글
    @PostMapping("/like/{id}")
    @ResponseBody
    public Map<String, Object> toggleReviewLike(@PathVariable Long id,
                                               @AuthenticationPrincipal PrincipalDetail principalDetail) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 로그인 확인
            if (principalDetail == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return response;
            }

            // 좋아요 토글 처리
            boolean isLiked = reviewService.toggleReviewLike(id, principalDetail.getUsername());
            int likesCount = reviewService.getReviewLikesCount(id);

            response.put("success", true);
            response.put("isLiked", isLiked);
            response.put("likesCount", likesCount);

        } catch (Exception e) {
            log.error("Error toggling review like: " + e.getMessage());
            response.put("success", false);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
        }

        return response;
    }

    // 리뷰 신고 폼
    @GetMapping("/report/{id}")
    public String reportReviewForm(@PathVariable Long id, Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        try {
            // 로그인 확인
            if (principalDetail == null) {
                return "redirect:/login";
            }

            // 리뷰 존재 여부 확인
            ReviewEntity review = reviewService.readReview(id);
            if (review == null) {
                log.warn("Review not found with id: " + id);
                return "redirect:/recipe/list";
            }

            // 자신의 리뷰는 신고할 수 없음
            if (review.getBuyer() != null && 
                review.getBuyer().getUsername().equals(principalDetail.getUsername())) {
                log.warn("User attempted to report their own review: " + principalDetail.getUsername());
                return "redirect:/reviews/view/" + id + "?error=자신의 리뷰는 신고할 수 없습니다.";
            }

            // 이미 신고한 리뷰인지 확인
            if (reviewReportService.hasUserReportedReview(id, principalDetail.getUsername())) {
                log.warn("User has already reported this review: " + principalDetail.getUsername());
                return "redirect:/reviews/view/" + id + "?error=이미 신고한 리뷰입니다.";
            }

            RecipeDTO recipe = recipeService.getRecipeById(review.getRecipe().getId());

            model.addAttribute("review", review);
            model.addAttribute("recipe", recipe);

            return "reviews/report";
        } catch (Exception e) {
            log.error("Error accessing report form: " + e.getMessage());
            return "redirect:/recipe/list";
        }
    }

    // 리뷰 신고 처리
    @PostMapping("/report/{id}")
    public String reportReview(@PathVariable Long id, 
                              @RequestParam String reason,
                              @AuthenticationPrincipal PrincipalDetail principalDetail,
                              Model model) {
        try {
            // 로그인 확인
            if (principalDetail == null) {
                return "redirect:/login";
            }

            // 리뷰 존재 여부 확인
            ReviewEntity review = reviewService.readReview(id);
            if (review == null) {
                log.warn("Review not found with id: " + id);
                return "redirect:/recipe/list";
            }

            // 자신의 리뷰는 신고할 수 없음
            if (review.getBuyer() != null && 
                review.getBuyer().getUsername().equals(principalDetail.getUsername())) {
                log.warn("User attempted to report their own review: " + principalDetail.getUsername());
                return "redirect:/reviews/view/" + id + "?error=자신의 리뷰는 신고할 수 없습니다.";
            }

            // 이미 신고한 리뷰인지 확인
            if (reviewReportService.hasUserReportedReview(id, principalDetail.getUsername())) {
                log.warn("User has already reported this review: " + principalDetail.getUsername());
                return "redirect:/reviews/view/" + id + "?error=이미 신고한 리뷰입니다.";
            }

            // 신고 사유 검증
            if (reason == null || reason.trim().isEmpty()) {
                log.warn("Empty report reason");
                return "redirect:/reviews/report/" + id + "?error=신고 사유를 입력해주세요.";
            }

            // 신고 처리
            reviewReportService.reportReview(id, reason, principalDetail.getUsername());

            return "redirect:/reviews/view/" + id + "?success=리뷰가 신고되었습니다. 관리자 검토 후 조치가 취해질 것입니다.";
        } catch (Exception e) {
            log.error("Error reporting review: " + e.getMessage());
            return "redirect:/reviews/view/" + id + "?error=신고 처리 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    // 신고된 리뷰 목록 (관리자용)
    @GetMapping("/reported")
    public String reportedReviews(Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        try {
            // 관리자 권한 확인
            if (principalDetail == null || !principalDetail.getUser().getRole().contains("ADMIN")) {
                log.warn("Unauthorized access to reported reviews: " + 
                         (principalDetail != null ? principalDetail.getUsername() : "anonymous"));
                return "redirect:/";
            }

            // 신고된 리뷰 목록 조회
            List<ReviewReportEntity> pendingReports = reviewReportService.getPendingReports();

            model.addAttribute("pendingReports", pendingReports);

            return "reviews/reported";
        } catch (Exception e) {
            log.error("Error listing reported reviews: " + e.getMessage());
            return "redirect:/";
        }
    }

    // 신고된 리뷰 처리 (관리자용)
    @PostMapping("/moderate/{reportId}")
    public String moderateReport(@PathVariable Long reportId,
                               @RequestParam String action,
                               @RequestParam(required = false) String comment,
                               @AuthenticationPrincipal PrincipalDetail principalDetail) {
        try {
            // 관리자 권한 확인
            if (principalDetail == null || !principalDetail.getUser().getRole().contains("ADMIN")) {
                log.warn("Unauthorized attempt to moderate report: " + 
                         (principalDetail != null ? principalDetail.getUsername() : "anonymous"));
                return "redirect:/";
            }

            // 액션 검증
            if (!action.equals("APPROVED") && !action.equals("REJECTED") && !action.equals("EDIT_REQUESTED")) {
                log.warn("Invalid moderation action: " + action);
                return "redirect:/reviews/reported?error=잘못된 처리 액션입니다.";
            }

            // 신고 처리
            reviewReportService.moderateReport(reportId, action, principalDetail.getUsername(), comment);

            return "redirect:/reviews/reported?success=신고가 처리되었습니다.";
        } catch (Exception e) {
            log.error("Error moderating report: " + e.getMessage());
            return "redirect:/reviews/reported?error=신고 처리 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}

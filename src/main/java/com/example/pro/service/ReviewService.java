package com.example.pro.service;

import com.example.pro.dto.ReviewDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    void registerReview(ReviewDTO reviewDTO);
    ReviewEntity readReview(Long id);
    void updateReview(Long id, ReviewDTO reviewDTO);
    void deleteReview(Long id);
    void addReplyToReview(Long id, String reply, String username);
    List<ReviewEntity> getReviewsByRecipe(Long recipeId);
    List<ReviewEntity> getReviewsByRecipeSorted(Long recipeId, String sortBy);
    Page<ReviewEntity> getReviewsByRecipePaged(Long recipeId, String sortBy, Pageable pageable);
    RecipeEntity getRecipeEntityById(Long recipeId);
    double calculateAverageRating(Long recipeId);

    default ReviewEntity dtoToEntity(ReviewDTO reviewDTO, RecipeEntity recipe) {
        ReviewEntity review = new ReviewEntity();
        review.setBuyer(reviewDTO.getBuyer());
        review.setViewer(reviewDTO.getViewer());
        review.setContent(reviewDTO.getContent());
        review.setRating(reviewDTO.getRating());
        review.setImagePath(reviewDTO.getImagePath());
        review.setId(reviewDTO.getId());
        review.setRecipe(recipe);
        return review;
    }
    default ReviewDTO entityToDto(ReviewEntity reviewEntity) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setBuyer(reviewEntity.getBuyer());
        reviewDTO.setViewer(reviewEntity.getViewer());
        reviewDTO.setContent(reviewEntity.getContent());
        reviewDTO.setRating(reviewEntity.getRating());
        reviewDTO.setImagePath(reviewEntity.getImagePath());
        reviewDTO.setId(reviewEntity.getId());
        reviewDTO.setRecipeId(reviewEntity.getRecipe().getId());
        reviewDTO.setReply(reviewEntity.getReply());
        reviewDTO.setReplyDate(reviewEntity.getReplyDate());
        return reviewDTO;
    }
}

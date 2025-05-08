package com.example.pro.service;

import com.example.pro.dto.ReviewDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

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

    // Search functionality
    List<ReviewEntity> getReviewsByRecipeAndWriter(Long recipeId, String writer);
    List<ReviewEntity> getReviewsByRecipeAndContent(Long recipeId, String content);

    // Review like functionality
    boolean toggleReviewLike(Long reviewId, String username);
    boolean hasUserLikedReview(Long reviewId, String username);
    int getReviewLikesCount(Long reviewId);
    void updateReviewLikesCount(Long reviewId);

    // Rating distribution functionality
    int[] calculateRatingDistribution(Long recipeId);

    // Rating trend analysis functionality
    Map<String, Double> calculateRatingTrend(Long recipeId);

    // Comparative rating metrics functionality
    double calculateCategoryAverageRating(String category);
    List<RecipeEntity> findSimilarRecipes(Long recipeId, int limit);
    int calculateRecipeRank(Long recipeId);

    // User profile integration functionality
    List<ReviewEntity> getReviewsByUsername(String username);

    // User review dashboard functionality
    double calculateUserAverageRating(String username);
    Map<Integer, Integer> calculateUserRatingDistribution(String username);
    int countUserReviews(String username);
    int countUserReviewLikes(String username);
    int countUserReviewReplies(String username);
    List<ReviewEntity> getMostLikedReviewsByUser(String username, int limit);
    Map<String, Integer> calculateUserReviewsByCategory(String username);

    // Review history tracking functionality
    List<ReviewEntity> getChronologicalReviewsByUser(String username);
    Map<String, Integer> calculateReviewsByMonth(String username);
    Map<String, Double> calculateRatingsByMonth(String username);
    Map<String, Double> calculateRatingsByCategory(String username);

    default ReviewEntity dtoToEntity(ReviewDTO reviewDTO, RecipeEntity recipe) {
        ReviewEntity review = new ReviewEntity();
        review.setBuyer(reviewDTO.getBuyer());
        review.setViewer(reviewDTO.getViewer());
        review.setTitle(reviewDTO.getTitle());
        review.setContent(reviewDTO.getContent());
        review.setRating(reviewDTO.getRating());
        review.setImagePath(reviewDTO.getImagePath());
        review.setId(reviewDTO.getId());
        review.setRecipe(recipe);
        review.setLikesCount(reviewDTO.getLikesCount());
        return review;
    }
    default ReviewDTO entityToDto(ReviewEntity reviewEntity) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setBuyer(reviewEntity.getBuyer());
        reviewDTO.setViewer(reviewEntity.getViewer());
        reviewDTO.setTitle(reviewEntity.getTitle());
        reviewDTO.setContent(reviewEntity.getContent());
        reviewDTO.setRating(reviewEntity.getRating());
        reviewDTO.setImagePath(reviewEntity.getImagePath());
        reviewDTO.setId(reviewEntity.getId());
        reviewDTO.setRecipeId(reviewEntity.getRecipe().getId());
        reviewDTO.setReply(reviewEntity.getReply());
        reviewDTO.setReplyDate(reviewEntity.getReplyDate());
        reviewDTO.setLikesCount(reviewEntity.getLikesCount());
        return reviewDTO;
    }
}

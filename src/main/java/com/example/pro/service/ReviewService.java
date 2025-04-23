package com.example.pro.service;

import com.example.pro.dto.ReviewDTO;
import com.example.pro.entity.ReviewEntity;

public interface ReviewService {
    void registerReview(ReviewDTO reviewDTO);
    ReviewEntity readReview(Long id);
    void updateReview(Long id,ReviewDTO reviewDTO);
    void deleteReview(Long id);

    default ReviewEntity dtoToEntity(ReviewDTO reviewDTO) {
        ReviewEntity review = new ReviewEntity();
        review.setBuyer(reviewDTO.getBuyer());
        review.setViewer(reviewDTO.getViewer());
        review.setContent(reviewDTO.getContent());
        review.setRating(reviewDTO.getRating());
        review.setImagePath(reviewDTO.getImagePath());
        review.setId(reviewDTO.getId());
//        review.setRecipe(reviewDTO.getRecipe());
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
//        reviewDTO.setRecipe(reviewEntity.getRecipe());
        return reviewDTO;
    }
}

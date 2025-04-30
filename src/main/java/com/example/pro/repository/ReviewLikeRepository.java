package com.example.pro.repository;

import com.example.pro.entity.ReviewLikeEntity;
import com.example.pro.entity.ReviewEntity;
import com.example.pro.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLikeEntity, Long> {
    // Check if a user has already liked a review
    boolean existsByReviewAndUser(ReviewEntity review, UserEntity user);
    
    // Count the number of likes for a review
    int countByReview(ReviewEntity review);
    
    // Delete a like
    void deleteByReviewAndUser(ReviewEntity review, UserEntity user);
}
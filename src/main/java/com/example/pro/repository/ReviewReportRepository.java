package com.example.pro.repository;

import com.example.pro.entity.ReviewEntity;
import com.example.pro.entity.ReviewReportEntity;
import com.example.pro.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing ReviewReportEntity objects.
 */
@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReportEntity, Long> {
    
    // Find all reports for a specific review
    List<ReviewReportEntity> findByReview(ReviewEntity review);
    
    // Find all reports submitted by a specific user
    List<ReviewReportEntity> findByReporter(UserEntity reporter);
    
    // Find all reports handled by a specific moderator
    List<ReviewReportEntity> findByModerator(UserEntity moderator);
    
    // Find all reports with a specific status
    List<ReviewReportEntity> findByStatus(String status);
    
    // Count reports for a specific review with a specific status
    int countByReviewAndStatus(ReviewEntity review, String status);
    
    // Count reports for reviews by a specific user with a specific status
    int countByReview_BuyerAndStatus(UserEntity user, String status);
    
    // Find all pending reports (for admin dashboard)
    List<ReviewReportEntity> findByStatusOrderByRegDateDesc(String status);
    
    // Check if a user has already reported a review
    boolean existsByReviewAndReporter(ReviewEntity review, UserEntity reporter);
}
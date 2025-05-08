package com.example.pro.service;

import com.example.pro.entity.ReviewEntity;
import com.example.pro.entity.ReviewReportEntity;
import com.example.pro.entity.UserEntity;

import java.util.List;

/**
 * Service interface for managing review reports.
 */
public interface ReviewReportService {
    
    /**
     * Report a review as inappropriate.
     * 
     * @param reviewId The ID of the review to report
     * @param reason The reason for reporting
     * @param reporterUsername The username of the user reporting the review
     * @return The created report entity
     * @throws IllegalArgumentException if the review doesn't exist or the user has already reported it
     */
    ReviewReportEntity reportReview(Long reviewId, String reason, String reporterUsername);
    
    /**
     * Get all reports for a specific review.
     * 
     * @param reviewId The ID of the review
     * @return List of report entities
     */
    List<ReviewReportEntity> getReportsByReview(Long reviewId);
    
    /**
     * Get all reports submitted by a specific user.
     * 
     * @param username The username of the reporter
     * @return List of report entities
     */
    List<ReviewReportEntity> getReportsByReporter(String username);
    
    /**
     * Get all reports with a specific status.
     * 
     * @param status The status to filter by (e.g., "PENDING", "APPROVED", "REJECTED")
     * @return List of report entities
     */
    List<ReviewReportEntity> getReportsByStatus(String status);
    
    /**
     * Get all pending reports for admin review.
     * 
     * @return List of pending report entities
     */
    List<ReviewReportEntity> getPendingReports();
    
    /**
     * Moderate a reported review.
     * 
     * @param reportId The ID of the report
     * @param action The action to take ("APPROVED", "REJECTED", "EDIT_REQUESTED")
     * @param moderatorUsername The username of the moderator
     * @param comment Optional comment from the moderator
     * @return The updated report entity
     */
    ReviewReportEntity moderateReport(Long reportId, String action, String moderatorUsername, String comment);
    
    /**
     * Check if a user has already reported a review.
     * 
     * @param reviewId The ID of the review
     * @param username The username of the user
     * @return true if the user has already reported the review, false otherwise
     */
    boolean hasUserReportedReview(Long reviewId, String username);
    
    /**
     * Count the number of reports for a review with a specific status.
     * 
     * @param reviewId The ID of the review
     * @param status The status to count
     * @return The count of reports
     */
    int countReportsByReviewAndStatus(Long reviewId, String status);
}
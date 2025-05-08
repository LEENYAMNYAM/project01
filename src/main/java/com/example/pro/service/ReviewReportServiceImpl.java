package com.example.pro.service;

import com.example.pro.entity.ReviewEntity;
import com.example.pro.entity.ReviewReportEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.ReviewReportRepository;
import com.example.pro.repository.ReviewRepository;
import com.example.pro.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the ReviewReportService interface.
 */
@Service
@Log4j2
public class ReviewReportServiceImpl implements ReviewReportService {

    @Autowired
    private ReviewReportRepository reviewReportRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public ReviewReportEntity reportReview(Long reviewId, String reason, String reporterUsername) {
        // Find the review
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));
        
        // Find the reporter
        UserEntity reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + reporterUsername));
        
        // Check if the user has already reported this review
        if (reviewReportRepository.existsByReviewAndReporter(review, reporter)) {
            throw new IllegalArgumentException("You have already reported this review");
        }
        
        // Create and save the report
        ReviewReportEntity report = new ReviewReportEntity();
        report.setReview(review);
        report.setReporter(reporter);
        report.setReason(reason);
        report.setStatus("PENDING");
        
        ReviewReportEntity savedReport = reviewReportRepository.save(report);
        log.info("Review report created: {}", savedReport.getId());
        
        // Check if the review has reached the threshold for automatic hiding
        int reportCount = reviewReportRepository.countByReviewAndStatus(review, "PENDING");
        if (reportCount >= 5) {
            // You might want to add a field to ReviewEntity to mark it as hidden
            // review.setHidden(true);
            // reviewRepository.save(review);
            log.info("Review {} has been automatically hidden due to multiple reports", reviewId);
        }
        
        return savedReport;
    }
    
    @Override
    public List<ReviewReportEntity> getReportsByReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));
        
        return reviewReportRepository.findByReview(review);
    }
    
    @Override
    public List<ReviewReportEntity> getReportsByReporter(String username) {
        UserEntity reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        
        return reviewReportRepository.findByReporter(reporter);
    }
    
    @Override
    public List<ReviewReportEntity> getReportsByStatus(String status) {
        return reviewReportRepository.findByStatus(status);
    }
    
    @Override
    public List<ReviewReportEntity> getPendingReports() {
        return reviewReportRepository.findByStatusOrderByRegDateDesc("PENDING");
    }
    
    @Override
    @Transactional
    public ReviewReportEntity moderateReport(Long reportId, String action, String moderatorUsername, String comment) {
        // Find the report
        ReviewReportEntity report = reviewReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + reportId));
        
        // Find the moderator
        UserEntity moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + moderatorUsername));
        
        // Update the report
        report.setStatus(action); // "APPROVED", "REJECTED", "EDIT_REQUESTED"
        report.setModerator(moderator);
        report.setModeratedDate(LocalDateTime.now());
        report.setModeratorComment(comment);
        
        ReviewReportEntity updatedReport = reviewReportRepository.save(report);
        log.info("Review report {} moderated by {}: {}", reportId, moderatorUsername, action);
        
        // If the report is approved, take action on the review
        if ("APPROVED".equals(action)) {
            ReviewEntity review = report.getReview();
            // You might want to add a field to ReviewEntity to mark it as hidden or deleted
            // review.setHidden(true);
            // reviewRepository.save(review);
            log.info("Review {} has been hidden due to approved report", review.getId());
        }
        
        return updatedReport;
    }
    
    @Override
    public boolean hasUserReportedReview(Long reviewId, String username) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));
        
        UserEntity reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        
        return reviewReportRepository.existsByReviewAndReporter(review, reporter);
    }
    
    @Override
    public int countReportsByReviewAndStatus(Long reviewId, String status) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));
        
        return reviewReportRepository.countByReviewAndStatus(review, status);
    }
}
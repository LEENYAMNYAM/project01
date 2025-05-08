package com.example.pro.service;

import com.example.pro.dto.ReviewDTO;
import com.example.pro.entity.UserEntity;

/**
 * Service interface for filtering review content.
 * Provides methods for detecting inappropriate language, spam, and duplicate reviews.
 */
public interface ContentFilterService {
    
    /**
     * Checks if the review content contains inappropriate language.
     * 
     * @param content The review content to check
     * @return true if inappropriate language is detected, false otherwise
     */
    boolean containsInappropriateLanguage(String content);
    
    /**
     * Checks if the review appears to be spam.
     * 
     * @param reviewDTO The review to check
     * @return true if the review is likely spam, false otherwise
     */
    boolean isSpam(ReviewDTO reviewDTO);
    
    /**
     * Checks if the user has already submitted a review for the same recipe.
     * 
     * @param user The user submitting the review
     * @param recipeId The ID of the recipe being reviewed
     * @return true if a duplicate review is detected, false otherwise
     */
    boolean isDuplicateReview(UserEntity user, Long recipeId);
    
    /**
     * Validates a review against all content filters.
     * 
     * @param reviewDTO The review to validate
     * @param user The user submitting the review
     * @return A validation result containing success status and error message if any
     */
    ContentFilterResult validateReview(ReviewDTO reviewDTO, UserEntity user);
    
    /**
     * Result class for content filter validation.
     */
    class ContentFilterResult {
        private boolean valid;
        private String errorMessage;
        
        public ContentFilterResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public static ContentFilterResult valid() {
            return new ContentFilterResult(true, null);
        }
        
        public static ContentFilterResult invalid(String errorMessage) {
            return new ContentFilterResult(false, errorMessage);
        }
    }
}
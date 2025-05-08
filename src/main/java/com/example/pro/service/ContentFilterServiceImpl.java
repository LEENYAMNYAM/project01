package com.example.pro.service;

import com.example.pro.dto.ReviewDTO;
import com.example.pro.entity.ReviewEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.ReviewRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of the ContentFilterService interface.
 * Provides methods for filtering inappropriate content, detecting spam, and preventing duplicate reviews.
 */
@Service
@Log4j2
public class ContentFilterServiceImpl implements ContentFilterService {

    @Autowired
    private ReviewRepository reviewRepository;

    // List of inappropriate words to filter (can be expanded)
    private static final List<String> INAPPROPRIATE_WORDS = Arrays.asList(
            "욕설", "바보", "멍청이", "개새끼", "병신", "씨발", "지랄", "좆", "새끼",
            "fuck", "shit", "bitch", "asshole", "bastard", "damn"
    );

    // Pattern for detecting excessive use of special characters (potential spam)
    private static final Pattern SPAM_PATTERN = Pattern.compile("(\\W{5,})");

    // Pattern for detecting excessive repetition (potential spam)
    private static final Pattern REPETITION_PATTERN = Pattern.compile("(.)\\1{4,}");

    // Pattern for detecting URLs (potential spam)
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://|www\\.)\\S+");

    @Override
    public boolean containsInappropriateLanguage(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        String lowerContent = content.toLowerCase();

        // Check for inappropriate words
        for (String word : INAPPROPRIATE_WORDS) {
            if (lowerContent.contains(word.toLowerCase())) {
                log.warn("Inappropriate language detected: " + word);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSpam(ReviewDTO reviewDTO) {
        if (reviewDTO == null || reviewDTO.getContent() == null) {
            return false;
        }

        String content = reviewDTO.getContent();
        String title = reviewDTO.getTitle();

        // Check for excessive special characters
        if (SPAM_PATTERN.matcher(content).find() || (title != null && SPAM_PATTERN.matcher(title).find())) {
            log.warn("Spam detected: excessive special characters");
            return true;
        }

        // Check for excessive repetition
        if (REPETITION_PATTERN.matcher(content).find() || (title != null && REPETITION_PATTERN.matcher(title).find())) {
            log.warn("Spam detected: excessive character repetition");
            return true;
        }

        // Check for URLs (often used in spam)
        if (URL_PATTERN.matcher(content).find() || (title != null && URL_PATTERN.matcher(title).find())) {
            log.warn("Spam detected: URL found in review");
            return true;
        }

        return false;
    }

    @Override
    public boolean isDuplicateReview(UserEntity user, Long recipeId) {
        if (user == null || recipeId == null) {
            return false;
        }

        // Check if the user has already reviewed this recipe
        List<ReviewEntity> userReviews = reviewRepository.findByBuyerUsername(user.getUsername());

        for (ReviewEntity review : userReviews) {
            if (review.getRecipe() != null && review.getRecipe().getId().equals(recipeId)) {
                log.warn("Duplicate review detected: User " + user.getUsername() + " already reviewed recipe " + recipeId);
                return true;
            }
        }

        return false;
    }

    @Override
    public ContentFilterResult validateReview(ReviewDTO reviewDTO, UserEntity user) {
        // Check for inappropriate language in title
        if (reviewDTO.getTitle() != null && containsInappropriateLanguage(reviewDTO.getTitle())) {
            return ContentFilterResult.invalid("리뷰 제목에 부적절한 언어가 포함되어 있습니다.");
        }

        // Check for inappropriate language in content
        if (reviewDTO.getContent() != null && containsInappropriateLanguage(reviewDTO.getContent())) {
            return ContentFilterResult.invalid("리뷰 내용에 부적절한 언어가 포함되어 있습니다.");
        }

        // Check for spam
        if (isSpam(reviewDTO)) {
            return ContentFilterResult.invalid("스팸으로 의심되는 내용이 포함되어 있습니다.");
        }

        // Check for duplicate review (only for new reviews, not updates)
        if (reviewDTO.getId() == null && isDuplicateReview(user, reviewDTO.getRecipeId())) {
            return ContentFilterResult.invalid("이미 이 레시피에 대한 리뷰를 작성하셨습니다.");
        }

        return ContentFilterResult.valid();
    }
}

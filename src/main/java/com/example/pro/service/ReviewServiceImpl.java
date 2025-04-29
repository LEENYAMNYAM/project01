package com.example.pro.service;

import com.example.pro.dto.ReviewDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.entity.ReviewEntity;
import com.example.pro.repository.RecipeRepository;
import com.example.pro.repository.ReviewRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    public void registerReview(ReviewDTO reviewDTO) {
        RecipeEntity recipe = getRecipeEntityById(reviewDTO.getRecipeId());
        ReviewEntity review = dtoToEntity(reviewDTO, recipe);
        reviewRepository.save(review);
    }

    @Override
    public RecipeEntity getRecipeEntityById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));
    }

    @Override
    public ReviewEntity readReview(Long id) {
        ReviewEntity foundReview = reviewRepository.findById(id).orElse(null);
        return foundReview;
    }

    @Override
    public void updateReview(Long id, ReviewDTO reviewDTO) {
        ReviewEntity reviewEntity = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));

        // Update the recipe if it has changed
        if (reviewDTO.getRecipeId() != null &&
            !reviewDTO.getRecipeId().equals(reviewEntity.getRecipe().getId())) {
            RecipeEntity recipe = getRecipeEntityById(reviewDTO.getRecipeId());
            reviewEntity.setRecipe(recipe);
        }

        reviewEntity.change1(reviewDTO.getContent(), reviewDTO.getRating());
        reviewRepository.save(reviewEntity);
    }


    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public void addReplyToReview(Long id, String reply, String username) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));

        // Check if the user is the owner of the recipe
        if (!review.getRecipe().getUser().getUsername().equals(username)) {
            throw new RuntimeException("레시피 작성자만 답변을 달 수 있습니다.");
        }

        review.addReply(reply);
        reviewRepository.save(review);
    }

    @Override
    public List<ReviewEntity> getReviewsByRecipe(Long recipeId) {
        return reviewRepository.findByRecipeId(recipeId);
    }

    @Override
    public List<ReviewEntity> getReviewsByRecipeSorted(Long recipeId, String sortBy) {
        List<ReviewEntity> reviews = getReviewsByRecipe(recipeId);

        switch (sortBy) {
            case "newest":
                reviews.sort((r1, r2) -> r2.getRegDate().compareTo(r1.getRegDate()));
                break;
            case "oldest":
                reviews.sort((r1, r2) -> r1.getRegDate().compareTo(r2.getRegDate()));
                break;
            case "highest_rating":
                reviews.sort((r1, r2) -> Integer.compare(r2.getRating(), r1.getRating()));
                break;
            case "lowest_rating":
                reviews.sort((r1, r2) -> Integer.compare(r1.getRating(), r2.getRating()));
                break;
            default:
                // Default to newest
                reviews.sort((r1, r2) -> r2.getRegDate().compareTo(r1.getRegDate()));
                break;
        }

        return reviews;
    }

    @Override
    public Page<ReviewEntity> getReviewsByRecipePaged(Long recipeId, String sortBy, Pageable pageable) {
        List<ReviewEntity> reviews = getReviewsByRecipeSorted(recipeId, sortBy);

        // Calculate start and end indices for the requested page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), reviews.size());

        // Create a sublist for the requested page
        List<ReviewEntity> pageContent = reviews.subList(start, end);

        // Create and return a Page object
        return new PageImpl<>(pageContent, pageable, reviews.size());
    }

    @Override
    public double calculateAverageRating(Long recipeId) {
        List<ReviewEntity> reviews = getReviewsByRecipe(recipeId);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double sum = reviews.stream()
                .mapToInt(ReviewEntity::getRating)
                .sum();

        return Math.round((sum / reviews.size()) * 10.0) / 10.0; // Round to 1 decimal place
    }
}

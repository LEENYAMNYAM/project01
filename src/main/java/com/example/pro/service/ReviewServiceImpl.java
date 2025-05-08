package com.example.pro.service;

import com.example.pro.dto.ReviewDTO;
import com.example.pro.entity.RecipeEntity;
import com.example.pro.entity.ReviewEntity;
import com.example.pro.entity.ReviewLikeEntity;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.RecipeRepository;
import com.example.pro.repository.ReviewLikeRepository;
import com.example.pro.repository.ReviewRepository;
import com.example.pro.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void registerReview(ReviewDTO reviewDTO) {
        RecipeEntity recipe = getRecipeEntityById(reviewDTO.getRecipeId());
        ReviewEntity review = dtoToEntity(reviewDTO, recipe);
        reviewRepository.save(review);

        ///포인트 추가
        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        userEntity.addPoints(100);
        userRepository.save(userEntity);
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

        reviewEntity.change1(reviewDTO.getTitle(), reviewDTO.getContent(), reviewDTO.getRating());
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
            case "most_likes":
                reviews.sort((r1, r2) -> Integer.compare(r2.getLikesCount(), r1.getLikesCount()));
                break;
            default:
                // Default to most likes, then newest
                reviews.sort((r1, r2) -> {
                    int likesCompare = Integer.compare(r2.getLikesCount(), r1.getLikesCount());
                    return likesCompare != 0 ? likesCompare : r2.getRegDate().compareTo(r1.getRegDate());
                });
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

    @Override
    @Transactional
    public boolean toggleReviewLike(Long reviewId, String username) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        // Check if the user has already liked this review
        boolean hasLiked = reviewLikeRepository.existsByReviewAndUser(review, user);

        if (hasLiked) {
            // User has already liked the review, so unlike it
            reviewLikeRepository.deleteByReviewAndUser(review, user);
            // Update likes count
            updateReviewLikesCount(reviewId);
            return false; // Returned false to indicate the review is now unliked
        } else {
            // User hasn't liked the review yet, so like it
            ReviewLikeEntity reviewLike = new ReviewLikeEntity();
            reviewLike.setReview(review);
            reviewLike.setUser(user);
            reviewLikeRepository.save(reviewLike);
            // Update likes count
            updateReviewLikesCount(reviewId);
            return true; // Returned true to indicate the review is now liked
        }
    }

    @Override
    public boolean hasUserLikedReview(Long reviewId, String username) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        return reviewLikeRepository.existsByReviewAndUser(review, user);
    }

    @Override
    public int getReviewLikesCount(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));

        return reviewLikeRepository.countByReview(review);
    }

    @Override
    @Transactional
    public void updateReviewLikesCount(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));

        int likesCount = reviewLikeRepository.countByReview(review);
        review.setLikesCount(likesCount);
        reviewRepository.save(review);
    }

    @Override
    public List<ReviewEntity> getReviewsByRecipeAndWriter(Long recipeId, String writer) {
        List<ReviewEntity> allReviews = reviewRepository.findByRecipeId(recipeId);

        // Filter reviews by writer username (case-insensitive)
        return allReviews.stream()
                .filter(review -> review.getBuyer() != null &&
                        review.getBuyer().getUsername().toLowerCase().contains(writer.toLowerCase()))
                .toList();
    }

    @Override
    public List<ReviewEntity> getReviewsByRecipeAndContent(Long recipeId, String content) {
        List<ReviewEntity> allReviews = reviewRepository.findByRecipeId(recipeId);

        // Filter reviews by content (case-insensitive)
        return allReviews.stream()
                .filter(review -> review.getContent() != null &&
                        review.getContent().toLowerCase().contains(content.toLowerCase()))
                .toList();
    }

    @Override
    public int[] calculateRatingDistribution(Long recipeId) {
        List<ReviewEntity> reviews = getReviewsByRecipe(recipeId);

        // Initialize array to store counts for ratings 1-5
        // Index 0 will be for rating 1, index 1 for rating 2, etc.
        int[] distribution = new int[5];

        // Count reviews for each rating
        for (ReviewEntity review : reviews) {
            int rating = review.getRating();
            if (rating >= 1 && rating <= 5) {
                distribution[rating - 1]++;
            }
        }

        return distribution;
    }

    @Override
    public Map<String, Double> calculateRatingTrend(Long recipeId) {
        List<ReviewEntity> reviews = getReviewsByRecipe(recipeId);

        if (reviews.isEmpty()) {
            return new HashMap<>();
        }

        // Group reviews by year-month and calculate average rating for each period
        Map<String, List<ReviewEntity>> reviewsByMonth = new HashMap<>();

        for (ReviewEntity review : reviews) {
            // Format: YYYY-MM
            String yearMonth = review.getRegDate().getYear() + "-" + 
                              (review.getRegDate().getMonthValue() < 10 ? "0" : "") + 
                              review.getRegDate().getMonthValue();

            if (!reviewsByMonth.containsKey(yearMonth)) {
                reviewsByMonth.put(yearMonth, new ArrayList<>());
            }

            reviewsByMonth.get(yearMonth).add(review);
        }

        // Calculate average rating for each month
        Map<String, Double> ratingTrend = new TreeMap<>(); // TreeMap to sort by key (date)

        for (Map.Entry<String, List<ReviewEntity>> entry : reviewsByMonth.entrySet()) {
            String yearMonth = entry.getKey();
            List<ReviewEntity> monthlyReviews = entry.getValue();

            double sum = monthlyReviews.stream()
                    .mapToInt(ReviewEntity::getRating)
                    .sum();

            double average = Math.round((sum / monthlyReviews.size()) * 10.0) / 10.0; // Round to 1 decimal place
            ratingTrend.put(yearMonth, average);
        }

        return ratingTrend;
    }

    @Override
    public double calculateCategoryAverageRating(String category) {
        // Get all recipes in the category
        List<RecipeEntity> recipesInCategory = recipeRepository.findByCategory(category);

        if (recipesInCategory.isEmpty()) {
            return 0.0;
        }

        // Calculate the sum of average ratings for all recipes in the category
        double sumOfAverages = 0.0;
        int recipesWithReviews = 0;

        for (RecipeEntity recipe : recipesInCategory) {
            List<ReviewEntity> reviews = reviewRepository.findByRecipeId(recipe.getId());
            if (!reviews.isEmpty()) {
                double sum = reviews.stream()
                        .mapToInt(ReviewEntity::getRating)
                        .sum();
                sumOfAverages += (sum / reviews.size());
                recipesWithReviews++;
            }
        }

        // Calculate the average of averages
        if (recipesWithReviews > 0) {
            return Math.round((sumOfAverages / recipesWithReviews) * 10.0) / 10.0; // Round to 1 decimal place
        } else {
            return 0.0;
        }
    }

    @Override
    public List<RecipeEntity> findSimilarRecipes(Long recipeId, int limit) {
        // Get the current recipe
        RecipeEntity currentRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));

        // Get recipes in the same category
        List<RecipeEntity> recipesInSameCategory = recipeRepository.findByCategory(currentRecipe.getCategory());

        // Remove the current recipe from the list
        recipesInSameCategory.removeIf(recipe -> recipe.getId().equals(recipeId));

        // Sort recipes by average rating (highest first)
        recipesInSameCategory.sort((r1, r2) -> {
            double r1Rating = calculateAverageRating(r1.getId());
            double r2Rating = calculateAverageRating(r2.getId());
            return Double.compare(r2Rating, r1Rating); // Descending order
        });

        // Return the top 'limit' recipes
        return recipesInSameCategory.stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public int calculateRecipeRank(Long recipeId) {
        // Get the current recipe
        RecipeEntity currentRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));

        // Get all recipes in the same category
        List<RecipeEntity> recipesInSameCategory = recipeRepository.findByCategory(currentRecipe.getCategory());

        // Calculate average rating for each recipe and sort by rating (highest first)
        Map<Long, Double> recipeRatings = new HashMap<>();

        for (RecipeEntity recipe : recipesInSameCategory) {
            double avgRating = calculateAverageRating(recipe.getId());
            recipeRatings.put(recipe.getId(), avgRating);
        }

        // Sort recipes by rating
        List<Map.Entry<Long, Double>> sortedRecipes = new ArrayList<>(recipeRatings.entrySet());
        sortedRecipes.sort(Map.Entry.<Long, Double>comparingByValue().reversed());

        // Find the rank of the current recipe
        for (int i = 0; i < sortedRecipes.size(); i++) {
            if (sortedRecipes.get(i).getKey().equals(recipeId)) {
                return i + 1; // Rank is 1-based
            }
        }

        return recipesInSameCategory.size(); // If not found, return the last rank
    }

    @Override
    public List<ReviewEntity> getReviewsByUsername(String username) {
        return reviewRepository.findByBuyerUsername(username);
    }

    @Override
    public double calculateUserAverageRating(String username) {
        List<ReviewEntity> userReviews = getReviewsByUsername(username);

        if (userReviews.isEmpty()) {
            return 0.0;
        }

        double sum = userReviews.stream()
                .mapToInt(ReviewEntity::getRating)
                .sum();

        return Math.round((sum / userReviews.size()) * 10.0) / 10.0; // Round to 1 decimal place
    }

    @Override
    public Map<Integer, Integer> calculateUserRatingDistribution(String username) {
        List<ReviewEntity> userReviews = getReviewsByUsername(username);

        // Initialize map with ratings 1-5
        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }

        // Count reviews for each rating
        for (ReviewEntity review : userReviews) {
            int rating = review.getRating();
            if (rating >= 1 && rating <= 5) {
                distribution.put(rating, distribution.get(rating) + 1);
            }
        }

        return distribution;
    }

    @Override
    public int countUserReviews(String username) {
        return getReviewsByUsername(username).size();
    }

    @Override
    public int countUserReviewLikes(String username) {
        List<ReviewEntity> userReviews = getReviewsByUsername(username);

        return userReviews.stream()
                .mapToInt(ReviewEntity::getLikesCount)
                .sum();
    }

    @Override
    public int countUserReviewReplies(String username) {
        List<ReviewEntity> userReviews = getReviewsByUsername(username);

        return (int) userReviews.stream()
                .filter(review -> review.getReply() != null && !review.getReply().isEmpty())
                .count();
    }

    @Override
    public List<ReviewEntity> getMostLikedReviewsByUser(String username, int limit) {
        List<ReviewEntity> userReviews = getReviewsByUsername(username);

        // Sort by likes count (descending)
        userReviews.sort((r1, r2) -> Integer.compare(r2.getLikesCount(), r1.getLikesCount()));

        // Return top 'limit' reviews
        return userReviews.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> calculateUserReviewsByCategory(String username) {
        List<ReviewEntity> userReviews = getReviewsByUsername(username);

        Map<String, Integer> reviewsByCategory = new HashMap<>();

        for (ReviewEntity review : userReviews) {
            String category = review.getRecipe().getCategory();
            reviewsByCategory.put(category, reviewsByCategory.getOrDefault(category, 0) + 1);
        }

        return reviewsByCategory;
    }

    @Override
    public List<ReviewEntity> getChronologicalReviewsByUser(String username) {
        List<ReviewEntity> userReviews = getReviewsByUsername(username);

        // Sort by creation date (oldest first)
        userReviews.sort(Comparator.comparing(ReviewEntity::getRegDate));

        return userReviews;
    }

    @Override
    public Map<String, Integer> calculateReviewsByMonth(String username) {
        List<ReviewEntity> userReviews = getReviewsByUsername(username);

        Map<String, Integer> reviewsByMonth = new TreeMap<>(); // TreeMap to sort by key (date)

        for (ReviewEntity review : userReviews) {
            // Format: YYYY-MM
            String yearMonth = review.getRegDate().getYear() + "-" + 
                              (review.getRegDate().getMonthValue() < 10 ? "0" : "") + 
                              review.getRegDate().getMonthValue();

            reviewsByMonth.put(yearMonth, reviewsByMonth.getOrDefault(yearMonth, 0) + 1);
        }

        return reviewsByMonth;
    }

    @Override
    public Map<String, Double> calculateRatingsByMonth(String username) {
        List<ReviewEntity> userReviews = getReviewsByUsername(username);

        // Group reviews by month
        Map<String, List<ReviewEntity>> reviewsByMonth = new HashMap<>();

        for (ReviewEntity review : userReviews) {
            // Format: YYYY-MM
            String yearMonth = review.getRegDate().getYear() + "-" + 
                              (review.getRegDate().getMonthValue() < 10 ? "0" : "") + 
                              review.getRegDate().getMonthValue();

            if (!reviewsByMonth.containsKey(yearMonth)) {
                reviewsByMonth.put(yearMonth, new ArrayList<>());
            }

            reviewsByMonth.get(yearMonth).add(review);
        }

        // Calculate average rating for each month
        Map<String, Double> ratingsByMonth = new TreeMap<>(); // TreeMap to sort by key (date)

        for (Map.Entry<String, List<ReviewEntity>> entry : reviewsByMonth.entrySet()) {
            String yearMonth = entry.getKey();
            List<ReviewEntity> monthlyReviews = entry.getValue();

            double sum = monthlyReviews.stream()
                    .mapToInt(ReviewEntity::getRating)
                    .sum();

            double average = Math.round((sum / monthlyReviews.size()) * 10.0) / 10.0; // Round to 1 decimal place
            ratingsByMonth.put(yearMonth, average);
        }

        return ratingsByMonth;
    }

    @Override
    public Map<String, Double> calculateRatingsByCategory(String username) {
        List<ReviewEntity> userReviews = getReviewsByUsername(username);

        // Group reviews by category
        Map<String, List<ReviewEntity>> reviewsByCategory = new HashMap<>();

        for (ReviewEntity review : userReviews) {
            String category = review.getRecipe().getCategory();

            if (!reviewsByCategory.containsKey(category)) {
                reviewsByCategory.put(category, new ArrayList<>());
            }

            reviewsByCategory.get(category).add(review);
        }

        // Calculate average rating for each category
        Map<String, Double> ratingsByCategory = new HashMap<>();

        for (Map.Entry<String, List<ReviewEntity>> entry : reviewsByCategory.entrySet()) {
            String category = entry.getKey();
            List<ReviewEntity> categoryReviews = entry.getValue();

            double sum = categoryReviews.stream()
                    .mapToInt(ReviewEntity::getRating)
                    .sum();

            double average = Math.round((sum / categoryReviews.size()) * 10.0) / 10.0; // Round to 1 decimal place
            ratingsByCategory.put(category, average);
        }

        return ratingsByCategory;
    }
}

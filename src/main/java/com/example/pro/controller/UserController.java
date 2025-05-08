package com.example.pro.controller;

import com.example.pro.config.auth.PrincipalDetail;
import com.example.pro.entity.UserEntity;
import com.example.pro.entity.ReviewEntity;

import com.example.pro.dto.UserDTO;

import com.example.pro.service.UserService;
import com.example.pro.service.ReviewService;
import com.example.pro.service.UserFollowService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
@RequestMapping("/userinfo")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserFollowService userFollowService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @GetMapping("/join")
    public void join() {

    }
    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttributes) {
        log.info("/user/register POST 요청 - 회원가입 처리: {}", userDTO.getUsername());

        try {

            userService.registerUser(userDTO);

            redirectAttributes.addFlashAttribute("message", "회원가입이 성공적으로 완료되었습니다!");
            return "redirect:/userinfo/login";

        } catch (Exception e) {

            log.error("회원가입 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "회원가입 실패: " + e.getMessage());
            return "redirect:/userinfo/join";
        }
    }
    @GetMapping("/login")
    public void login() {

    }

    @GetMapping("/userinfo")
    public String userInfo(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Logged-in username: {}", username);

        UserEntity userEntity = userService.readUser(username);
        if (userEntity == null) {
            log.error("User not found for username: {}", username);
            return "error";
        }
        log.info("User entity retrieved: {}", userEntity);


        UserDTO userDTO = userService.entityToDto(userEntity);
        log.info("User DTO created: {}", userDTO);


        model.addAttribute("user", userDTO);
        log.info("User DTO added to model");

        // Get user's reviews for profile integration
        List<ReviewEntity> userReviews = reviewService.getReviewsByUsername(username);
        model.addAttribute("userReviews", userReviews);
        log.info("User reviews added to model: {} reviews found", userReviews.size());

        return "/userinfo/userinfo";
    }
    @GetMapping("/userupdate")
    public String userUpdate(@AuthenticationPrincipal PrincipalDetail principal, Model model) {


        model.addAttribute("user", principal.getUser());
        return "userinfo/userupdate";
    }
    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") UserDTO userDTO,
                             @AuthenticationPrincipal PrincipalDetail principal) {

        String username = principal.getUsername();

        // 기존 시그니처에 맞게 호출
        userService.updateUser(username, userDTO);

        return "redirect:/userinfo/userinfo";
    }




    @GetMapping("/delete")
    public String deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        userService.deleteUser(username);

        // 로그아웃 처리할 수도 있음 (선택사항)
        SecurityContextHolder.clearContext();

        return "redirect:/logout"; // 혹은 메인 페이지로 리디렉트
    }

    /**
     * View a user's profile with followers and following information
     */
    @GetMapping("/profile/{username}")
    public String viewProfile(@PathVariable String username, Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        try {
            // Get user information
            UserEntity userEntity = userService.readUser(username);
            if (userEntity == null) {
                return "error";
            }

            UserDTO userDTO = userService.entityToDto(userEntity);
            model.addAttribute("profileUser", userDTO);

            // Get user's reviews
            List<ReviewEntity> userReviews = reviewService.getReviewsByUsername(username);
            model.addAttribute("userReviews", userReviews);

            // Get followers and following
            List<UserEntity> followers = userFollowService.getFollowers(username);
            List<UserEntity> following = userFollowService.getFollowing(username);
            model.addAttribute("followers", followers);
            model.addAttribute("following", following);
            model.addAttribute("followerCount", followers.size());
            model.addAttribute("followingCount", following.size());

            // Check if the current user is following this user
            boolean isFollowing = false;
            if (principalDetail != null) {
                String currentUsername = principalDetail.getUsername();
                isFollowing = userFollowService.isFollowing(currentUsername, username);
                model.addAttribute("isFollowing", isFollowing);
            }

            return "userinfo/profile";
        } catch (Exception e) {
            log.error("Error viewing profile: {}", e.getMessage());
            return "error";
        }
    }

    /**
     * Follow a user
     */
    @PostMapping("/follow/{username}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> followUser(@PathVariable String username, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if user is logged in
            if (principalDetail == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.ok(response);
            }

            String currentUsername = principalDetail.getUsername();

            // Follow the user
            boolean success = userFollowService.followUser(currentUsername, username);

            if (success) {
                response.put("success", true);
                response.put("message", username + "님을 팔로우했습니다.");
                response.put("followerCount", userFollowService.countFollowers(username));
            } else {
                response.put("success", false);
                response.put("message", "이미 팔로우 중이거나 팔로우할 수 없는 사용자입니다.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error following user: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Unfollow a user
     */
    @PostMapping("/unfollow/{username}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unfollowUser(@PathVariable String username, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if user is logged in
            if (principalDetail == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.ok(response);
            }

            String currentUsername = principalDetail.getUsername();

            // Unfollow the user
            boolean success = userFollowService.unfollowUser(currentUsername, username);

            if (success) {
                response.put("success", true);
                response.put("message", username + "님 팔로우를 취소했습니다.");
                response.put("followerCount", userFollowService.countFollowers(username));
            } else {
                response.put("success", false);
                response.put("message", "팔로우 중이 아니거나 팔로우 취소할 수 없는 사용자입니다.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error unfollowing user: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Get popular reviewers
     */
    @GetMapping("/popular-reviewers")
    public String getPopularReviewers(Model model) {
        try {
            List<UserEntity> popularReviewers = userFollowService.getPopularReviewers(5); // Get top 5 popular reviewers
            model.addAttribute("popularReviewers", popularReviewers);

            return "userinfo/popular-reviewers";
        } catch (Exception e) {
            log.error("Error getting popular reviewers: {}", e.getMessage());
            return "error";
        }
    }

    /**
     * User review dashboard
     */
    @GetMapping("/review-dashboard")
    public String reviewDashboard(Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        try {
            // Check if user is logged in
            if (principalDetail == null) {
                return "redirect:/login";
            }

            String username = principalDetail.getUsername();
            UserEntity userEntity = userService.readUser(username);

            if (userEntity == null) {
                log.error("User not found for username: {}", username);
                return "error";
            }

            // Get user information
            UserDTO userDTO = userService.entityToDto(userEntity);
            model.addAttribute("user", userDTO);

            // Get user's reviews
            List<ReviewEntity> userReviews = reviewService.getReviewsByUsername(username);
            model.addAttribute("userReviews", userReviews);

            // Get review statistics
            double averageRating = reviewService.calculateUserAverageRating(username);
            Map<Integer, Integer> ratingDistribution = reviewService.calculateUserRatingDistribution(username);
            int totalReviews = reviewService.countUserReviews(username);
            int totalLikes = reviewService.countUserReviewLikes(username);
            int totalReplies = reviewService.countUserReviewReplies(username);
            List<ReviewEntity> mostLikedReviews = reviewService.getMostLikedReviewsByUser(username, 3); // Top 3 most liked reviews
            Map<String, Integer> reviewsByCategory = reviewService.calculateUserReviewsByCategory(username);

            // Add statistics to model
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("ratingDistribution", ratingDistribution);
            model.addAttribute("totalReviews", totalReviews);
            model.addAttribute("totalLikes", totalLikes);
            model.addAttribute("totalReplies", totalReplies);
            model.addAttribute("mostLikedReviews", mostLikedReviews);
            model.addAttribute("reviewsByCategory", reviewsByCategory);

            return "userinfo/review-dashboard";
        } catch (Exception e) {
            log.error("Error loading review dashboard: {}", e.getMessage());
            return "error";
        }
    }

    /**
     * User review history
     */
    @GetMapping("/review-history")
    public String reviewHistory(Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        try {
            // Check if user is logged in
            if (principalDetail == null) {
                return "redirect:/login";
            }

            String username = principalDetail.getUsername();
            UserEntity userEntity = userService.readUser(username);

            if (userEntity == null) {
                log.error("User not found for username: {}", username);
                return "error";
            }

            // Get user information
            UserDTO userDTO = userService.entityToDto(userEntity);
            model.addAttribute("user", userDTO);

            // Get review statistics
            double averageRating = reviewService.calculateUserAverageRating(username);
            int totalReviews = reviewService.countUserReviews(username);
            int totalLikes = reviewService.countUserReviewLikes(username);
            int totalReplies = reviewService.countUserReviewReplies(username);

            // Get review history data
            List<ReviewEntity> chronologicalReviews = reviewService.getChronologicalReviewsByUser(username);
            Map<String, Integer> reviewsByMonth = reviewService.calculateReviewsByMonth(username);
            Map<String, Double> ratingsByMonth = reviewService.calculateRatingsByMonth(username);
            Map<String, Integer> reviewsByCategory = reviewService.calculateUserReviewsByCategory(username);
            Map<String, Double> ratingsByCategory = reviewService.calculateRatingsByCategory(username);

            // Add data to model
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("totalReviews", totalReviews);
            model.addAttribute("totalLikes", totalLikes);
            model.addAttribute("totalReplies", totalReplies);
            model.addAttribute("chronologicalReviews", chronologicalReviews);
            model.addAttribute("reviewsByMonth", reviewsByMonth);
            model.addAttribute("ratingsByMonth", ratingsByMonth);
            model.addAttribute("reviewsByCategory", reviewsByCategory);
            model.addAttribute("ratingsByCategory", ratingsByCategory);

            return "userinfo/review-history";
        } catch (Exception e) {
            log.error("Error loading review history: {}", e.getMessage());
            return "error";
        }
    }
}

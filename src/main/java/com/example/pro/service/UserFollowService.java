package com.example.pro.service;

import com.example.pro.entity.UserEntity;
import com.example.pro.entity.UserFollowEntity;

import java.util.List;

/**
 * Service interface for managing user follow relationships.
 */
public interface UserFollowService {
    
    /**
     * Follow a user.
     * 
     * @param followerUsername The username of the user who wants to follow
     * @param followingUsername The username of the user to be followed
     * @return true if the follow was successful, false if already following
     */
    boolean followUser(String followerUsername, String followingUsername);
    
    /**
     * Unfollow a user.
     * 
     * @param followerUsername The username of the user who wants to unfollow
     * @param followingUsername The username of the user to be unfollowed
     * @return true if the unfollow was successful, false if not following
     */
    boolean unfollowUser(String followerUsername, String followingUsername);
    
    /**
     * Check if a user is following another user.
     * 
     * @param followerUsername The username of the potential follower
     * @param followingUsername The username of the potential followed user
     * @return true if following, false otherwise
     */
    boolean isFollowing(String followerUsername, String followingUsername);
    
    /**
     * Get all users that a specific user is following.
     * 
     * @param username The username of the user
     * @return List of UserEntity objects representing followed users
     */
    List<UserEntity> getFollowing(String username);
    
    /**
     * Get all users who are following a specific user.
     * 
     * @param username The username of the user
     * @return List of UserEntity objects representing followers
     */
    List<UserEntity> getFollowers(String username);
    
    /**
     * Count how many users a specific user is following.
     * 
     * @param username The username of the user
     * @return The count of followed users
     */
    int countFollowing(String username);
    
    /**
     * Count how many followers a specific user has.
     * 
     * @param username The username of the user
     * @return The count of followers
     */
    int countFollowers(String username);
    
    /**
     * Get popular reviewers (users with most followers).
     * 
     * @param limit The maximum number of popular reviewers to return
     * @return List of UserEntity objects representing popular reviewers
     */
    List<UserEntity> getPopularReviewers(int limit);
}
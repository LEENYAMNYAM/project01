package com.example.pro.service;

import com.example.pro.entity.UserEntity;
import com.example.pro.entity.UserFollowEntity;
import com.example.pro.repository.UserFollowRepository;
import com.example.pro.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the UserFollowService interface.
 */
@Service
@Log4j2
public class UserFollowServiceImpl implements UserFollowService {

    @Autowired
    private UserFollowRepository userFollowRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public boolean followUser(String followerUsername, String followingUsername) {
        // Prevent users from following themselves
        if (followerUsername.equals(followingUsername)) {
            log.warn("User attempted to follow themselves: {}", followerUsername);
            return false;
        }
        
        UserEntity follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower user not found: " + followerUsername));
        
        UserEntity following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("Following user not found: " + followingUsername));
        
        // Check if already following
        if (userFollowRepository.existsByFollowerAndFollowing(follower, following)) {
            log.info("User {} is already following {}", followerUsername, followingUsername);
            return false;
        }
        
        // Create new follow relationship
        UserFollowEntity userFollow = new UserFollowEntity();
        userFollow.setFollower(follower);
        userFollow.setFollowing(following);
        
        userFollowRepository.save(userFollow);
        log.info("User {} is now following {}", followerUsername, followingUsername);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean unfollowUser(String followerUsername, String followingUsername) {
        UserEntity follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower user not found: " + followerUsername));
        
        UserEntity following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("Following user not found: " + followingUsername));
        
        // Check if following
        if (!userFollowRepository.existsByFollowerAndFollowing(follower, following)) {
            log.info("User {} is not following {}", followerUsername, followingUsername);
            return false;
        }
        
        // Delete follow relationship
        userFollowRepository.deleteByFollowerAndFollowing(follower, following);
        log.info("User {} has unfollowed {}", followerUsername, followingUsername);
        
        return true;
    }
    
    @Override
    public boolean isFollowing(String followerUsername, String followingUsername) {
        UserEntity follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("Follower user not found: " + followerUsername));
        
        UserEntity following = userRepository.findByUsername(followingUsername)
                .orElseThrow(() -> new RuntimeException("Following user not found: " + followingUsername));
        
        return userFollowRepository.existsByFollowerAndFollowing(follower, following);
    }
    
    @Override
    public List<UserEntity> getFollowing(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        return userFollowRepository.findByFollower(user).stream()
                .map(UserFollowEntity::getFollowing)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserEntity> getFollowers(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        return userFollowRepository.findByFollowing(user).stream()
                .map(UserFollowEntity::getFollower)
                .collect(Collectors.toList());
    }
    
    @Override
    public int countFollowing(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        return userFollowRepository.countByFollower(user);
    }
    
    @Override
    public int countFollowers(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        return userFollowRepository.countByFollowing(user);
    }
    
    @Override
    public List<UserEntity> getPopularReviewers(int limit) {
        // Get users with the most followers
        // This is a simplified implementation - in a real system, you might want to
        // use a more sophisticated query or caching mechanism
        
        // Get all users
        List<UserEntity> allUsers = userRepository.findAll();
        
        // Sort users by follower count (descending)
        return allUsers.stream()
                .sorted((u1, u2) -> {
                    int u1Followers = userFollowRepository.countByFollowing(u1);
                    int u2Followers = userFollowRepository.countByFollowing(u2);
                    return Integer.compare(u2Followers, u1Followers); // Descending order
                })
                .limit(limit)
                .collect(Collectors.toList());
    }
}
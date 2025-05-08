package com.example.pro.repository;

import com.example.pro.entity.UserFollowEntity;
import com.example.pro.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing UserFollowEntity objects.
 */
@Repository
public interface UserFollowRepository extends JpaRepository<UserFollowEntity, Long> {
    
    // Find all users that a specific user is following
    List<UserFollowEntity> findByFollower(UserEntity follower);
    
    // Find all users who are following a specific user
    List<UserFollowEntity> findByFollowing(UserEntity following);
    
    // Count how many users a specific user is following
    int countByFollower(UserEntity follower);
    
    // Count how many followers a specific user has
    int countByFollowing(UserEntity following);
    
    // Check if a follow relationship exists
    boolean existsByFollowerAndFollowing(UserEntity follower, UserEntity following);
    
    // Delete a follow relationship
    void deleteByFollowerAndFollowing(UserEntity follower, UserEntity following);
    
    // Find popular users (users with most followers)
    List<UserFollowEntity> findByFollowingOrderByRegDateDesc(UserEntity following);
}
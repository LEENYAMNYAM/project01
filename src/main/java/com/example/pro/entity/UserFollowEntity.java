package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a following relationship between users.
 * A user (follower) can follow another user (following).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user_follow")
public class UserFollowEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private UserEntity follower; // The user who is following
    
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private UserEntity following; // The user being followed
    
    // Composite unique constraint to prevent duplicate follows
    @Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "following_id"})
    })
    public static class UniqueUserFollow {}
}
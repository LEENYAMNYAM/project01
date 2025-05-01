package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="review_like")
public class ReviewLikeEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    // Composite unique constraint to prevent duplicate likes
    @Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"review_id", "user_id"})
    })
    public static class UniqueReviewLike {}
}
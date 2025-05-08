package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a report submitted by a user for an inappropriate review.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="review_report")
public class ReviewReportEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review;
    
    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserEntity reporter;
    
    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private UserEntity moderator;
    
    @Column(nullable = false)
    private String reason;
    
    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, EDIT_REQUESTED
    
    private LocalDateTime moderatedDate;
    
    @Column(length = 1000)
    private String moderatorComment;
}
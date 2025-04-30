package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="recipe")
@Getter
@Setter
@Builder
@ToString(exclude = {"steps", "reviews"})
@NoArgsConstructor
@AllArgsConstructor
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 레시피 제목 */
    @Column(unique = true, nullable = false)
    private String title;

    /* 카테고리 */
    @Column(nullable = false)
    private String category;

    /* 이미지 url */
    private String mainImage;

    /* youtubeLink */
    private String youtubeLink;

    /* 작성자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private UserEntity user;

    /* 작성시간 */
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    /* 좋아요갯수 */
    private Long likeCount;

    @PrePersist
    public void prePersist() {
        this.likeCount = this.likeCount == null ? 0 : this.likeCount;
    }

    @OneToMany(mappedBy = "recipeEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStepEntity> steps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews = new ArrayList<>();



}

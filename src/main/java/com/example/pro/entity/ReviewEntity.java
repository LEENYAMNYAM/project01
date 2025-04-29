package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="review")
public class ReviewEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private RecipeEntity recipe;
    // 구매자
    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private UserEntity buyer;

    // 접속자 (리뷰 보는 사람 or 작성자 본인 제외 다른 사용자 정보가 필요한 경우)
    @ManyToOne
    @JoinColumn(name = "viewer_id")
    private UserEntity viewer;

    // 리뷰 내용
    @Column(nullable = false, length = 1000)
    private String content;

    // 별점 (1~5)
    @Column(nullable = false)
    private int rating;

    // 이미지 경로
    private String imagePath;

    // 레시피 작성자의 답변
    @Column(length = 1000)
    private String reply;

    // 답변 작성 시간
    private LocalDateTime replyDate;

    public void change1(String content, int rating) {
        this.content = content;
        this.rating = rating;
    }

    public void addReply(String reply) {
        this.reply = reply;
        this.replyDate = LocalDateTime.now();
    }


}

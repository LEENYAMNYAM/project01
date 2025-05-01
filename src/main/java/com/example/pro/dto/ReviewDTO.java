package com.example.pro.dto;

import com.example.pro.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long id;

    private Long recipeId;

    private UserEntity buyer;

    private UserEntity viewer;

    private String content;

    private int rating;

    private String imagePath;

    private String reply;

    private LocalDateTime replyDate;
}

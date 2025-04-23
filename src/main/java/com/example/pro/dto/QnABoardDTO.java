package com.example.pro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnABoardDTO {

    private Long qno;
    private String title;
    private String writer;
    private String content;
    private LocalDateTime createdAt;
    private Long hitcount;
}

package com.example.pro.dto;

import com.example.pro.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CSBoardReplyDTO {
    private Long id;
    private Long csBoardId;
    private String content;
    private String writer;
    private UserEntity user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
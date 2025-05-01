package com.example.pro.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CSBoardDTO {

    private Long id;
    private String title;
    private String writer;
    private String content;
    private LocalDateTime createdAt;
    private Long hitcount;
}

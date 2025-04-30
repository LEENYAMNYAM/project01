package com.example.pro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private Long id;

    private String username;

    private Long totalPrice;
    111
    private LocalDateTime createdAt;
}

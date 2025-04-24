package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "recipe_step")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStepEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id") // 외래키 컬럼명 (DB 테이블에 실제로 들어갈 이름)
    private RecipeEntity recipe;

    private int stepNumber;

    private String content;

    private String imageName;
}

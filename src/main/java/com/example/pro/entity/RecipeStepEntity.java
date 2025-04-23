package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "recipe_step")
@Getter @Setter
public class RecipeStepEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id") // 외래키 컬럼명 (DB 테이블에 실제로 들어갈 이름)
    private RecipeEntity recipe;

    @Column(nullable = false)
    private int stepNumber;

    @Column(nullable = false)
    private String content;

    private String imagename;
}

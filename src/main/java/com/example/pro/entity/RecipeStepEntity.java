package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "recipe_step")
@Getter @Setter
public class RecipeStepEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    private RecipeEntity recipeEntity;

    @Column(nullable = false)
    private int stepNumber;

    @Column(nullable = false)
    private String content;

    private String imagename;

}

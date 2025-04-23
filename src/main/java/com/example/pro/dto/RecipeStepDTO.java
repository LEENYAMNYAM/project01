package com.example.pro.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeStepDTO {

    private Long id;

    @NotEmpty
    private Long recipeId;

    private int stepNumber;

    private String content;

    private String imageName;

}

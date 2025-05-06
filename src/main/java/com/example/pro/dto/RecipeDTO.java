package com.example.pro.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {

    private Long id;
    /* 레시피 제목 */
    @NotEmpty
    private String title;
    /* 카테고리 */
    @NotEmpty
    private String category;
    /* youtubeLink */
    private String youtubeLink;
    /* 메인이미지주소 */
    private String mainImagePath;
    /* 작성자 */
    private String username;
    /* 재료 목록 */
    private List<RecipeIngredientsDTO> recipeIngredients;
    /* 요리 순서*/
    private List<RecipeStepDTO> steps;
    /* 작성시간 */
    private LocalDateTime createdAt;
    /* 좋아요갯수 */
    private Long likeCount;
    /* 평균 별점 */
    private Double averageRating;
    private String description;

}

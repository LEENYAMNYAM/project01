package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="ingredient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String ingredientName;
    @Column(nullable = false)
    private Long price;
    private String detail;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @PrePersist
    public void prePersist() {
        this.price = this.price == null? 0 : this.price;
    }

}

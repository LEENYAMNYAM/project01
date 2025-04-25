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
    @Column(unique = true, nullable = false)
    private String productId;
    private Long price;
    private String mallLink;
    private String mallName;
    private String imageUrl;

    @PrePersist
    public void prePersist() {
        this.price = this.price == null? 0 : this.price;
    }

}

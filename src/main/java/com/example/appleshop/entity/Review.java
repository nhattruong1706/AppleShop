package com.example.appleshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "product_reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="variant_id", nullable = false)
    private Long variantId;

    @Column
    private Integer rating;

    @Column(name="comment", columnDefinition="nvarchar(500)")
    private String comment;

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="img", length=255)
    private String img;
}

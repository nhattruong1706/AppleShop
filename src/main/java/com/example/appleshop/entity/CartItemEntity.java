package com.example.appleshop.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "cart_items")
@Data
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant; // ✅ Đổi lại đúng tên class

    @Column(nullable = false)
    private int qty;
}

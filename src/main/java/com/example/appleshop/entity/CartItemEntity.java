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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "product_id", nullable = false)
    private Long productId; // nếu có ProductEntity thì @ManyToOne

    @Column(name = "qty", nullable = false)
    private int qty;
}

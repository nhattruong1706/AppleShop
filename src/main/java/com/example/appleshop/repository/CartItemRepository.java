package com.example.appleshop.repository;
import com.example.appleshop.entity.CartItemEntity;
import com.example.appleshop.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByUser(UserEntity user);
    Optional<CartItemEntity> findByUserAndProductId(UserEntity user, Long productId);
}

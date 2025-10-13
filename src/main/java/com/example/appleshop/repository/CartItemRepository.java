package com.example.appleshop.repository;

import com.example.appleshop.entity.CartItemEntity;
import com.example.appleshop.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByUser(UserEntity user);
}


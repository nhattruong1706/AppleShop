package com.example.appleshop.repository;

import com.example.appleshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Thêm query method nếu muốn, ví dụ tìm theo category
}

package com.example.appleshop.repository;

import com.example.appleshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Có thể thêm query method nếu muốn, ví dụ findByName
}

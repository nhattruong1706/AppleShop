package com.example.appleshop.repository;

import com.example.appleshop.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // Lấy danh sách biến thể theo product_id
    List<ProductVariant> findByProductId(Long productId);

    // 🔍 Tìm 1 variant dựa vào productId + color + storage
    Optional<ProductVariant> findByProductIdAndColorAndStorage(Long productId, String color, String storage);
}

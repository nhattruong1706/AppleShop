package com.example.appleshop.repository;

import com.example.appleshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByVariantIdOrderByCreatedAtDesc(Long variantId);
    long countByVariantIdAndRating(Long variantId, int rating);
    long countByVariantId(Long variantId);
    // ReviewRepository.java

    List<Review> findByVariantIdInOrderByCreatedAtDesc(List<Long> variantIds);
    long countByVariantIdInAndRating(List<Long> variantIds, int rating);
    long countByVariantIdIn(List<Long> variantIds);

}

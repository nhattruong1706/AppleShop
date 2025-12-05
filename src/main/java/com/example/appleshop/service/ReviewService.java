package com.example.appleshop.service;

import com.example.appleshop.entity.Review;
import com.example.appleshop.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ReviewService {

    private final ReviewRepository repo;

    public ReviewService(ReviewRepository repo) {
        this.repo = repo;
    }

    public Review save(Review r) {
        return repo.save(r);
    }

    public List<Review> findByVariant(Long variantId) {
        return repo.findByVariantIdOrderByCreatedAtDesc(variantId);
    }

    public Map<Integer, Long> countByStars(Long variantId) {
        return IntStream.rangeClosed(1, 5).boxed()
                .collect(Collectors.toMap(i -> i, i -> repo.countByVariantIdAndRating(variantId, i)));
    }

    public double averageStars(Long variantId) {
        long total = repo.countByVariantId(variantId);
        if (total == 0) return 0.0;
        long sum = 0;
        for (int s = 1; s <= 5; s++) sum += s * repo.countByVariantIdAndRating(variantId, s);
        return (double) sum / total;
    }

    public long totalReviews(Long variantId) {
        return repo.countByVariantId(variantId);
    }
    // ReviewService.java

    public List<Review> findByVariants(List<Long> variantIds) {
        return repo.findByVariantIdInOrderByCreatedAtDesc(variantIds);
    }

    public Map<Integer, Long> countByStars(List<Long> variantIds) {
        return IntStream.rangeClosed(1,5).boxed()
                .collect(Collectors.toMap(i -> i,
                        i -> repo.countByVariantIdInAndRating(variantIds, i)));
    }

    public double averageStars(List<Long> variantIds) {
        long total = totalReviews(variantIds);
        if (total == 0) return 0.0;
        long sum = 0;
        for (int s = 1; s <= 5; s++) sum += s * repo.countByVariantIdInAndRating(variantIds, s);
        return (double) sum / total;
    }

    public long totalReviews(List<Long> variantIds) {
        return repo.countByVariantIdIn(variantIds);
    }

}

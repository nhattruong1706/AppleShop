package com.example.appleshop.controller;

import com.example.appleshop.entity.Review;
import com.example.appleshop.entity.UserEntity;
import com.example.appleshop.service.OrderService;
import com.example.appleshop.service.ProductService;
import com.example.appleshop.service.ReviewService;
import com.example.appleshop.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final FileUploadController fileUploadController;
    private final ProductService productService; // ✅ thêm productService

    // Sửa constructor để inject productService mà vẫn giữ code cũ
    public ReviewController(ReviewService reviewService, OrderService orderService,
                            UserRepository userRepository, FileUploadController fileUploadController,
                            ProductService productService) {
        this.reviewService = reviewService;
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.fileUploadController = fileUploadController;
        this.productService = productService;
    }

    // =======================
    // GET tất cả review theo productId
    // =======================
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getReviewsByProduct(@PathVariable Long productId) {
        if (productId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cần cung cấp productId"));
        }

        // Lấy danh sách variantId của sản phẩm
        List<Long> variantIds = productService.getVariantsByProduct(productId)
                .stream()
                .map(v -> v.getId())
                .toList();

        if (variantIds.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "meta", Map.of(
                            "average", 0.0,
                            "total", 0,
                            "distribution", Map.of(1,0,2,0,3,0,4,0,5,0)
                    ),
                    "reviews", List.of()
            ));
        }

        // Lấy review từ tất cả variant
        List<Review> reviews = reviewService.findByVariants(variantIds);

        // Tính meta
        double average = reviewService.averageStars(variantIds);
        long total = reviewService.totalReviews(variantIds);
        Map<Integer, Long> distribution = reviewService.countByStars(variantIds);

        Map<String, Object> meta = Map.of(
                "average", average,
                "total", total,
                "distribution", distribution
        );

        return ResponseEntity.ok(Map.of(
                "meta", meta,
                "reviews", reviews
        ));
    }

    // =======================
    // GET danh sách review + meta
    // =======================
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(name="variantId", required = false) Long variantId,
                                  @RequestParam(name="productId", required = false) Long productId) {

        if (variantId == null) variantId = productId != null ? productId : 1L;

        List<Review> reviews = reviewService.findByVariant(variantId);

        Map<String, Object> meta = new HashMap<>();
        meta.put("average", reviewService.averageStars(variantId));
        meta.put("total", reviewService.totalReviews(variantId));
        meta.put("distribution", reviewService.countByStars(variantId));

        return ResponseEntity.ok(Map.of(
                "meta", meta,
                "reviews", reviews
        ));
    }

    // =======================
    // POST thêm review mới
    // =======================
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> add(
            @RequestParam("variantId") Long variantId,
            @RequestParam("rating") int rating,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error","Vui lòng đăng nhập"));
        }

        UserEntity user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (!orderService.hasPurchased(user, variantId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error","Bạn chưa mua sản phẩm này nên không thể đánh giá"));
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = fileUploadController.uploadImage(image).getBody().get("url");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Upload ảnh thất bại: " + e.getMessage()));
            }
        }

        Review review = new Review();
        review.setVariantId(variantId);
        review.setUserId(user.getId());
        review.setRating(rating);
        review.setComment(comment);
        review.setImg(imageUrl);
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewService.save(review);
        return ResponseEntity.ok(saved);
    }
}

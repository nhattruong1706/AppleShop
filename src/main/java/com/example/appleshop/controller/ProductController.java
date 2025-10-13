package com.example.appleshop.controller;

import com.example.appleshop.entity.Category;
import com.example.appleshop.entity.Product;
import com.example.appleshop.entity.ProductVariant;
import com.example.appleshop.repository.CategoryRepository;
import com.example.appleshop.service.CategoryService;
import com.example.appleshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CategoryRepository  categoryRepository;
    // 🔹 Thêm sản phẩm (không kèm biến thể)
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> body) {
        try {
            Product product = new Product();
            product.setName((String) body.get("name"));
            product.setDescription((String) body.get("description"));
            product.setPrice(Double.valueOf(body.get("price").toString()));
            product.setStock(Integer.valueOf(body.get("stock").toString()));
            product.setImg((String) body.get("img"));

            // ✅ Lấy categoryId từ JSON (sửa kiểu Long → Integer)
            if (body.get("categoryId") != null) {
                Integer categoryId = Integer.valueOf(body.get("categoryId").toString());
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found with id = " + categoryId));
                product.setCategory(category);
            }

            Product saved = productService.createProduct(product);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
    }



    // 🔹 Lấy tất cả sản phẩm
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // 🔹 Lấy chi tiết sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔹 Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product updatedProduct) {
        try {
            Product saved = productService.updateProduct(id, updatedProduct);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }
    }

    // 🔹 Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
    }

    // =====================================================
    // 🔹 Các API xử lý biến thể (ProductVariant)
    // =====================================================

    // 🆕 Thêm biến thể cho sản phẩm theo productId
    @PostMapping("/{productId}/variants")
    public ResponseEntity<?> addVariantToProduct(
            @PathVariable Long productId,
            @RequestBody ProductVariant variantRequest) {
        try {
            ProductVariant savedVariant = productService.addVariant(productId, variantRequest);
            return ResponseEntity.ok(savedVariant);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi thêm biến thể: " + e.getMessage());
        }
    }

    // 🔹 Lấy danh sách biến thể theo sản phẩm
    @GetMapping("/{productId}/variants")
    public ResponseEntity<?> getVariantsByProduct(@PathVariable Long productId) {
        try {
            List<ProductVariant> variants = productService.getVariantsByProduct(productId);
            return ResponseEntity.ok(variants);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔹 Cập nhật biến thể
    @PutMapping("/variants/{variantId}")
    public ResponseEntity<?> updateVariant(
            @PathVariable Long variantId,
            @RequestBody ProductVariant updatedVariant) {
        try {
            ProductVariant saved = productService.updateVariant(variantId, updatedVariant);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật biến thể: " + e.getMessage());
        }
    }

    // 🔹 Xóa biến thể
    @DeleteMapping("/variants/{variantId}")
    public ResponseEntity<?> deleteVariant(@PathVariable Long variantId) {
        try {
            productService.deleteVariant(variantId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa biến thể: " + e.getMessage());
        }
    }
}

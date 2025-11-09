
package com.example.appleshop.controller;

import com.example.appleshop.entity.Product;
import com.example.appleshop.entity.ProductVariant;
import com.example.appleshop.repository.ProductRepository;
import com.example.appleshop.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variants")
@CrossOrigin(origins = "*")
public class ProductVariantController {

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ProductRepository productRepository;

    // ✅ Lấy tất cả biến thể
    @GetMapping
    public ResponseEntity<List<ProductVariant>> getAllVariants() {
        return ResponseEntity.ok(variantRepository.findAll());
    }

    // ✅ Lấy biến thể theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductVariant> getVariantById(@PathVariable Long id) {
        return variantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Lấy danh sách biến thể theo product_id
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductVariant>> getByProduct(@PathVariable Long productId) {
        List<ProductVariant> variants = variantRepository.findByProductId(productId);
        return ResponseEntity.ok(variants);
    }

    // ✅ 🔍 Tìm variant theo productId + color + storage
    @GetMapping("/find")
    public ResponseEntity<?> findVariant(
            @RequestParam Long productId,
            @RequestParam String color,
            @RequestParam String storage) {

        ProductVariant variant = variantRepository
                .findByProductIdAndColorAndStorage(productId, color, storage)
                .orElse(null);

        if (variant == null) {
            return ResponseEntity.status(404).body("Không tìm thấy biến thể với productId=" + productId +
                    ", color=" + color + ", storage=" + storage);
        }

        return ResponseEntity.ok(variant);
    }

    // ✅ Thêm biến thể mới
    @PostMapping
    public ResponseEntity<?> createVariant(
            @RequestParam Long productId,
            @RequestBody ProductVariant variant) {

        Product product = productRepository.findById(productId)
                .orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm với ID = " + productId);
        }

        variant.setProduct(product);
        ProductVariant saved = variantRepository.save(variant);
        return ResponseEntity.ok(saved);
    }

    // ✅ Cập nhật biến thể
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVariant(@PathVariable Long id, @RequestBody ProductVariant variant) {
        return variantRepository.findById(id).map(v -> {
            v.setVariantName(variant.getVariantName());
            v.setColor(variant.getColor());
            v.setStorage(variant.getStorage());
            v.setPrice(variant.getPrice());
            v.setStock(variant.getStock());
            v.setImg(variant.getImg());

            if (variant.getProduct() != null) {
                v.setProduct(variant.getProduct());
            }

            variantRepository.save(v);
            return ResponseEntity.ok(v);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Xóa biến thể
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long id) {
        if (!variantRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        variantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

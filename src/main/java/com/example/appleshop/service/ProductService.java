package com.example.appleshop.service;

import com.example.appleshop.entity.Product;
import com.example.appleshop.entity.ProductVariant;
import com.example.appleshop.repository.ProductRepository;
import com.example.appleshop.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;

    // =========================
    // 🔹 CRUD cho PRODUCT
    // =========================

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id = " + id));
    }

    public Product createProduct(Product product) {
        // Không lưu variant ở đây, chỉ lưu sản phẩm
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStock(updatedProduct.getStock());
        existing.setImg(updatedProduct.getImg());
        existing.setCategory(updatedProduct.getCategory());

        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Không tồn tại sản phẩm với id = " + id);
        }
        productRepository.deleteById(id);
    }

    // =========================
    // 🔹 CRUD cho PRODUCT VARIANT
    // =========================

    public ProductVariant addVariant(Long productId, ProductVariant variant) {
        Product product = getProductById(productId);
        variant.setProduct(product);
        return variantRepository.save(variant);
    }

    public List<ProductVariant> getVariantsByProduct(Long productId) {
        // Kiểm tra product có tồn tại không
        getProductById(productId);
        return variantRepository.findByProductId(productId);
    }

    public ProductVariant updateVariant(Long id, ProductVariant updatedVariant) {
        ProductVariant existing = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với id = " + id));

        existing.setVariantName(updatedVariant.getVariantName());
        existing.setColor(updatedVariant.getColor());
        existing.setStorage(updatedVariant.getStorage());
        existing.setPrice(updatedVariant.getPrice());
        existing.setStock(updatedVariant.getStock());
        existing.setImg(updatedVariant.getImg());

        return variantRepository.save(existing);
    }

    public void deleteVariant(Long variantId) {
        if (!variantRepository.existsById(variantId)) {
            throw new RuntimeException("Không tồn tại biến thể với id = " + variantId);
        }
        variantRepository.deleteById(variantId);
    }
}

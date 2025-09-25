package com.example.appleshop.service;

import com.example.appleshop.entity.Product;
import com.example.appleshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Lấy tất cả products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Lấy product theo id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Tạo product mới
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Cập nhật product
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setImg(productDetails.getImg());
        product.setCategory(productDetails.getCategory());

        return productRepository.save(product);
    }

    // Xóa product
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
        productRepository.delete(product);
    }
}

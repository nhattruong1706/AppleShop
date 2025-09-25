package com.example.appleshop.service;

import com.example.appleshop.entity.Category;
import com.example.appleshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy tất cả categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Lấy category theo id
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    // Tạo category mới
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Cập nhật category
    public Category updateCategory(Integer id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));

        category.setName(categoryDetails.getName());
        return categoryRepository.save(category);
    }

    // Xóa category
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
        categoryRepository.delete(category);
    }
}

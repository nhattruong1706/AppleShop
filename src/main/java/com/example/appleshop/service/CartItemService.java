package com.example.appleshop.service;

import com.example.appleshop.entity.CartItemEntity;
import com.example.appleshop.entity.ProductVariant;
import com.example.appleshop.entity.UserEntity;
import com.example.appleshop.repository.CartItemRepository;
import com.example.appleshop.repository.ProductVariantRepository;
import com.example.appleshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    // Lấy danh sách cart của 1 user
    public List<CartItemEntity> getCartByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartItemRepository.findByUser(user);
    }

    // Thêm hoặc cập nhật sản phẩm vào giỏ
    public CartItemEntity addToCart(Long userId, Long variantId, int qty) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        // Nếu sản phẩm đã tồn tại trong giỏ → cộng thêm số lượng
        List<CartItemEntity> existingItems = cartItemRepository.findByUser(user);
        for (CartItemEntity item : existingItems) {
            if (item.getVariant().getId().equals(variantId)) {
                item.setQty(item.getQty() + qty);
                return cartItemRepository.save(item);
            }
        }

        // Nếu chưa có → tạo mới
        CartItemEntity newItem = new CartItemEntity();
        newItem.setUser(user);
        newItem.setVariant(variant);
        newItem.setQty(qty);
        return cartItemRepository.save(newItem);
    }

    // Xóa 1 sản phẩm khỏi giỏ
    public void removeItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    // Xóa toàn bộ giỏ hàng của user
    public void clearCart(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<CartItemEntity> items = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(items);
    }
}

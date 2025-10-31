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

    // ✅ Lấy danh sách giỏ hàng của user
    public List<CartItemEntity> getCartByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartItemRepository.findByUser(user);
    }

    // ✅ Thêm sản phẩm vào giỏ hoặc cập nhật (tăng)
    public CartItemEntity addToCart(Long userId, Long variantId, int qty) {
        if (qty <= 0) throw new RuntimeException("Số lượng phải lớn hơn 0");

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        CartItemEntity item = cartItemRepository.findByUserAndVariant(user, variant)
                .orElse(null);

        if (item != null) {
            int newQty = item.getQty() + qty;
            if (newQty > variant.getStock()) {
                throw new RuntimeException("⚠️ Số lượng vượt quá tồn kho!");
            }
            item.setQty(newQty);
            return cartItemRepository.save(item);
        }

        // Nếu chưa có item, kiểm tra tồn kho
        if (qty > variant.getStock()) {
            throw new RuntimeException("⚠️ Số lượng vượt quá tồn kho!");
        }

        CartItemEntity newItem = new CartItemEntity();
        newItem.setUser(user);
        newItem.setVariant(variant);
        newItem.setQty(qty);

        return cartItemRepository.save(newItem);
    }

    // ✅ Giảm số lượng sản phẩm
    public CartItemEntity decreaseQty(Long userId, Long variantId, int qty) {
        if (qty <= 0) throw new RuntimeException("Số lượng phải lớn hơn 0");

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        CartItemEntity item = cartItemRepository.findByUserAndVariant(user, variant)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ"));

        if (item.getQty() <= qty) {
            // Nếu giảm hết → xóa luôn
            cartItemRepository.delete(item);
            return null;
        }

        item.setQty(item.getQty() - qty);
        return cartItemRepository.save(item);
    }

    // ✅ Xóa 1 sản phẩm khỏi giỏ
    public void removeItem(Long itemId) {
        if (!cartItemRepository.existsById(itemId)) {
            throw new RuntimeException("Cart item not found");
        }
        cartItemRepository.deleteById(itemId);
    }

    // ✅ Xóa toàn bộ giỏ hàng của user
    public void clearCart(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<CartItemEntity> items = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(items);
    }
}

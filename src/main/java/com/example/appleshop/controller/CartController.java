package com.example.appleshop.controller;

import com.example.appleshop.entity.CartItemEntity;
import com.example.appleshop.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemService cartItemService;

    // ✅ Lấy danh sách giỏ hàng của 1 user
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemEntity>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartItemService.getCartByUserId(userId));
    }

    // ✅ Thêm sản phẩm vào giỏ
    @PostMapping("/add")
    public ResponseEntity<CartItemEntity> addToCart(
            @RequestParam Long userId,
            @RequestParam Long variantId,
            @RequestParam int qty
    ) {
        return ResponseEntity.ok(cartItemService.addToCart(userId, variantId, qty));
    }

    // ✅ Xóa 1 item khỏi giỏ
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        cartItemService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // ✅ Xóa toàn bộ giỏ của user
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartItemService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}

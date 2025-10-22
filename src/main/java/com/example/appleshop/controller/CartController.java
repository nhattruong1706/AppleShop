package com.example.appleshop.controller;

import com.example.appleshop.entity.CartItemEntity;
import com.example.appleshop.entity.UserEntity;
import com.example.appleshop.service.CartItemService;
import com.example.appleshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor  // Lombok sẽ tạo constructor với tất cả final fields
public class CartController {

    private final CartItemService cartItemService;  // Spring sẽ inject
    private final UserRepository userRepository;    // Spring sẽ inject

    // Lấy user hiện tại từ session đăng nhập
    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + username));
    }

    // Lấy danh sách giỏ hàng của user đang đăng nhập
    @GetMapping
    public ResponseEntity<?> getCart() {
        try {
            UserEntity currentUser = getCurrentUser();
            List<CartItemEntity> cart = cartItemService.getCartByUserId(currentUser.getId());
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // Thêm sản phẩm vào giỏ hàng (tăng)
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestParam Long variantId,
            @RequestParam(defaultValue = "1") int qty
    ) {
        try {
            UserEntity currentUser = getCurrentUser();
            CartItemEntity item = cartItemService.addToCart(currentUser.getId(), variantId, qty);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Giảm số lượng sản phẩm
    @PostMapping("/decrease")
    public ResponseEntity<?> decreaseCart(
            @RequestParam Long variantId,
            @RequestParam(defaultValue = "1") int qty
    ) {
        try {
            UserEntity currentUser = getCurrentUser();
            CartItemEntity item = cartItemService.decreaseQty(currentUser.getId(), variantId, qty);
            return ResponseEntity.ok(item); // trả về null nếu đã xóa
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Xóa 1 item khỏi giỏ hàng
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        try {
            cartItemService.removeItem(itemId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Xóa toàn bộ giỏ hàng
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        try {
            UserEntity currentUser = getCurrentUser();
            cartItemService.clearCart(currentUser.getId());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

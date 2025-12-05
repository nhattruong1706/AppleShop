
package com.example.appleshop.controller;

import com.example.appleshop.entity.OrderEntity;
import com.example.appleshop.entity.UserEntity;
import com.example.appleshop.repository.OrderRepository;
import com.example.appleshop.repository.ProductVariantRepository;
import com.example.appleshop.repository.UserRepository;
import com.example.appleshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderRepository orderRepository; // 🔹 Thêm dòng này

    // 🔹 Lấy user hiện tại từ session
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

    // 🔹 API tạo đơn hàng
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            UserEntity currentUser = getCurrentUser();
            OrderEntity order = orderService.createOrderFromMap(currentUser, data);
            return ResponseEntity.ok(Map.of(
                    "message", "✅ Đặt hàng thành công!",
                    "orderId", order.getId(),
                    "total", order.getTotalAmount()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // 🔹 Lấy danh sách đơn hàng của user (chỉ hiện field cần thiết)
    @GetMapping
    public ResponseEntity<?> getMyOrders() {
        try {
            UserEntity currentUser = getCurrentUser();
            List<Map<String, Object>> orders = orderRepository.findOrdersByUserId(currentUser.getId());
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    // 🔹 Admin: lấy tất cả đơn hàng
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        List<Map<String, Object>> orders = orderRepository.findAllOrderSummary();
        return ResponseEntity.ok(orders);
    }

    // 🔹 Cập nhật trạng thái đơn hàng
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STAFF')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 🔹 Hủy đơn hàng
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok(Map.of("message", "Đơn hàng đã được hủy"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/cancelled")
    public ResponseEntity<?> deleteAllCancelledOrders() {
        try {
            orderService.deleteAllCancelledOrders();
            return ResponseEntity.ok(Map.of("message", "Đã xóa tất cả đơn hàng đã hủy"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 🔹 Xem chi tiết đơn hàng
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        OrderEntity order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy đơn hàng"));
        }
        return ResponseEntity.ok(order);
    }
    @GetMapping("/{id}/variants")
    public ResponseEntity<?> getOrderVariants(@PathVariable Long id) {
        try {
            List<Map<String, Object>> result = orderRepository.findOrderVariantsByOrderId(id);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy đơn hàng hoặc sản phẩm"));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/{id}/street")
    public ResponseEntity<?> getOrderStreet(@PathVariable Long id) {
        try {
            List<Map<String, Object>> result = orderRepository.findOrderStreetByOrderId(id);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy địa chỉ cho đơn hàng này"));
            }
            return ResponseEntity.ok(result.get(0)); // Một đơn chỉ có một địa chỉ
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}

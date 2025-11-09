package com.example.appleshop.service;

import com.example.appleshop.entity.*;
import com.example.appleshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    /** 🧾 Tạo đơn hàng từ dữ liệu Map (không dùng DTO) */
    @Transactional
    public OrderEntity createOrderFromMap(UserEntity user, Map<String, Object> data) {
        // 🏠 Lấy địa chỉ giao hàng
        if (!data.containsKey("addressId")) {
            throw new RuntimeException("Thiếu thông tin địa chỉ giao hàng!");
        }
        Long addressId = Long.valueOf(data.get("addressId").toString());
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng"));

        // 📦 Lấy danh sách sản phẩm
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) data.get("items");
        if (itemList == null || itemList.isEmpty()) {
            throw new RuntimeException("Danh sách sản phẩm trống!");
        }

        // 💰 Tạo đơn hàng ban đầu
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus("PENDING");
        order.setTotalAmount(BigDecimal.ZERO);

        // Lưu trước để sinh ID
        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;

        // 🔹 Lưu từng item và tính tổng
        for (Map<String, Object> i : itemList) {
            Long variantId = Long.valueOf(i.get("variantId").toString());
            int qty = Integer.parseInt(i.get("qty").toString());

            ProductVariant variant = productVariantRepository.findById(variantId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            if (variant.getStock() < qty)
                throw new RuntimeException("Sản phẩm " + variant.getVariantName() + " vượt quá tồn kho!");

            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setVariant(variant);
            item.setQty(qty);
            item.setPrice(BigDecimal.valueOf(variant.getPrice()));
            orderItemRepository.save(item);

            // Cập nhật tồn kho
            variant.setStock(variant.getStock() - qty);
            productVariantRepository.save(variant);

            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        // 🔹 Cập nhật tổng tiền
        order.setTotalAmount(total);
        orderRepository.save(order);

        // 💳 Xử lý thanh toán
        if (data.containsKey("payment")) {
            Map<String, Object> paymentMap = (Map<String, Object>) data.get("payment");
            PaymentEntity payment = new PaymentEntity();
            payment.setOrder(order);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod(paymentMap.getOrDefault("method", "COD").toString());
            payment.setPaymentStatus(paymentMap.getOrDefault("paymentStatus", "UNPAID").toString());
            paymentRepository.save(payment);
        } else {
            // Nếu không có thông tin thanh toán, mặc định là COD chưa thanh toán
            PaymentEntity payment = new PaymentEntity();
            payment.setOrder(order);
            payment.setPaymentMethod("COD");
            payment.setPaymentStatus("UNPAID");
            paymentRepository.save(payment);
        }

        // 🧹 Xóa giỏ hàng của user
        List<CartItemEntity> cart = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(cart);

        return order;
    }

    public List<OrderEntity> getOrdersByUser(UserEntity user) {
        return orderRepository.findByUser(user);
    }

    public OrderEntity getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Đơn hàng đã bị hủy trước đó");
        }

        // Cộng lại tồn kho cho từng sản phẩm
        for (OrderItemEntity item : order.getItems()) {
            ProductVariant variant = item.getVariant();
            variant.setStock(variant.getStock() + item.getQty());
            productVariantRepository.save(variant);
        }

        // Cập nhật trạng thái đơn hàng
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    /** ✅ Cập nhật trạng thái đơn hàng (lưu employee khi xác nhận) */
    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        String current = order.getStatus().toUpperCase();
        newStatus = newStatus.toUpperCase();

        List<String> validFlow = List.of("PENDING", "CONFIRMED", "SHIPPING", "SUCCESS", "CANCELLED");
        if (!validFlow.contains(newStatus)) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        // 🔹 Ghi lại nhân viên xác nhận khi chuyển sang CONFIRMED
        if (current.equals("PENDING") && newStatus.equals("CONFIRMED")) {
            order.setStatus("CONFIRMED");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                String username = auth.getName();
                UserEntity employee = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + username));
                order.setEmployee(employee);
            }

        } else if (current.equals("CONFIRMED") && newStatus.equals("SHIPPING")) {
            order.setStatus("SHIPPING");

        } else if (current.equals("SHIPPING") && newStatus.equals("SUCCESS")) {
            order.setStatus("SUCCESS");

        } else if (newStatus.equals("CANCELLED")) {
            for (OrderItemEntity item : order.getItems()) {
                var variant = item.getVariant();
                if (variant != null) {
                    variant.setStock(variant.getStock() + item.getQty());
                    productVariantRepository.save(variant);
                }
            }
            order.setStatus("CANCELLED");
        } else {
            throw new RuntimeException("Không thể chuyển từ " + current + " sang " + newStatus);
        }

        orderRepository.save(order);
    }

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void deleteAllCancelledOrders() {
        List<OrderEntity> cancelledOrders = orderRepository.findByStatus("CANCELLED");
        orderRepository.deleteAll(cancelledOrders);
    }
}

package com.example.appleshop.repository;

import com.example.appleshop.entity.OrderEntity;
import com.example.appleshop.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query("""
    SELECT new map(
        o.id as orderId,
        o.status as status,
        o.totalAmount as totalAmount,
        a.street as street,
        a.city as city,
        i.qty as qty,
        i.price as price,
        v.id as variantId,
        v.variantName as variantName,
        v.color as color,
        v.storage as storage,
        v.img as variantImg,
        p.name as productName
    )
    FROM OrderEntity o
    JOIN o.items i
    JOIN i.variant v
    JOIN v.product p
    LEFT JOIN o.address a
    WHERE o.id = :orderId
""")
    List<Map<String, Object>> findOrderVariantsByOrderId(Long orderId);
    @Query("""
    SELECT new map(
        o.id as orderId,
        a.street as street
    )
    FROM OrderEntity o
    JOIN o.address a
    WHERE o.id = :orderId
""")
    List<Map<String, Object>> findOrderStreetByOrderId(Long orderId);

    // 🔹 Admin: Lấy tất cả đơn, có cả địa chỉ
    @Query("""
        SELECT new map(
            o.id as id,
            o.status as status,
            o.totalAmount as totalAmount,
            u.fullName as userName,
            u.email as userEmail,
            a.street as street,
            a.city as city
        )
        FROM OrderEntity o
        JOIN o.user u
        LEFT JOIN o.address a
        ORDER BY o.id DESC
    """)
    List<Map<String, Object>> findAllOrderSummary();

    // 🔹 User: Lấy đơn hàng của user đang đăng nhập (có địa chỉ)
    @Query("""
        SELECT new map(
            o.id as id,
            o.status as status,
            o.totalAmount as totalAmount,
            a.street as street,
            a.city as city
        )
        FROM OrderEntity o
        JOIN o.user u
        LEFT JOIN o.address a
        WHERE u.id = :userId
        ORDER BY o.id DESC
    """)
    List<Map<String, Object>> findOrdersByUserId(Long userId);

    List<OrderEntity> findByUser(UserEntity user);
    List<OrderEntity> findByStatus(String status);

}

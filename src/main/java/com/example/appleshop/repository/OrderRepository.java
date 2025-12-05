package com.example.appleshop.repository;

import com.example.appleshop.entity.OrderEntity;
import com.example.appleshop.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
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
    // ======================= THỐNG KÊ DOANH THU =======================

    // Tổng doanh thu
    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o")
    Double getTotalRevenue();


    // Doanh thu theo từng sản phẩm
    @Query("""
        SELECT new map(
            p.name as name,
            SUM(i.qty) as quantity,
            SUM(i.qty * i.price) as revenue
        )
        FROM OrderEntity o
        JOIN o.items i
        JOIN i.variant v
        JOIN v.product p
        GROUP BY p.name
    """)
    List<Map<String, Object>> getProductRevenue();


    // Doanh thu theo ngày
    @Query("""
    SELECT new map(
        o.createdAt as date,
        SUM(o.totalAmount) as revenue
    )
    FROM OrderEntity o
    WHERE o.createdAt BETWEEN :start AND :end
    GROUP BY o.createdAt
    ORDER BY o.createdAt ASC
""")
    List<Map<String, Object>> getRevenueByDate(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );




    List<OrderEntity> findByUser(UserEntity user);
    List<OrderEntity> findByStatus(String status);

}

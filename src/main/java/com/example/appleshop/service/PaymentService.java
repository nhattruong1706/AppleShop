package com.example.appleshop.service;

import com.example.appleshop.entity.PaymentEntity;
import java.util.List;

public interface PaymentService {
    List<PaymentEntity> getAllPayments();
    PaymentEntity getPaymentById(Long id);
    List<PaymentEntity> getPaymentsByOrder(Long orderId);
    PaymentEntity createPayment(PaymentEntity payment);
    PaymentEntity updatePayment(Long id, PaymentEntity payment);
    void deletePayment(Long id);
}

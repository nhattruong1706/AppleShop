package com.example.appleshop.service.impl;

import com.example.appleshop.entity.PaymentEntity;
import com.example.appleshop.repository.PaymentRepository;
import com.example.appleshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public List<PaymentEntity> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public PaymentEntity getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Override
    public List<PaymentEntity> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public PaymentEntity createPayment(PaymentEntity payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public PaymentEntity updatePayment(Long id, PaymentEntity payment) {
        PaymentEntity existing = getPaymentById(id);
        if (existing == null) return null;

        existing.setPaymentMethod(payment.getPaymentMethod());
        existing.setPaymentStatus(payment.getPaymentStatus());
        existing.setPaymentDate(payment.getPaymentDate());
        existing.setTransactionId(payment.getTransactionId());
        return paymentRepository.save(existing);
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}

package com.example.appleshop.controller;

import com.example.appleshop.entity.PaymentEntity;
import com.example.appleshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentEntity> getAll() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public PaymentEntity getById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/order/{orderId}")
    public List<PaymentEntity> getByOrder(@PathVariable Long orderId) {
        return paymentService.getPaymentsByOrder(orderId);
    }

    @PostMapping
    public PaymentEntity create(@RequestBody PaymentEntity payment) {
        return paymentService.createPayment(payment);
    }

    @PutMapping("/{id}")
    public PaymentEntity update(@PathVariable Long id, @RequestBody PaymentEntity payment) {
        return paymentService.updatePayment(id, payment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }
}

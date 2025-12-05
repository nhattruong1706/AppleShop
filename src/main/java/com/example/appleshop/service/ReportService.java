package com.example.appleshop.service;

import com.example.appleshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    public Double totalRevenue() {
        return orderRepository.getTotalRevenue();
    }

    public List<Map<String, Object>> productRevenue() {
        return orderRepository.getProductRevenue();
    }

    public List<Map<String, Object>> revenueByDate(LocalDate start, LocalDate end) {
        return orderRepository.getRevenueByDate(start, end);
    }

}

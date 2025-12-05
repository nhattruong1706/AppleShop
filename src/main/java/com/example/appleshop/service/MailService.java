package com.example.appleshop.service;

import com.example.appleshop.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    // Gửi OTP
    public void sendOtp(String toEmail, String otp) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("🔒 Mã OTP xác thực tài khoản");
        msg.setText("👋 Xin chào!\nCảm ơn bạn đã đăng ký tại Apple Shop.\n\n"
                + "🔑 Mã OTP của bạn là: " + otp + "\n⏱ Có hiệu lực trong 5 phút.");
        mailSender.send(msg);
    }

    // Gửi thông báo đơn hàng đã xác nhận
    public void sendOrderConfirmation(OrderEntity order) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(order.getUser().getEmail());
        msg.setSubject("✅ Đơn hàng #" + order.getId() + " đã được xác nhận");
        msg.setText("👋 Xin chào " + order.getUser().getFullName() + "!\n\n"
                + "📦 Đơn hàng #" + order.getId() + " của bạn đã được xác nhận.\n"
                + "💰 Tổng tiền: " + order.getTotalAmount() + "₫\n\n"
                + " Cảm ơn bạn đã đặt hàng tại Apple Shop!");
        mailSender.send(msg);
    }
}

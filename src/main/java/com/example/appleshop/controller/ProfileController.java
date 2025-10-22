package com.example.appleshop.controller;

import com.example.appleshop.entity.UserEntity;
import com.example.appleshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;


    // ✅ Lấy user hiện tại thông qua Spring Security
    private Optional<UserEntity> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        return userRepo.findByUsername(auth.getName());
    }

    // ✅ Lấy thông tin hồ sơ
    @GetMapping
    public ResponseEntity<?> getProfile() {
        return getCurrentUser()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).body(Map.of(
                        "error", "Chưa đăng nhập"
                )));
    }

    // ✅ Cập nhật hồ sơ (Trả về JSON thay vì text)
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UserEntity updated) {
        return getCurrentUser().map(current -> {
            current.setFullName(updated.getFullName());
            current.setEmail(updated.getEmail());
            current.setPhone(updated.getPhone());

            if (updated.getPasswordHash() != null && !updated.getPasswordHash().isEmpty()) {
                current.setPasswordHash(updated.getPasswordHash());
            }

            userRepo.save(current);

            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật hồ sơ thành công!",
                    "user", current
            ));
        }).orElse(ResponseEntity.status(401).body(Map.of(
                "error", "Chưa đăng nhập"
        )));
    }

    // ✅ Đổi mật khẩu
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        return getCurrentUser().map(current -> {
            String oldPass = body.get("oldPassword");
            String newPass = body.get("newPassword");

            if (oldPass == null || newPass == null || oldPass.isEmpty() || newPass.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng nhập đầy đủ thông tin!"));
            }

            // 🔒 So sánh đúng cách với PasswordEncoder
            if (!passwordEncoder.matches(oldPass, current.getPasswordHash())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu cũ không đúng!"));
            }

            current.setPasswordHash(passwordEncoder.encode(newPass));
            userRepo.save(current);

            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
        }).orElse(ResponseEntity.status(401).body(Map.of(
                "error", "Chưa đăng nhập"
        )));
    }

}

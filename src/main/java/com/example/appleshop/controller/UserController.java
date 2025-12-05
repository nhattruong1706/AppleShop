package com.example.appleshop.controller;

import com.example.appleshop.entity.UserEntity;
import com.example.appleshop.repository.UserRepository;
import com.example.appleshop.service.UserService;
import com.example.appleshop.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService; // 👉 thêm vào đây

    // ----------------------------
    //    ĐĂNG KÝ GỬI OTP
    // ----------------------------
    @PostMapping("/register")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        // Tạo OTP 6 số
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Gửi mail
        mailService.sendOtp(email, otp);

        // Trả OTP về client
        return ResponseEntity.ok(Map.of(
                "message", "OTP đã được gửi tới email",
                "otp", otp
        ));
    }

    // ----------------------------
    //      API CÓ SẴN
    // ----------------------------

    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/whoami")
    public ResponseEntity<String> whoAmI() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(username);
    }

    @GetMapping("/search")
    public List<UserEntity> searchUsers(@RequestParam String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserEntity createUser(@RequestBody UserEntity user) {
        return userService.createUser(user);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @RequestBody UserEntity userDetails) {
        try {
            UserEntity updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

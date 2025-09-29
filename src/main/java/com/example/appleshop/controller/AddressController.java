package com.example.appleshop.controller;

import com.example.appleshop.entity.Address;
import com.example.appleshop.entity.UserEntity;
import com.example.appleshop.repository.UserRepository;
import com.example.appleshop.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserRepository userRepository;

    // Lấy tất cả địa chỉ của user đang đăng nhập
    @GetMapping("/me")
    public ResponseEntity<List<Address>> getMyAddresses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(addressService.getAddressesByUser(user.getId()));
    }

    // Lấy địa chỉ cụ thể của user đang đăng nhập
    @GetMapping("/{id}")
    public ResponseEntity<Address> getMyAddress(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressService.getAddress(id);
        if (address == null || !address.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(address);
    }

    // Thêm địa chỉ mới cho user đang đăng nhập
    @PostMapping
    public ResponseEntity<Address> createMyAddress(@RequestBody Address address) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        address.setUser(user);
        Address saved = addressService.createAddress(address);
        return ResponseEntity.ok(saved);
    }

    // Cập nhật địa chỉ của user đang đăng nhập
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateMyAddress(@PathVariable Long id, @RequestBody Address newAddress) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address existing = addressService.getAddress(id);
        if (existing == null || !existing.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        Address updated = addressService.updateAddress(id, newAddress);
        return ResponseEntity.ok(updated);
    }

    // Xóa địa chỉ của user đang đăng nhập
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyAddress(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address existing = addressService.getAddress(id);
        if (existing == null || !existing.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.appleshop.repository;

import com.example.appleshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Có thể thêm query method nếu cần, ví dụ:
    // Optional<User> findByUsername(String username);
}

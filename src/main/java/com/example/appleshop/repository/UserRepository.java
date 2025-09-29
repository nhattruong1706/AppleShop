package com.example.appleshop.repository;

import com.example.appleshop.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // Có thể thêm query method nếu cần, ví dụ:
    Optional<UserEntity> findByUsername(String username);
}

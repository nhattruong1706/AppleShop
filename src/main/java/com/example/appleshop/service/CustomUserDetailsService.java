package com.example.appleshop.service;




import com.example.appleshop.entity.UserEntity;
import com.example.appleshop.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash()) // đã được mã hoá BCrypt
                .roles(user.getRole().replace("ROLE_", "")) // bỏ prefix để Spring tự thêm
                .build();
    }
}

package com.example.appleshop.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data

public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tương ứng với IDENTITY(1,1) bên SQL Server
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "role")
    private String role;  // ví dụ: ADMIN, USER

    public UserEntity() {

    }
}

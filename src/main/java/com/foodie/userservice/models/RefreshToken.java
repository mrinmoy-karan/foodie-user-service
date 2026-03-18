package com.foodie.userservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String token;
    @Column(nullable = false) private Instant expiryDate;
    @OneToOne @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}

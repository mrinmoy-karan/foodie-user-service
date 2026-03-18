package com.foodie.userservice.service;

import com.foodie.userservice.dto.LoginRequest;
import com.foodie.userservice.dto.UserRegistrationRequest;
import com.foodie.userservice.models.User;
import com.foodie.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(UserRegistrationRequest request) {
        // 1. Check if email exists
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered!");
        }

        // 2. Create and Encrypt
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setMobile(request.mobile());
        user.setPassword(passwordEncoder.encode(request.password())); // Hashing

        userRepository.save(user);
        return "User registered successfully!";
    }

    public String loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Match raw password with hashed password
        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            return "Login Successful! Welcome " + user.getName();
        } else {
            return "Invalid Credentials";
        }
    }
}
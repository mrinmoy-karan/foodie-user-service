package com.foodie.userservice.service;

import com.foodie.userservice.dto.LoginRequest;
import com.foodie.userservice.dto.UserRegistrationRequest;
import com.foodie.userservice.exception.UserNotFoundException;
import com.foodie.userservice.models.Role;
import com.foodie.userservice.models.User;
import com.foodie.userservice.repository.RoleRepository;
import com.foodie.userservice.repository.UserRepository;
import com.foodie.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

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

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Default Role 'USER' not found in database."));

        user.getRoles().add(defaultRole);

        userRepository.save(user);
        return "User registered successfully!";
    }

    public String loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));



        // Match raw password with hashed password
        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            return jwtService.generateToken(user);
        } else {
            return "Invalid Credentials";
        }
    }

    public void deactivateUser(Long id) {
        // 1. Check if user exists first
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found: " + id);
        }

        // 2. Call your custom @Modifying query
        userRepository.softDelete(id);
    }
}
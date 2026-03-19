package com.foodie.userservice.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodie.userservice.exception.UserNotFoundException;
import com.foodie.userservice.models.User;
import com.foodie.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Only Admins can enter this entire controller
public class AdminController {

    private final UserRepository userRepository;
    @PostConstruct
    public void init() {
        System.out.println("AdminController loaded successfully!");
    }

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        // 1. Find the target user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // 2. Business Logic: Prevent admin from deactivating themselves (Safety Check)
        // String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        // if(user.getEmail().equals(currentUserEmail)) return ResponseEntity.badRequest().body("Cannot deactivate yourself");

        // 3. Set inactive
        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User " + user.getEmail() + " has been deactivated successfully."));
    }


    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        User user = findUser(id);

        // Industry Tip: Check if already active to avoid redundant DB writes
        if (user.isActive()) {
            return ResponseEntity.ok(Map.of("message", "User is already active."));
        }

        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User " + user.getEmail() + " has been reactivated."));
    }

    // Private helper to keep code DRY
    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
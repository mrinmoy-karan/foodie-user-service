package com.foodie.userservice.controller;

import com.foodie.userservice.dto.UpdateProfileRequest;
import com.foodie.userservice.dto.UserResponseDTO;

import com.foodie.userservice.exception.UserNotFoundException;
import com.foodie.userservice.models.User;
import com.foodie.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        // Extract the email from the JWT (set by JwtAuthenticationFilter)
        String currentUserEmail = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        // Fetch user from MySQL
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email " + currentUserEmail + " not found"));

        // Map Entity to DTO
        UserResponseDTO response = UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRoles().toString())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt().toString())
                .build();

        return ResponseEntity.ok(response);
    }



    @PutMapping("/update")
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody UpdateProfileRequest request) {
        // 1. Get current user's email from JWT context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Find user in MySQL
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // 3. Update only the allowed fields
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getMobile() != null) {
            user.setMobile(request.getMobile());
        }

        // 4. Save back to MySQL (JPA Auditing will automatically update 'modifiedAt')
        User updatedUser = userRepository.save(user);

        // 5. Return the updated profile (Convert to DTO)
        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    // Helper method to keep code clean
    private UserResponseDTO convertToDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRoles().toString())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}
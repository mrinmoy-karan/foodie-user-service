package com.foodie.userservice.controller;

import com.foodie.userservice.dto.ApiResponse;
import com.foodie.userservice.dto.UpdateProfileRequest;
import com.foodie.userservice.dto.UserResponseDTO;
import com.foodie.userservice.exception.UserNotFoundException;
import com.foodie.userservice.models.Role;
import com.foodie.userservice.models.User;
import com.foodie.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserResponseDTO dto = convertToDto(user);
        // Wrap in our new standard!
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", dto));
    }
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(@RequestBody UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getMobile() != null) user.setMobile(request.getMobile());
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", convertToDto(updatedUser)));
    }

    // This is your "Source of Truth" for how a Profile looks
    private UserResponseDTO convertToDto(User user) {
        String rolesString = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(rolesString)
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}
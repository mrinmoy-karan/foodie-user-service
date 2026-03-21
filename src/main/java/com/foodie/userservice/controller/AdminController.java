package com.foodie.userservice.controller;

import com.foodie.userservice.dto.ApiResponse;
import com.foodie.userservice.dto.UserResponseDTO;
import com.foodie.userservice.exception.UserNotFoundException;
import com.foodie.userservice.models.User;
import com.foodie.userservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        User user = findUser(id);
        if (user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("Cannot deactivate another admin");
        }
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User " + user.getEmail() + " deactivated successfully", null));
    }

    @PatchMapping("/users/{id}/reactivate")
    public ResponseEntity<ApiResponse<Void>> reactivateUser(@PathVariable Long id) {
        User user = findUser(id);
        user.setActive(true);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User " + user.getEmail() + " reactivated successfully", null));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserResponseDTO> dtoPage = userPage.map(this::convertToDto);
        return ResponseEntity.ok(dtoPage);
    }

    private UserResponseDTO convertToDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt().toString());
        // Convert Set<Role> to a comma-separated String or List of Strings
        String roles = user.getRoles().stream()
                .map(role -> role.getName().toString())
                .collect(java.util.stream.Collectors.joining(", "));
        dto.setRole(roles);

        return dto;
    }
}
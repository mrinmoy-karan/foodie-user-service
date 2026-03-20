package com.foodie.userservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodie.userservice.exception.UserNotFoundException;
import com.foodie.userservice.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Success: Should call softDelete when user exists")
    void testDeactivateUser_Success() {
        // 1. Arrange: Mock the existence check
        when(userRepository.existsById(1L)).thenReturn(true);

        // 2. Act
        userService.deactivateUser(1L);

        // 3. Assert: Verify the custom softDelete method was called
        verify(userRepository, times(1)).softDelete(1L);

        // Safety check: ensure no other delete methods were called
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Fail: Should throw Exception when user does not exist")
    void testDeactivateUser_NotFound() {
        // 1. Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // 2. Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.deactivateUser(99L);
        });

        // Verify softDelete was NEVER called for a non-existent user
        verify(userRepository, never()).softDelete(anyLong());
    }
}
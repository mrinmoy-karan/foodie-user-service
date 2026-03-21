package com.foodie.userservice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String status;      // "Success" or "Error"
    private String message;     // Human-readable message
    private int code;           // HTTP Status Code (200, 401, 400, etc.)
    private T data;             // The actual object (User, Token, etc.) or null
    private LocalDateTime timestamp;

    // Helper for quick success responses
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("Success")
                .message(message)
                .code(200)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
package com.foodie.userservice.dto;

// LoginRequest.java
public record LoginRequest(
    String email,
    String password
) {}
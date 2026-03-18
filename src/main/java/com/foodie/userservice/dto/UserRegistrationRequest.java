package com.foodie.userservice.dto;

// UserRegistrationRequest.java
public record UserRegistrationRequest(
    String name,
    String email,
    String mobile,
    String password
) {}
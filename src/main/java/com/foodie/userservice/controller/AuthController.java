package com.foodie.userservice.controller;

import com.foodie.userservice.dto.*;
import com.foodie.userservice.models.RefreshToken;
import com.foodie.userservice.security.JwtService;
import com.foodie.userservice.service.RefreshTokenService;
import com.foodie.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRegistrationRequest request) {
        String result = userService.registerUser(request);
       return ResponseEntity.ok(ApiResponse.success("User registered successfully",result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        // This line triggers CustomUserDetailsService and checks password automatically
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        // If we reach here, authentication was successful
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.email());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(jwtService.generateToken(userDetails))
                .token(refreshToken.getToken())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest refreshToken) {
        boolean isDeleted = refreshTokenService.logoutByToken(refreshToken.getRefreshToken());
        refreshTokenService.deleteByToken(refreshToken.getRefreshToken());
        if (isDeleted) {
            return ResponseEntity.ok("Logged out successfully");
        } else {
            // If the key was wrong, return 404 (Not Found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Invalid Refresh Token: Token not found or already logged out.");
        }
    }


}
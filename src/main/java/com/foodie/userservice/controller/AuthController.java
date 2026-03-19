package com.foodie.userservice.controller;

import com.foodie.userservice.dto.AuthResponse;
import com.foodie.userservice.dto.LoginRequest;
import com.foodie.userservice.dto.RefreshTokenRequest;
import com.foodie.userservice.dto.UserRegistrationRequest;
import com.foodie.userservice.models.RefreshToken;
import com.foodie.userservice.security.JwtService;
import com.foodie.userservice.service.RefreshTokenService;
import com.foodie.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<String> signup(@RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        if (auth.isAuthenticated()) {
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.email());
            return ResponseEntity.ok(AuthResponse.builder()
                    .accessToken(jwtService.generateToken(request.email(), auth.getAuthorities()))
                    .token(refreshToken.getToken()).build());
        }
        return ResponseEntity.status(401).body("Invalid Credentials");
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
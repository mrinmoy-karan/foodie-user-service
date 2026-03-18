package com.foodie.userservice.service;

import com.foodie.userservice.models.RefreshToken;
import com.foodie.userservice.models.User;
import com.foodie.userservice.repository.RefreshTokenRepository;
import com.foodie.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // 1. Create a new Refresh Token for a User
    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Optional: Delete existing tokens for this user to prevent multiple sessions
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(604800000)); // 7 Days expiry

        return refreshTokenRepository.save(refreshToken);
    }

    // 2. Verify if the token is still valid (not expired)
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    // 3. Find token by string
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // 4. Delete token (For Logout)
    @Transactional
    public String deleteByToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.isPresent()) {
            refreshTokenRepository.deleteByToken(token);
            return "Logged out successfully";
        }
        // Return a message indicating it wasn't found
        return "Token already invalid or does not exist";
    }

    @Transactional
    public boolean logoutByToken(String token) {
        int deletedCount = refreshTokenRepository.deleteByToken(token);
        // If deletedCount is 1, return true. If 0 (wrong key), return false.
        return deletedCount > 0;
    }
}
package com.foodie.userservice.repository;

import com.foodie.userservice.models.RefreshToken;
import com.foodie.userservice.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByUser(User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.token = :token")
    int deleteByToken(String token);
}
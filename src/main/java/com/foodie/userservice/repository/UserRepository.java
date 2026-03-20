package com.foodie.userservice.repository;


import com.foodie.userservice.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);
    // This is needed for your Login logic
    Optional<User> findByEmail(String email);

    // Custom query to "Soft Delete"
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    void softDelete(Long id);



    Optional<User> findByEmailAndIsActiveTrue(String email);
}

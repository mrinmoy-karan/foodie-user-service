package com.foodie.userservice.repository;


import com.foodie.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);
    // This is needed for your Login logic
    Optional<User> findByEmail(String email);

}

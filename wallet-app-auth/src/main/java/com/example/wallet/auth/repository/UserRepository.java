package com.example.wallet.auth.repository;

import com.example.wallet.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Find by username (case-insensitive example)
    Optional<User> findByUsernameIgnoreCase(String username);

    // Find by email (case-insensitive example)
    Optional<User> findByEmailIgnoreCase(String email);

    Boolean existsByUsernameIgnoreCase(String username);

    Boolean existsByEmailIgnoreCase(String email);
}
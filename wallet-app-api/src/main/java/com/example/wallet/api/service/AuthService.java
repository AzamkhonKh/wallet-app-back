package com.example.wallet.api.service;

import com.example.wallet.auth.domain.Role;
import com.example.wallet.auth.domain.User;
import com.example.wallet.auth.exception.UserAlreadyExistsException;
import com.example.wallet.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
     // AuthenticationManager will be provided by Spring Security Config
    private final AuthenticationManager authenticationManager;


    @Transactional // Ensure operation is atomic
    public User signUp(String username, String email, String password) {
        log.info("Attempting registration for username: {}", username);

        // Check if user already exists
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new UserAlreadyExistsException("Username is already taken: " + username);
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserAlreadyExistsException("Email is already in use: " + email);
        }

        // Create new user's account
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password)) // Hash the password
                .role(Role.USER) // Default role
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", username);
        return savedUser;
    }

     public UserDetails signIn(String usernameOrEmail, String password) {
         log.info("Attempting sign in for user: {}", usernameOrEmail);

         // Let Spring Security's AuthenticationManager handle the authentication
         // It uses the configured UserDetailsService and PasswordEncoder
         Authentication authentication = authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
         );

         // If authentication is successful, the principal will be our UserDetails object
         log.info("User authenticated successfully: {}", usernameOrEmail);
         return (UserDetails) authentication.getPrincipal();
     }
}
package com.example.wallet.api.controller;

import com.example.wallet.api.dto.JwtAuthenticationResponse;
import com.example.wallet.api.dto.SignInRequest;
import com.example.wallet.api.dto.SignUpRequest;
import com.example.wallet.api.dto.UserDto; // Simple DTO for registered user
import com.example.wallet.api.mapper.UserMapper;
import com.example.wallet.api.service.JwtService;
import com.example.wallet.auth.domain.User;
import com.example.wallet.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth") // Base path for auth endpoints
@RequiredArgsConstructor
@Slf4j
public class AuthController {

     private final AuthService authService;
     private final JwtService jwtService;

     @PostMapping("/signup")
     public ResponseEntity<UserDto> signUp(@Valid @RequestBody SignUpRequest request) {
          // AuthService handles existence check and throws exception if needed
          log.info("Received signup request for username: {}", request.getUsername());
          User registeredUser = authService.signUp(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword());
          // Map to DTO before sending response (exclude password)
          return new ResponseEntity<>(UserMapper.toDto(registeredUser), HttpStatus.CREATED);
     }

     @PostMapping("/signin")
     public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest request) {
          log.info("Received signin request for user: {}", request.getUsernameOrEmail());
          try {
               // AuthService performs authentication via AuthenticationManager
               UserDetails userDetails = authService.signIn(
                         request.getUsernameOrEmail(),
                         request.getPassword());

               // Generate tokens upon successful authentication
               String accessToken = jwtService.generateToken(userDetails);
               String refreshToken = jwtService.generateRefreshToken(userDetails);

               return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken));

          } catch (AuthenticationException e) {
               log.warn("Authentication failed for user {}: {}", request.getUsernameOrEmail(), e.getMessage());
               // Return 401 Unauthorized for bad credentials
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username/email or password");
          }
     }

     // TODO: Add endpoint for token refresh if implementing refresh token strategy
     // @PostMapping("/refresh")
     // public ResponseEntity<?> refreshToken(...) { ... }
}
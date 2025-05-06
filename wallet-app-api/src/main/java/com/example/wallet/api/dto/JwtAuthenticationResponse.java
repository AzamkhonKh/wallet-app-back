package com.example.wallet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    // Optional: Add expiry times, token type ("Bearer") if needed by client
    // private String tokenType = "Bearer";
}
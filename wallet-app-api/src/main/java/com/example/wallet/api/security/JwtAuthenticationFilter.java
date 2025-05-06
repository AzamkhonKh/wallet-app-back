package com.example.wallet.api.security;

import com.example.wallet.api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Use Spring's interface
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Mark as a Spring component
@RequiredArgsConstructor // Inject dependencies
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Inject Spring's UserDetailsService

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Skip filter if no Authorization header or doesn't start with Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Extract token after "Bearer "

        try {
            username = jwtService.extractUsername(jwt);

            // If username extracted and no authentication in current security context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Validate token against user details
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, // Use UserDetails object as principal
                            null,        // Credentials aren't needed for JWT auth
                            userDetails.getAuthorities() // Set authorities
                    );
                    // Set details from the request
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Update SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("User '{}' authenticated successfully via JWT.", username);
                } else {
                     log.warn("Invalid JWT token for user '{}'.", username);
                }
            }
        } catch (Exception e) {
             log.error("Cannot set user authentication: {}", e.getMessage());
             // Example: Handle expired tokens, malformed tokens etc. specifically if needed
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
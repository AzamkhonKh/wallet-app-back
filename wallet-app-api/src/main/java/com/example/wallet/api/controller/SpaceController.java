package com.example.wallet.api.controller;

import com.example.wallet.api.dto.CreateSpaceRequest;
import com.example.wallet.api.dto.SpaceDto;
import com.example.wallet.api.dto.UpdateSpaceRequest;
import com.example.wallet.api.mapper.SpaceMapper;
import com.example.wallet.core.domain.Space;
import com.example.wallet.core.service.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.wallet.auth.domain.User; // Import your User class

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
@Slf4j
// TODO: Add @CrossOrigin if needed for frontend interactions from different
// origins
public class SpaceController {

    private final SpaceService spaceService;

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User)) {
            // This should ideally not happen for protected endpoints if security is set up
            // correctly
            log.error("Could not retrieve authenticated user details.");
            throw new IllegalStateException("User not authenticated properly"); // Or handle differently
        }
        User currentUser = (User) authentication.getPrincipal();
        return currentUser.getId();
    }

    @PostMapping
    public ResponseEntity<SpaceDto> createSpace(@Valid @RequestBody CreateSpaceRequest request) {
        UUID userId = getCurrentUserId();
        log.info("Received request to create space for user {}: {}", userId, request.getName());
        Space createdSpace = spaceService.createSpace(
                userId,
                request.getName(),
                request.getDescription(),
                request.getCurrency());
        return new ResponseEntity<>(SpaceMapper.toDto(createdSpace), HttpStatus.CREATED);
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceDto> getSpaceById(@PathVariable UUID spaceId) {
        UUID userId = getCurrentUserId();
        log.debug("Received request to get space {} for user {}", spaceId, userId);
        Space space = spaceService.getSpaceById(userId, spaceId);
        return ResponseEntity.ok(SpaceMapper.toDto(space));
    }

    @GetMapping
    public ResponseEntity<List<SpaceDto>> getAllSpacesForUser() {
        UUID userId = getCurrentUserId();
        log.debug("Received request to get all spaces for user {}", userId);
        List<Space> spaces = spaceService.getSpacesForUser(userId);
        return ResponseEntity.ok(SpaceMapper.toDtoList(spaces));
    }

    @PutMapping("/{spaceId}")
    public ResponseEntity<SpaceDto> updateSpace(@PathVariable UUID spaceId,
            @Valid @RequestBody UpdateSpaceRequest request) {
        UUID userId = getCurrentUserId();
        log.info("Received request to update space {} for user {}: {}", spaceId, userId, request.getName());
        Space updatedSpace = spaceService.updateSpace(
                userId,
                spaceId,
                request.getName(),
                request.getDescription());
        return ResponseEntity.ok(SpaceMapper.toDto(updatedSpace));
    }

    @DeleteMapping("/{spaceId}")
    public ResponseEntity<Void> deleteSpace(@PathVariable UUID spaceId) {
        UUID userId = getCurrentUserId();
        log.info("Received request to delete space {} for user {}", spaceId, userId);
        spaceService.deleteSpace(userId, spaceId);
        return ResponseEntity.noContent().build(); // Standard response for successful DELETE
    }
}
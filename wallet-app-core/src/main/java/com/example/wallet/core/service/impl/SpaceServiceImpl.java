package com.example.wallet.core.service.impl;

import com.example.wallet.common.exception.ResourceNotFoundException;
import com.example.wallet.core.domain.Space;
import com.example.wallet.core.repository.SpaceRepository;
import com.example.wallet.core.service.SpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Constructor injection via Lombok
@Slf4j // Logging facade
@Transactional // Default transaction demarcation for all public methods
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    // Inject TransactionRepository if needed for cascading deletes or validation

    @Override
    public Space createSpace(UUID userId, String name, String description, String currency) {
        log.info("Creating space for user {}: name={}", userId, name);
        Space space = Space.builder()
            .userId(userId)
            .name(name)
            .description(description)
            .currency(currency.toUpperCase()) // Ensure currency code consistency
            .build();
        Space savedSpace = spaceRepository.save(space);
        log.info("Space created successfully with id {}", savedSpace.getId());
        return savedSpace;
    }

    @Override
    @Transactional(readOnly = true) // Optimize for read operations
    public Space getSpaceById(UUID userId, UUID spaceId) {
        log.debug("Fetching space {} for user {}", spaceId, userId);
        return spaceRepository.findByIdAndUserId(spaceId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Space", "id", spaceId + " for user " + userId));
        // Note: The exception message could imply the space exists but belongs to another user.
        // Depending on security requirements, a more generic "Not Found" might be better.
    }

    @Override
    @Transactional(readOnly = true)
    public List<Space> getSpacesForUser(UUID userId) {
        log.debug("Fetching all spaces for user {}", userId);
        return spaceRepository.findByUserIdOrderByCreatedAtAsc(userId);
    }

    @Override
    public Space updateSpace(UUID userId, UUID spaceId, String name, String description) {
        log.info("Updating space {} for user {}", spaceId, userId);
        Space existingSpace = getSpaceById(userId, spaceId); // Reuse get method for existence and ownership check

        existingSpace.setName(name);
        existingSpace.setDescription(description);
        // Note: Currency is typically not updated. Add if needed.

        Space updatedSpace = spaceRepository.save(existingSpace); // JPA handles update due to attached entity state
        log.info("Space {} updated successfully", spaceId);
        return updatedSpace;
    }

    @Override
    public void deleteSpace(UUID userId, UUID spaceId) {
        log.info("Deleting space {} for user {}", spaceId, userId);
        Space spaceToDelete = getSpaceById(userId, spaceId); // Check existence and ownership

        // Consider implications: What happens to transactions in this space?
        // Option 1: Cascade delete (defined in DB or JPA relationship - potentially dangerous)
        // Option 2: Prevent deletion if transactions exist (check transactionRepository)
        // Option 3: Soft delete (add an 'isActive' flag to Space) - Recommended for financial apps
        // For now, we proceed with hard delete assuming cascade or manual cleanup later.
        spaceRepository.delete(spaceToDelete);
        log.info("Space {} deleted successfully", spaceId);
    }
}
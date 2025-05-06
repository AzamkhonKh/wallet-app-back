package com.example.wallet.core.repository;

import com.example.wallet.core.domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID> {

    // Find all spaces belonging to a specific user, ordered by creation time
    List<Space> findByUserIdOrderByCreatedAtAsc(UUID userId);

    // Find a specific space by its ID and the user ID (for authorization checks)
    Optional<Space> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID spaceId, UUID userId);
    
}
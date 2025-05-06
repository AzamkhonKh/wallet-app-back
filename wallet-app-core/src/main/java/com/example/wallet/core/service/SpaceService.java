package com.example.wallet.core.service;

import com.example.wallet.core.domain.Space;
import java.util.List;
import java.util.UUID;

public interface SpaceService {

    /** Creates a new space for the given user. */
    Space createSpace(UUID userId, String name, String description, String currency);

    /** Retrieves a specific space if it belongs to the user. */
    Space getSpaceById(UUID userId, UUID spaceId);

    /** Retrieves all spaces belonging to the user. */
    List<Space> getSpacesForUser(UUID userId);

    /** Updates an existing space if it belongs to the user. */
    Space updateSpace(UUID userId, UUID spaceId, String name, String description); // Only allow updating certain fields

    /** Deletes a space if it belongs to the user. */
    void deleteSpace(UUID userId, UUID spaceId);
}
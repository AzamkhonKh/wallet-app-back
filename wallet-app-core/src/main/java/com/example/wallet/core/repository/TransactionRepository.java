
package com.example.wallet.core.repository;

import com.example.wallet.core.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Find transactions for a specific space, ordered by date (most recent first)
    // Requires userId check separately in service layer
    List<Transaction> findBySpaceIdOrderByTransactionDateDescCreatedAtDesc(UUID spaceId);

    // Find a specific transaction by its ID and the user ID (for authorization)
    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    // Find transactions belonging to a user updated after a certain timestamp (for
    // sync)
    List<Transaction> findByUserIdAndUpdatedAtAfterOrderByUpdatedAtAsc(UUID userId, OffsetDateTime timestamp);

    // Check if a transaction exists for a given user and space (useful for auth
    // before fetching list)
    boolean existsByIdAndUserId(UUID id, UUID userId);

    // Example: Find transactions for a user within a specific space
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.spaceId = :spaceId ORDER BY t.transactionDate DESC, t.createdAt DESC")
    List<Transaction> findUserTransactionsInSpace(UUID userId, UUID spaceId);
}

package com.example.wallet.core.service.impl;

import com.example.wallet.common.exception.ResourceNotFoundException;
import com.example.wallet.core.domain.Transaction;
import com.example.wallet.core.domain.TransactionType;
import com.example.wallet.core.repository.SpaceRepository;
import com.example.wallet.core.repository.TransactionRepository;
import com.example.wallet.core.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final SpaceRepository spaceRepository; // Needed to verify space ownership

    @Override
    public Transaction createTransaction(UUID userId, UUID spaceId, TransactionType type, BigDecimal amount, LocalDate transactionDate, String description) {
        log.info("Creating transaction for user {} in space {}", userId, spaceId);

        // *** CRITICAL: Verify the user owns the space before creating a transaction in it ***
        if (!spaceRepository.existsByIdAndUserId(spaceId, userId)) {
             log.warn("User {} attempted to create transaction in unauthorized or non-existent space {}", userId, spaceId);
             throw new ResourceNotFoundException("Space", "id", spaceId + " accessible by user " + userId); // Or UnauthorizedAccess
        }

        Transaction transaction = Transaction.builder()
            .userId(userId) // Store userId directly on transaction
            .spaceId(spaceId)
            .type(type)
            .amount(amount)
            .transactionDate(transactionDate)
            .description(description)
            .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction {} created successfully in space {}", savedTransaction.getId(), spaceId);
        return savedTransaction;
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getTransactionById(UUID userId, UUID transactionId) {
        log.debug("Fetching transaction {} for user {}", transactionId, userId);
        return transactionRepository.findByIdAndUserId(transactionId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId + " for user " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsForSpace(UUID userId, UUID spaceId) {
        log.debug("Fetching transactions for space {} belonging to user {}", spaceId, userId);

        // Verify user owns the space before fetching transactions
         if (!spaceRepository.existsByIdAndUserId(spaceId, userId)) {
             log.warn("User {} attempted to access transactions from unauthorized or non-existent space {}", userId, spaceId);
             // Return empty list or throw exception based on desired behavior
             // Throwing aligns with trying to access a non-owned resource directly
             throw new ResourceNotFoundException("Space", "id", spaceId + " accessible by user " + userId);
         }

        // Fetch transactions using the optimized query if defined
        // return transactionRepository.findUserTransactionsInSpace(userId, spaceId);
        // Or using the simpler method (requires spaceId is correctly indexed)
        return transactionRepository.findBySpaceIdOrderByTransactionDateDescCreatedAtDesc(spaceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsForUserSince(UUID userId, OffsetDateTime since) {
        log.debug("Fetching transactions for user {} updated since {}", userId, since);
        if (since == null) {
            // Handle case where no timestamp is provided (e.g., fetch all or return error)
            // For sync, usually a timestamp is required. Let's return empty for null.
             log.warn("Attempted sync fetch for user {} with null timestamp", userId);
            return Collections.emptyList();
        }
        return transactionRepository.findByUserIdAndUpdatedAtAfterOrderByUpdatedAtAsc(userId, since);
    }

    @Override
    public Transaction updateTransaction(UUID userId, UUID transactionId, BigDecimal amount, LocalDate transactionDate, String description) {
        log.info("Updating transaction {} for user {}", transactionId, userId);
        Transaction existingTransaction = getTransactionById(userId, transactionId); // Checks ownership

        // Update allowed fields
        existingTransaction.setAmount(amount);
        existingTransaction.setTransactionDate(transactionDate);
        existingTransaction.setDescription(description);
        // Type and Space are generally not updatable, requires delete/create.

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        log.info("Transaction {} updated successfully", transactionId);
        return updatedTransaction;
    }

    @Override
    public void deleteTransaction(UUID userId, UUID transactionId) {
        log.info("Deleting transaction {} for user {}", transactionId, userId);
        Transaction transactionToDelete = getTransactionById(userId, transactionId); // Checks ownership

        transactionRepository.delete(transactionToDelete);
        log.info("Transaction {} deleted successfully", transactionId);
    }
}
package com.example.wallet.core.service;

import com.example.wallet.core.domain.Transaction;
import com.example.wallet.core.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    /** Creates a transaction within a specific space, ensuring the user owns the space. */
    Transaction createTransaction(UUID userId, UUID spaceId, TransactionType type, BigDecimal amount, LocalDate transactionDate, String description);

    /** Retrieves a specific transaction if it belongs to the user. */
    Transaction getTransactionById(UUID userId, UUID transactionId);

    /** Retrieves all transactions for a specific space, ensuring the user owns the space. */
    List<Transaction> getTransactionsForSpace(UUID userId, UUID spaceId);

    /** Retrieves transactions for a user updated after a given timestamp (for sync). */
    List<Transaction> getTransactionsForUserSince(UUID userId, OffsetDateTime since);

    /** Updates an existing transaction if it belongs to the user. */
    Transaction updateTransaction(UUID userId, UUID transactionId, BigDecimal amount, LocalDate transactionDate, String description);

    /** Deletes a transaction if it belongs to the user. */
    void deleteTransaction(UUID userId, UUID transactionId);
}
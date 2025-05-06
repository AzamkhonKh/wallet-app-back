package com.example.wallet.api.controller;

import com.example.wallet.api.dto.CreateTransactionRequest;
import com.example.wallet.api.dto.TransactionDto;
import com.example.wallet.api.dto.UpdateTransactionRequest;
import com.example.wallet.api.mapper.TransactionMapper;
import com.example.wallet.core.domain.Transaction;
import com.example.wallet.core.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.wallet.auth.domain.User; // Import your User class

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

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
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        UUID userId = getCurrentUserId();
        log.info("Received request to create transaction for user {} in space {}", userId, request.getSpaceId());
        Transaction createdTransaction = transactionService.createTransaction(
                userId,
                request.getSpaceId(),
                request.getType(),
                request.getAmount(),
                request.getTransactionDate(),
                request.getDescription());
        return new ResponseEntity<>(TransactionMapper.toDto(createdTransaction), HttpStatus.CREATED);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable UUID transactionId) {
        UUID userId = getCurrentUserId();
        log.debug("Received request to get transaction {} for user {}", transactionId, userId);
        Transaction transaction = transactionService.getTransactionById(userId, transactionId);
        return ResponseEntity.ok(TransactionMapper.toDto(transaction));
    }

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsForSpace(@PathVariable UUID spaceId) {
        UUID userId = getCurrentUserId();
        log.debug("Received request to get transactions for space {} for user {}", spaceId, userId);
        List<Transaction> transactions = transactionService.getTransactionsForSpace(userId, spaceId);
        return ResponseEntity.ok(TransactionMapper.toDtoList(transactions));
    }

    @GetMapping("/sync")
    public ResponseEntity<List<TransactionDto>> getTransactionsForSync(
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime since) {
        UUID userId = getCurrentUserId();
        log.debug("Received sync request for transactions for user {} since {}", userId, since);
        List<Transaction> transactions = transactionService.getTransactionsForUserSince(userId, since);
        return ResponseEntity.ok(TransactionMapper.toDtoList(transactions));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable UUID transactionId,
            @Valid @RequestBody UpdateTransactionRequest request) {
        UUID userId = getCurrentUserId();
        log.info("Received request to update transaction {} for user {}", transactionId, userId);
        Transaction updatedTransaction = transactionService.updateTransaction(
                userId,
                transactionId,
                request.getAmount(),
                request.getTransactionDate(),
                request.getDescription());
        return ResponseEntity.ok(TransactionMapper.toDto(updatedTransaction));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID transactionId) {
        UUID userId = getCurrentUserId();
        log.info("Received request to delete transaction {} for user {}", transactionId, userId);
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }
}
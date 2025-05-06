package com.example.wallet.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero; // Use PositiveOrZero for amounts
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transactions_space_id", columnList = "space_id"),
    @Index(name = "idx_transactions_user_id", columnList = "user_id"), // Index user_id
    @Index(name = "idx_transactions_updated_at", columnList = "updated_at") // Index for sync
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "space_id", nullable = false)
    private UUID spaceId;
    // If using JPA relationships:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "space_id", nullable = false)
    // private Space space;

    @NotNull
    @Column(name = "user_id", nullable = false) // Denormalized for easier sync/querying
    private UUID userId;

    @NotNull(message = "Transaction type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @NotNull(message = "Amount cannot be null")
    @PositiveOrZero(message = "Amount must be positive or zero")
    @Column(nullable = false, precision = 19, scale = 4) // Example precision/scale
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Transaction date cannot be null")
    @PastOrPresent(message = "Transaction date must be in the past or present")
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
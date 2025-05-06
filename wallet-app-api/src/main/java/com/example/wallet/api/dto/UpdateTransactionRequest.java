package com.example.wallet.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateTransactionRequest {
    @NotNull(message = "Amount cannot be null")
    @PositiveOrZero(message = "Amount must be positive or zero")
    private BigDecimal amount;

    @NotNull(message = "Transaction date cannot be null")
    @PastOrPresent(message = "Transaction date must be in the past or present")
    private LocalDate transactionDate;

    private String description; // Optional
}
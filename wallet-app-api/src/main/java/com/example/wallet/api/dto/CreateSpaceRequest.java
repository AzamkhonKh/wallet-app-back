package com.example.wallet.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSpaceRequest {
    @NotBlank(message = "Space name cannot be blank")
    @Size(max = 100, message = "Space name cannot exceed 100 characters")
    private String name;

    private String description; // Optional

    @NotBlank(message = "Currency code cannot be blank")
    @Size(min = 3, max = 3, message = "Currency code must be 3 characters (ISO 4217)")
    private String currency;
}
package com.example.wallet.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateSpaceRequest {
    @NotBlank(message = "Space name cannot be blank")
    @Size(max = 100, message = "Space name cannot exceed 100 characters")
    private String name;

    private String description; // Optional
}
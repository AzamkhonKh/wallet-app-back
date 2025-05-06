package com.example.wallet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data // Includes @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class SpaceDto {
    private UUID id;
    private String name;
    private String description;
    private String currency;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    // Exclude userId from DTOs exposed to the client
}
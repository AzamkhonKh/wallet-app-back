package com.example.wallet.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder; // Optional: if you want builder pattern
import lombok.AllArgsConstructor; // Optional: if you want all args constructor

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "spaces", indexes = {
    @Index(name = "idx_spaces_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Example: Added for convenience
@Builder // Example: Added for convenience
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Use UUID strategy
    private UUID id;

    @NotNull // Ensure user ID is always present
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank(message = "Space name cannot be blank")
    @Size(max = 100, message = "Space name cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Currency code cannot be blank")
    @Size(min = 3, max = 3, message = "Currency code must be 3 characters")
    @Column(nullable = false, length = 3)
    private String currency; // ISO 4217

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Consider adding relationship to Transactions if needed (e.g., @OneToMany)
    // @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Transaction> transactions = new ArrayList<>();
}
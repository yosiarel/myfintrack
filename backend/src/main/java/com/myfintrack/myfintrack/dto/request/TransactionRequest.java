package com.myfintrack.myfintrack.dto.request;

import com.myfintrack.myfintrack.entity.Category;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    // Optional: will be auto-detected from category if not provided
    private Category.TransactionType type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Amount must have max 13 digits and 2 decimal places")
    private BigDecimal amount;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date cannot be in the future")
    private LocalDate transactionDate;
}

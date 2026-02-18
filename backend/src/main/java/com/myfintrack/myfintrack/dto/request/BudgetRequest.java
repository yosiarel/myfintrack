package com.myfintrack.myfintrack.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Monthly limit is required")
    @DecimalMin(value = "0.01", message = "Monthly limit must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Monthly limit must have max 13 digits and 2 decimal places")
    private BigDecimal monthlyLimit;

    private LocalDate startDate; // Optional: specific date within the budget month

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Max(value = 2100, message = "Year must be before 2100")
    private Integer year;
}

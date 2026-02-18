package com.myfintrack.myfintrack.dto.response;

import com.myfintrack.myfintrack.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private Category.TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.myfintrack.myfintrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private BigDecimal monthlyLimit;
    private BigDecimal spent;
    private BigDecimal remaining;
    private Double usagePercentage;
    private String status;
    private LocalDate startDate;
    private Integer month;
    private Integer year;
}

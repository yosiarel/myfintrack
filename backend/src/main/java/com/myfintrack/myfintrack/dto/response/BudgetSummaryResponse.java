package com.myfintrack.myfintrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalAllocated;
    private BigDecimal totalUnbudgetedSpent;
    private BigDecimal totalSpent;
    private BigDecimal netBalance;
    private int month;
    private int year;
}

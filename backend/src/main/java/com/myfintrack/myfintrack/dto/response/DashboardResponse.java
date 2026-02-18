package com.myfintrack.myfintrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal currentBalance;
    private BigDecimal netSavings;
    private List<CategoryExpense> expenseByCategory;
    private List<BudgetResponse> budgetProgress;
    private List<TransactionResponse> recentTransactions;
    private List<MonthlyTrend> monthlyTrend;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyTrend {
        private Integer year;
        private Integer month;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal netSavings;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryExpense {
        private Long categoryId;
        private String categoryName;
        private String categoryColor;
        private BigDecimal total;
        private Integer transactionCount;
        private Double percentage;
    }
}

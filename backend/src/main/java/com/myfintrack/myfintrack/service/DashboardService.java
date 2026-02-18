package com.myfintrack.myfintrack.service;

import com.myfintrack.myfintrack.dto.response.BudgetResponse;
import com.myfintrack.myfintrack.dto.response.DashboardResponse;
import com.myfintrack.myfintrack.dto.response.TransactionResponse;
import com.myfintrack.myfintrack.entity.Category;
import com.myfintrack.myfintrack.entity.Transaction;
import com.myfintrack.myfintrack.entity.User;
import com.myfintrack.myfintrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final TransactionRepository transactionRepository;
        private final BudgetService budgetService;

        public DashboardResponse getDashboardSummary(User user, Integer month, Integer year) {
                // Get monthly totals
                BigDecimal totalIncome = transactionRepository.getTotalByTypeAndMonth(
                                user.getId(), Category.TransactionType.INCOME, month, year);
                BigDecimal totalExpense = transactionRepository.getTotalByTypeAndMonth(
                                user.getId(), Category.TransactionType.EXPENSE, month, year);

                if (totalIncome == null)
                        totalIncome = BigDecimal.ZERO;
                if (totalExpense == null)
                        totalExpense = BigDecimal.ZERO;

                // Get current balance (all time)
                BigDecimal currentBalance = transactionRepository.getCurrentBalance(user.getId());
                if (currentBalance == null)
                        currentBalance = BigDecimal.ZERO;

                // Calculate net savings for the month
                BigDecimal netSavings = totalIncome.subtract(totalExpense);

                // Get expense by category
                List<Map<String, Object>> expenseData = transactionRepository.getExpenseByCategory(
                                user.getId(), month, year);

                BigDecimal totalExpenseForPercentage = totalExpense;
                List<DashboardResponse.CategoryExpense> expenseByCategory = expenseData.stream()
                                .map(data -> {
                                        BigDecimal total = (BigDecimal) data.get("total");
                                        double percentage = 0.0;
                                        if (totalExpenseForPercentage.compareTo(BigDecimal.ZERO) > 0) {
                                                percentage = total
                                                                .divide(totalExpenseForPercentage, 4,
                                                                                RoundingMode.HALF_UP)
                                                                .multiply(BigDecimal.valueOf(100))
                                                                .doubleValue();
                                        }

                                        return DashboardResponse.CategoryExpense.builder()
                                                        .categoryId(((Number) data.get("categoryId")).longValue())
                                                        .categoryName((String) data.get("categoryName"))
                                                        .categoryColor((String) data.get("categoryColor"))
                                                        .total(total)
                                                        .transactionCount(((Number) data.get("count")).intValue())
                                                        .percentage(percentage)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // Get budget progress
                List<BudgetResponse> budgetProgress = budgetService.getBudgets(user, month, year);

                // Get recent transactions (last 10)
                List<Transaction> recentTransactionsList = transactionRepository
                                .findByUserIdAndMonthAndYear(user.getId(), month, year)
                                .stream()
                                .limit(10)
                                .collect(Collectors.toList());

                List<TransactionResponse> recentTransactions = recentTransactionsList.stream()
                                .map(t -> TransactionResponse.builder()
                                                .id(t.getId())
                                                .categoryId(t.getCategory().getId())
                                                .categoryName(t.getCategory().getName())
                                                .categoryColor(t.getCategory().getColor())
                                                .type(t.getType())
                                                .amount(t.getAmount())
                                                .description(t.getDescription())
                                                .transactionDate(t.getTransactionDate())
                                                .createdAt(t.getCreatedAt())
                                                .updatedAt(t.getUpdatedAt())
                                                .build())
                                .collect(Collectors.toList());

                // Get monthly trend (Last 6 months ending at selected month/year)
                LocalDate endDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
                LocalDate startDate = endDate.minusMonths(5).withDayOfMonth(1);

                List<Map<String, Object>> trendData = transactionRepository.getMonthlyTrend(
                                user.getId(), startDate, endDate);

                // Initialize map for quick lookup
                Map<String, DashboardResponse.MonthlyTrend> trendMap = trendData.stream()
                                .collect(Collectors.toMap(
                                                data -> (int) data.get("year") + "-" + (int) data.get("month"),
                                                data -> DashboardResponse.MonthlyTrend.builder()
                                                                .year((Integer) data.get("year"))
                                                                .month((Integer) data.get("month"))
                                                                .income((BigDecimal) data.get("income"))
                                                                .expense((BigDecimal) data.get("expense"))
                                                                .netSavings(((BigDecimal) data.get("income"))
                                                                                .subtract((BigDecimal) data
                                                                                                .get("expense")))
                                                                .build()));

                // Fill gaps ensuring 6 months span
                List<DashboardResponse.MonthlyTrend> monthlyTrend = IntStream.range(0, 6)
                                .mapToObj(i -> startDate.plusMonths(i))
                                .map(date -> {
                                        String key = date.getYear() + "-" + date.getMonthValue();
                                        return trendMap.getOrDefault(key, DashboardResponse.MonthlyTrend.builder()
                                                        .year(date.getYear())
                                                        .month(date.getMonthValue())
                                                        .income(BigDecimal.ZERO)
                                                        .expense(BigDecimal.ZERO)
                                                        .netSavings(BigDecimal.ZERO)
                                                        .build());
                                })
                                .collect(Collectors.toList());

                return DashboardResponse.builder()
                                .totalIncome(totalIncome)
                                .totalExpense(totalExpense)
                                .currentBalance(currentBalance)
                                .netSavings(netSavings)
                                .expenseByCategory(expenseByCategory)
                                .budgetProgress(budgetProgress)
                                .recentTransactions(recentTransactions)
                                .monthlyTrend(monthlyTrend)
                                .build();
        }
}

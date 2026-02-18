package com.myfintrack.myfintrack.service;

import com.myfintrack.myfintrack.dto.request.BudgetRequest;
import com.myfintrack.myfintrack.dto.response.BudgetResponse;
import com.myfintrack.myfintrack.dto.response.BudgetSummaryResponse;
import com.myfintrack.myfintrack.entity.Budget;
import com.myfintrack.myfintrack.entity.Category;
import com.myfintrack.myfintrack.entity.User;
import com.myfintrack.myfintrack.exception.BadRequestException;
import com.myfintrack.myfintrack.exception.ResourceNotFoundException;
import com.myfintrack.myfintrack.repository.BudgetRepository;
import com.myfintrack.myfintrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    @Transactional
    public BudgetResponse createBudget(User user, BudgetRequest request) {
        Category category = categoryService.findById(request.getCategoryId());

        // Only allow EXPENSE categories for budgets
        if (!category.getType().equals(Category.TransactionType.EXPENSE)) {
            throw new BadRequestException("Budgets can only be set for expense categories");
        }

        // Check if budget already exists for this category and period
        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                user.getId(), request.getCategoryId(), request.getMonth(), request.getYear())) {
            throw new BadRequestException("Budget already exists for this category and period");
        }

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .monthlyLimit(request.getMonthlyLimit())
                .startDate(request.getStartDate())
                .month(request.getMonth())
                .year(request.getYear())
                .build();

        budget = budgetRepository.save(budget);
        return mapToResponse(budget, user.getId());
    }

    public List<BudgetResponse> getBudgets(User user, Integer month, Integer year) {
        List<Budget> budgets;

        if (month != null && year != null) {
            budgets = budgetRepository.findByUserIdAndMonthAndYear(user.getId(), month, year);
        } else {
            budgets = budgetRepository.findByUserIdOrderByMonthDescYearDesc(user.getId());
        }

        return budgets.stream()
                .map(budget -> mapToResponse(budget, user.getId()))
                .collect(Collectors.toList());
    }

    public BudgetSummaryResponse getEnvelopeSummary(User user, int month, int year) {
        // 1. Total Income
        BigDecimal totalIncome = transactionRepository.getTotalByTypeAndMonth(
                user.getId(), Category.TransactionType.INCOME, month, year);
        if (totalIncome == null)
            totalIncome = BigDecimal.ZERO;

        // 2. Get Budgets for the period
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(user.getId(), month, year);

        // 3. Total Allocated (Sum of Budget Limits)
        BigDecimal totalAllocated = budgets.stream()
                .map(Budget::getMonthlyLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Unbudgeted Spent
        List<Long> budgetedCategoryIds = budgets.stream()
                .map(b -> b.getCategory().getId())
                .collect(Collectors.toList());

        BigDecimal totalUnbudgetedSpent;
        if (budgetedCategoryIds.isEmpty()) {
            // If no budgets, ALL expenses are unbudgeted
            totalUnbudgetedSpent = transactionRepository.getTotalByTypeAndMonth(
                    user.getId(), Category.TransactionType.EXPENSE, month, year);
        } else {
            // Sum expenses where category is NOT in budgeted list
            totalUnbudgetedSpent = transactionRepository.getTotalByTypeAndMonthAndCategoryNotIn(
                    user.getId(), Category.TransactionType.EXPENSE, month, year, budgetedCategoryIds);
        }
        if (totalUnbudgetedSpent == null)
            totalUnbudgetedSpent = BigDecimal.ZERO;

        // 5. Total Spent (Allocated + Unbudgeted)
        BigDecimal totalSpent = totalAllocated.add(totalUnbudgetedSpent);

        // 6. Net Balance
        BigDecimal netBalance = totalIncome.subtract(totalSpent);

        return BudgetSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalAllocated(totalAllocated)
                .totalUnbudgetedSpent(totalUnbudgetedSpent)
                .totalSpent(totalSpent)
                .netBalance(netBalance)
                .month(month)
                .year(year)
                .build();
    }

    public BudgetResponse getBudgetById(User user, Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        // Ensure user owns this budget
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to access this budget");
        }

        return mapToResponse(budget, user.getId());
    }

    @Transactional
    public BudgetResponse updateBudget(User user, Long id, BudgetRequest request) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        // Ensure user owns this budget
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to update this budget");
        }

        Category category = categoryService.findById(request.getCategoryId());

        // Only allow EXPENSE categories for budgets
        if (!category.getType().equals(Category.TransactionType.EXPENSE)) {
            throw new BadRequestException("Budgets can only be set for expense categories");
        }

        budget.setCategory(category);
        budget.setMonthlyLimit(request.getMonthlyLimit());
        budget.setStartDate(request.getStartDate());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        budget = budgetRepository.save(budget);
        return mapToResponse(budget, user.getId());
    }

    @Transactional
    public void deleteBudget(User user, Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        // Ensure user owns this budget
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to delete this budget");
        }

        budgetRepository.delete(budget);
    }

    private BudgetResponse mapToResponse(Budget budget, Long userId) {
        // Calculate spent amount for this budget period
        BigDecimal spent = transactionRepository.getTotalByTypeAndMonthAndCategoryId(
                userId,
                Category.TransactionType.EXPENSE,
                budget.getMonth(),
                budget.getYear(),
                budget.getCategory().getId());

        if (spent == null)
            spent = BigDecimal.ZERO;

        BigDecimal remaining = budget.getMonthlyLimit().subtract(spent);

        double usagePercentage = 0.0;
        if (budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0) {
            usagePercentage = spent.divide(budget.getMonthlyLimit(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        String status;
        if (spent.compareTo(budget.getMonthlyLimit()) > 0) {
            status = "OVER_BUDGET";
        } else if (spent.compareTo(budget.getMonthlyLimit().multiply(BigDecimal.valueOf(0.8))) >= 0) {
            status = "WARNING";
        } else {
            status = "SAFE";
        }

        return BudgetResponse.builder()
                .id(budget.getId())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .categoryColor(budget.getCategory().getColor())
                .monthlyLimit(budget.getMonthlyLimit())
                .spent(spent)
                .remaining(remaining)
                .usagePercentage(usagePercentage)
                .status(status)
                .startDate(budget.getStartDate())
                .month(budget.getMonth())
                .year(budget.getYear())
                .build();
    }
}

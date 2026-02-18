package com.myfintrack.myfintrack.controller;

import com.myfintrack.myfintrack.dto.request.BudgetRequest;
import com.myfintrack.myfintrack.dto.response.ApiResponse;
import com.myfintrack.myfintrack.dto.response.BudgetResponse;
import com.myfintrack.myfintrack.dto.response.BudgetSummaryResponse;
import com.myfintrack.myfintrack.entity.User;
import com.myfintrack.myfintrack.service.BudgetService;
import com.myfintrack.myfintrack.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        BudgetResponse response = budgetService.createBudget(user, request);
        return ResponseEntity.ok(ApiResponse.success("Budget created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgets(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        List<BudgetResponse> budgets = budgetService.getBudgets(user, month, year);
        return ResponseEntity.ok(ApiResponse.success(budgets));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<BudgetSummaryResponse>> getEnvelopeSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        if (month == null)
            month = java.time.LocalDate.now().getMonthValue();
        if (year == null)
            year = java.time.LocalDate.now().getYear();

        BudgetSummaryResponse response = budgetService.getEnvelopeSummary(user, month, year);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetById(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        BudgetResponse response = budgetService.getBudgetById(user, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        BudgetResponse response = budgetService.updateBudget(user, id, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        budgetService.deleteBudget(user, id);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully", null));
    }
}

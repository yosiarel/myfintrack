package com.myfintrack.myfintrack.controller;

import com.myfintrack.myfintrack.dto.request.TransactionRequest;
import com.myfintrack.myfintrack.dto.response.ApiResponse;
import com.myfintrack.myfintrack.dto.response.TransactionResponse;
import com.myfintrack.myfintrack.entity.Category;
import com.myfintrack.myfintrack.entity.User;
import com.myfintrack.myfintrack.service.TransactionService;
import com.myfintrack.myfintrack.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        TransactionResponse response = transactionService.createTransaction(user, request);
        return ResponseEntity.ok(ApiResponse.success("Transaction created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Category.TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<TransactionResponse> transactions = transactionService.getTransactions(
                user, type, categoryId, startDate, endDate, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        TransactionResponse response = transactionService.getTransactionById(user, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        TransactionResponse response = transactionService.updateTransaction(user, id, request);
        return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        transactionService.deleteTransaction(user, id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> deleteTransactionPermanently(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        transactionService.deleteTransactionPermanently(user, id);
        return ResponseEntity.ok(ApiResponse.success("Transaction permanently deleted", null));
    }

    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getDeletedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<TransactionResponse> transactions = transactionService.getDeletedTransactions(user, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<TransactionResponse>> restoreTransaction(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        TransactionResponse response = transactionService.restoreTransaction(user, id);
        return ResponseEntity.ok(ApiResponse.success("Transaction restored successfully", response));
    }
}

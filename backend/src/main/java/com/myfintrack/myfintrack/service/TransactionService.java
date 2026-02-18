package com.myfintrack.myfintrack.service;

import com.myfintrack.myfintrack.dto.request.TransactionRequest;
import com.myfintrack.myfintrack.dto.response.TransactionResponse;
import com.myfintrack.myfintrack.entity.Category;
import com.myfintrack.myfintrack.entity.Transaction;
import com.myfintrack.myfintrack.entity.User;
import com.myfintrack.myfintrack.exception.BadRequestException;
import com.myfintrack.myfintrack.exception.ResourceNotFoundException;
import com.myfintrack.myfintrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    @Transactional
    public TransactionResponse createTransaction(User user, TransactionRequest request) {
        Category category = categoryService.findById(request.getCategoryId());

        Category.TransactionType transactionType = request.getType();
        if (transactionType == null) {
            transactionType = category.getType();
        } else {
            if (!category.getType().equals(transactionType)) {
                throw new BadRequestException("Category type does not match transaction type");
            }
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .type(transactionType)
                .amount(request.getAmount())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .build();

        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    public Page<TransactionResponse> getTransactions(
            User user,
            Category.TransactionType type,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        Page<Transaction> transactions;

        if (startDate != null && endDate != null) {
            if (type != null) {
                transactions = transactionRepository
                        .findByUserIdAndTypeAndTransactionDateBetweenOrderByTransactionDateDesc(
                                user.getId(), type, startDate, endDate, pageable);
            } else {
                transactions = transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                        user.getId(), startDate, endDate, pageable);
            }
        } else if (type != null) {
            transactions = transactionRepository.findByUserIdAndTypeOrderByTransactionDateDesc(
                    user.getId(), type, pageable);
        } else if (categoryId != null) {
            transactions = transactionRepository.findByUserIdAndCategoryIdOrderByTransactionDateDesc(
                    user.getId(), categoryId, pageable);
        } else {
            transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(
                    user.getId(), pageable);
        }

        return transactions.map(this::mapToResponse);
    }

    public TransactionResponse getTransactionById(User user, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to access this transaction");
        }

        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(User user, Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to update this transaction");
        }

        Category category = categoryService.findById(request.getCategoryId());

        Category.TransactionType transactionType = request.getType();
        if (transactionType == null) {
            transactionType = category.getType();
        } else {
            if (!category.getType().equals(transactionType)) {
                throw new BadRequestException("Category type does not match transaction type");
            }
        }

        transaction.setCategory(category);
        transaction.setType(transactionType);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());

        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(User user, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        // Ensure user owns this transaction
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to delete this transaction");
        }

        // Soft delete: set deletedAt timestamp instead of removing from DB
        transaction.setDeletedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransactionPermanently(User user, Long id) {
        Transaction transaction = transactionRepository.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to delete this transaction");
        }

        transactionRepository.delete(transaction);
    }

    @Transactional
    public TransactionResponse restoreTransaction(User user, Long id) {
        // Use native query to find including soft-deleted records
        Transaction transaction = transactionRepository.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to restore this transaction");
        }

        if (transaction.getDeletedAt() == null) {
            throw new BadRequestException("Transaction is not deleted");
        }

        transaction.setDeletedAt(null);
        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    public Page<TransactionResponse> getDeletedTransactions(User user, Pageable pageable) {
        return transactionRepository.findDeletedByUserId(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .categoryColor(transaction.getCategory().getColor())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}

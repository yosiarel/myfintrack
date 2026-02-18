package com.myfintrack.myfintrack.repository;

import com.myfintrack.myfintrack.entity.Category;
import com.myfintrack.myfintrack.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        // Paginated queries
        Page<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId, Pageable pageable);

        Page<Transaction> findByUserIdAndTypeOrderByTransactionDateDesc(
                        Long userId,
                        Category.TransactionType type,
                        Pageable pageable);

        Page<Transaction> findByUserIdAndCategoryIdOrderByTransactionDateDesc(
                        Long userId,
                        Long categoryId,
                        Pageable pageable);

        Page<Transaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                        Long userId,
                        LocalDate startDate,
                        LocalDate endDate,
                        Pageable pageable);

        Page<Transaction> findByUserIdAndTypeAndTransactionDateBetweenOrderByTransactionDateDesc(
                        Long userId,
                        Category.TransactionType type,
                        LocalDate startDate,
                        LocalDate endDate,
                        Pageable pageable);

        // Custom queries for dashboard
        @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
                        "AND YEAR(t.transactionDate) = :year " +
                        "AND MONTH(t.transactionDate) = :month " +
                        "ORDER BY t.transactionDate DESC")
        List<Transaction> findByUserIdAndMonthAndYear(
                        @Param("userId") Long userId,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT t.type as type, SUM(t.amount) as total, COUNT(t) as count " +
                        "FROM Transaction t " +
                        "WHERE t.user.id = :userId " +
                        "AND YEAR(t.transactionDate) = :year " +
                        "AND MONTH(t.transactionDate) = :month " +
                        "GROUP BY t.type")
        List<Map<String, Object>> getMonthlySummary(
                        @Param("userId") Long userId,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT c.id as categoryId, c.name as categoryName, c.color as categoryColor, " +
                        "SUM(t.amount) as total, COUNT(t) as count " +
                        "FROM Transaction t " +
                        "JOIN t.category c " +
                        "WHERE t.user.id = :userId " +
                        "AND t.type = 'EXPENSE' " +
                        "AND YEAR(t.transactionDate) = :year " +
                        "AND MONTH(t.transactionDate) = :month " +
                        "GROUP BY c.id, c.name, c.color " +
                        "ORDER BY total DESC")
        List<Map<String, Object>> getExpenseByCategory(
                        @Param("userId") Long userId,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT SUM(t.amount) FROM Transaction t " +
                        "WHERE t.user.id = :userId " +
                        "AND t.type = :type " +
                        "AND YEAR(t.transactionDate) = :year " +
                        "AND MONTH(t.transactionDate) = :month")
        BigDecimal getTotalByTypeAndMonth(
                        @Param("userId") Long userId,
                        @Param("type") Category.TransactionType type,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT SUM(t.amount) FROM Transaction t " +
                        "WHERE t.user.id = :userId " +
                        "AND t.type = :type " +
                        "AND YEAR(t.transactionDate) = :year " +
                        "AND MONTH(t.transactionDate) = :month " +
                        "AND t.category.id = :categoryId")
        BigDecimal getTotalByTypeAndMonthAndCategoryId(
                        @Param("userId") Long userId,
                        @Param("type") Category.TransactionType type,
                        @Param("month") int month,
                        @Param("year") int year,
                        @Param("categoryId") Long categoryId);

        @Query("SELECT SUM(t.amount) FROM Transaction t " +
                        "WHERE t.user.id = :userId " +
                        "AND t.type = :type " +
                        "AND YEAR(t.transactionDate) = :year " +
                        "AND MONTH(t.transactionDate) = :month " +
                        "AND t.category.id NOT IN :excludedCategoryIds")
        BigDecimal getTotalByTypeAndMonthAndCategoryNotIn(
                        @Param("userId") Long userId,
                        @Param("type") Category.TransactionType type,
                        @Param("month") int month,
                        @Param("year") int year,
                        @Param("excludedCategoryIds") List<Long> excludedCategoryIds);

        @Query("SELECT SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END) " +
                        "FROM Transaction t WHERE t.user.id = :userId")
        BigDecimal getCurrentBalance(@Param("userId") Long userId);

        // ── Soft-delete helpers ────────────────────────────────────────────────────

        /**
         * Find a transaction by ID regardless of soft-delete status.
         * Bypasses the @SQLRestriction filter via a native query.
         */
        @Query(value = "SELECT * FROM transactions WHERE id = :id", nativeQuery = true)
        Optional<Transaction> findByIdIncludeDeleted(@Param("id") Long id);

        /**
         * List soft-deleted transactions for a user, newest first.
         */
        @Query(value = "SELECT * FROM transactions WHERE user_id = :userId AND deleted_at IS NOT NULL ORDER BY deleted_at DESC", countQuery = "SELECT COUNT(*) FROM transactions WHERE user_id = :userId AND deleted_at IS NOT NULL", nativeQuery = true)
        Page<Transaction> findDeletedByUserId(@Param("userId") Long userId, Pageable pageable);

        /**
         * Get monthly income and expense for a date range.
         * Returns a list of maps with keys: year, month, income, expense.
         */
        @Query("SELECT new map(YEAR(t.transactionDate) as year, MONTH(t.transactionDate) as month, " +
                        "SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as income, " +
                        "SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as expense) " +
                        "FROM Transaction t " +
                        "WHERE t.user.id = :userId " +
                        "AND t.transactionDate BETWEEN :startDate AND :endDate " +
                        "GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate) " +
                        "ORDER BY YEAR(t.transactionDate), MONTH(t.transactionDate)")
        List<Map<String, Object>> getMonthlyTrend(
                        @Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}

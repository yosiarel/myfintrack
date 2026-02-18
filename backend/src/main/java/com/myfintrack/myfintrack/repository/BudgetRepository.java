package com.myfintrack.myfintrack.repository;

import com.myfintrack.myfintrack.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdOrderByMonthDescYearDesc(Long userId);

    List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            Long userId,
            Long categoryId,
            Integer month,
            Integer year);

    boolean existsByUserIdAndCategoryIdAndMonthAndYear(
            Long userId,
            Long categoryId,
            Integer month,
            Integer year);

    @Query("SELECT b FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.year = :year " +
            "AND b.month = :month " +
            "ORDER BY b.monthlyLimit DESC")
    List<Budget> findBudgetsByUserAndPeriod(
            @Param("userId") Long userId,
            @Param("month") Integer month,
            @Param("year") Integer year);
}

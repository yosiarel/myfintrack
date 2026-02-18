package com.myfintrack.myfintrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets", uniqueConstraints = @UniqueConstraint(name = "budgets_unique_constraint", columnNames = {
                "user_id", "category_id", "month", "year" }), indexes = {
                                @Index(name = "idx_budgets_user_id", columnList = "user_id"),
                                @Index(name = "idx_budgets_user_period", columnList = "user_id, year, month")
                })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_id", nullable = false)
        private Category category;

        @Column(name = "monthly_limit", nullable = false, precision = 15, scale = 2)
        private BigDecimal monthlyLimit;

        @Column(name = "start_date")
        private LocalDate startDate;

        @Column(nullable = false)
        private Integer month;

        @Column(nullable = false)
        private Integer year;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;
}

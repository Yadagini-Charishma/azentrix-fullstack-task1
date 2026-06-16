package com.azentrix.budgettracker.repository;

import com.azentrix.budgettracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // All entries for a specific user
    List<Expense> findByUserId(Long userId);

    // Entries for a user within a date range
    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
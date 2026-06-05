package com.azentrix.budgettracker.repository;

import com.azentrix.budgettracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Find all entries between two dates (for monthly filter)
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Find by type (INCOME or EXPENSE)
    List<Expense> findByType(String type);
}
package com.azentrix.budgettracker.service;

import com.azentrix.budgettracker.entity.Expense;

import java.util.List;
import java.util.Map;

public interface ExpenseService {

    Expense addExpense(Expense expense, Long userId);

    Expense updateExpense(Long id, Expense expense, Long userId);

    void deleteExpense(Long id, Long userId);

    List<Expense> getAllExpenses(Long userId);

    Expense getExpenseById(Long id, Long userId);

    List<Expense> getExpensesByMonth(int year, int month, Long userId);

    Map<String, Object> getMonthlySummary(int year, int month, Long userId);
}
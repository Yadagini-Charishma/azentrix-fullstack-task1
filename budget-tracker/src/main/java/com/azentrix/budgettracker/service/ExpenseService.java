package com.azentrix.budgettracker.service;

import com.azentrix.budgettracker.entity.Expense;

import java.util.List;
import java.util.Map;

public interface ExpenseService {

    Expense addExpense(Expense expense);

    Expense updateExpense(Long id, Expense expense);

    void deleteExpense(Long id);

    List<Expense> getAllExpenses();

    Expense getExpenseById(Long id);

    List<Expense> getExpensesByMonth(int year, int month);

    Map<String, Object> getMonthlySummary(int year, int month);
}
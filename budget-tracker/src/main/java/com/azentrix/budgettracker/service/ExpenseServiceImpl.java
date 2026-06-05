package com.azentrix.budgettracker.service;

import com.azentrix.budgettracker.entity.Expense;
import com.azentrix.budgettracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Override
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public Expense updateExpense(Long id, Expense updatedExpense) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        existing.setTitle(updatedExpense.getTitle());
        existing.setAmount(updatedExpense.getAmount());
        existing.setType(updatedExpense.getType());
        existing.setCategory(updatedExpense.getCategory());
        existing.setDate(updatedExpense.getDate());
        existing.setNote(updatedExpense.getNote());

        return expenseRepository.save(existing);
    }

    @Override
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    @Override
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
    }

    @Override
    public List<Expense> getExpensesByMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return expenseRepository.findByDateBetween(start, end);
    }

    @Override
    public Map<String, Object> getMonthlySummary(int year, int month) {
        List<Expense> entries = getExpensesByMonth(year, month);

        double totalIncome = entries.stream()
                .filter(e -> "INCOME".equalsIgnoreCase(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        double totalExpense = entries.stream()
                .filter(e -> "EXPENSE".equalsIgnoreCase(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        // Group expenses by category for chart
        Map<String, Double> categoryBreakdown = entries.stream()
                .filter(e -> "EXPENSE".equalsIgnoreCase(e.getType()))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("year", year);
        summary.put("month", month);
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("balance", totalIncome - totalExpense);
        summary.put("categoryBreakdown", categoryBreakdown);
        summary.put("entries", entries);

        return summary;
    }
}
package com.azentrix.budgettracker.service;

import com.azentrix.budgettracker.entity.Expense;
import com.azentrix.budgettracker.entity.User;
import com.azentrix.budgettracker.repository.ExpenseRepository;
import com.azentrix.budgettracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Expense addExpense(Expense expense, Long userId) {
        expense.setUser(getUser(userId));
        return expenseRepository.save(expense);
    }

    @Override
    public Expense updateExpense(Long id, Expense updated, Long userId) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        // Make sure this expense belongs to the logged-in user
        if (!existing.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied!");
        }

        existing.setTitle(updated.getTitle());
        existing.setAmount(updated.getAmount());
        existing.setType(updated.getType());
        existing.setCategory(updated.getCategory());
        existing.setDate(updated.getDate());
        existing.setNote(updated.getNote());
        return expenseRepository.save(existing);
    }

    @Override
    public void deleteExpense(Long id, Long userId) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied!");
        }
        expenseRepository.deleteById(id);
    }

    @Override
    public List<Expense> getAllExpenses(Long userId) {
        return expenseRepository.findByUserId(userId);
    }

    @Override
    public Expense getExpenseById(Long id, Long userId) {
        Expense e = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));
        if (!e.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied!");
        }
        return e;
    }

    @Override
    public List<Expense> getExpensesByMonth(int year, int month, Long userId) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end   = start.withDayOfMonth(start.lengthOfMonth());
        return expenseRepository.findByUserIdAndDateBetween(userId, start, end);
    }

    @Override
    public Map<String, Object> getMonthlySummary(int year, int month, Long userId) {
        List<Expense> entries = getExpensesByMonth(year, month, userId);

        double totalIncome = entries.stream()
                .filter(e -> "INCOME".equalsIgnoreCase(e.getType()))
                .mapToDouble(Expense::getAmount).sum();

        double totalExpense = entries.stream()
                .filter(e -> "EXPENSE".equalsIgnoreCase(e.getType()))
                .mapToDouble(Expense::getAmount).sum();

        Map<String, Double> categoryBreakdown = entries.stream()
                .filter(e -> "EXPENSE".equalsIgnoreCase(e.getType()))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));

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
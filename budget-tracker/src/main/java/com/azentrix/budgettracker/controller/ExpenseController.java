package com.azentrix.budgettracker.controller;

import com.azentrix.budgettracker.entity.Expense;
import com.azentrix.budgettracker.service.ExpenseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // ── Helper: get logged-in userId from session ─────────
    private Long getSessionUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) throw new RuntimeException("Not logged in");
        return userId;
    }

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {
        try {
            return ResponseEntity.ok(expenseService.getAllExpenses(getSessionUserId(session)));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, HttpSession session) {
        try {
            return ResponseEntity.ok(expenseService.getExpenseById(id, getSessionUserId(session)));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Expense expense, HttpSession session) {
        try {
            return ResponseEntity.ok(expenseService.addExpense(expense, getSessionUserId(session)));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Expense expense, HttpSession session) {
        try {
            return ResponseEntity.ok(expenseService.updateExpense(id, expense, getSessionUserId(session)));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        try {
            expenseService.deleteExpense(id, getSessionUserId(session));
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary(@RequestParam int year, @RequestParam int month, HttpSession session) {
        try {
            return ResponseEntity.ok(expenseService.getMonthlySummary(year, month, getSessionUserId(session)));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/month")
    public ResponseEntity<?> byMonth(@RequestParam int year, @RequestParam int month, HttpSession session) {
        try {
            return ResponseEntity.ok(expenseService.getExpensesByMonth(year, month, getSessionUserId(session)));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}
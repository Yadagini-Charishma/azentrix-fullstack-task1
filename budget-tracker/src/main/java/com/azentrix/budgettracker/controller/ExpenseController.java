package com.azentrix.budgettracker.controller;

import com.azentrix.budgettracker.entity.Expense;
import com.azentrix.budgettracker.service.ExpenseService;
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

    // ── GET all entries ──────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Expense>> getAll() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    // ── GET single entry by ID ───────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    // ── POST add new entry ───────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Expense> add(@RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.addExpense(expense));
    }

    // ── PUT update entry ─────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Expense> update(@PathVariable Long id, @RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.updateExpense(id, expense));
    }

    // ── DELETE entry ─────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    // ── GET monthly summary (with chart data) ────────────────────────
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getMonthlySummary(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(expenseService.getMonthlySummary(year, month));
    }

    // ── GET entries filtered by month ────────────────────────────────
    @GetMapping("/month")
    public ResponseEntity<List<Expense>> getByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(expenseService.getExpensesByMonth(year, month));
    }
}
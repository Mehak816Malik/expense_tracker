package com.example.demo.controller;

import com.example.demo.model.Expense;
import com.example.demo.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    @Autowired
    private ExpenseRepository repository;

    // Helper method to renumber all expenses sequentially
    private void renumberAllExpenses() {
        List<Expense> allExpenses = repository.findAll();
        for (int i = 0; i < allExpenses.size(); i++) {
            Expense expense = allExpenses.get(i);
            if (expense.getSequenceNo() == null || expense.getSequenceNo() != i + 1) {
                expense.setSequenceNo(i + 1);
                repository.save(expense);
            }
        }
    }

    @GetMapping
    @Transactional
    public List<Expense> getAll() {
        renumberAllExpenses();
        return repository.findAll();
    }

    // NEW: Filter by Category API
    // Access via: /api/expenses/filter?category=Food
    @GetMapping("/filter")
    public List<Expense> getByCategory(@RequestParam String category) {
        return repository.findAll().stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
        // Basic protection: don't save if empty
        if (expense.getDescription() == null || expense.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        // Assign next sequence number based on current count
        List<Expense> allExpenses = repository.findAll();
        int nextSequenceNo = allExpenses.size() + 1;
        expense.setSequenceNo(nextSequenceNo);

        Expense saved = repository.save(expense);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);

                // Renumber all remaining expenses
                List<Expense> allExpenses = repository.findAll();
                for (int i = 0; i < allExpenses.size(); i++) {
                    allExpenses.get(i).setSequenceNo(i + 1);
                    repository.save(allExpenses.get(i));
                }

                return ResponseEntity.ok("Deleted successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ID not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }
}

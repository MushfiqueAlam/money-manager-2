package com.money_manager.controller;

import com.money_manager.dto.ExpenseDto;
import com.money_manager.repository.ExpenseRepository;
import com.money_manager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseDto dto){
        ExpenseDto expenseDto=expenseService.addExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseDto);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpenses(){
        List<ExpenseDto> expenseDtos=expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenseDtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}

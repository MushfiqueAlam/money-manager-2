package com.money_manager.controller;

import com.money_manager.dto.IncomeDto;
import com.money_manager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto dto){
        IncomeDto incomeDto=incomeService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(incomeDto);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDto>> getIncome(){
        List<IncomeDto> incomeDtos=incomeService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.of(Optional.ofNullable(incomeDtos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id){
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
}

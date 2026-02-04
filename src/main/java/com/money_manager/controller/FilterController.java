package com.money_manager.controller;

import com.money_manager.dto.ExpenseDto;
import com.money_manager.dto.FilterDto;
import com.money_manager.dto.IncomeDto;
import com.money_manager.service.ExpenseService;
import com.money_manager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController {
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDto filter){
        //preparing the data or validation
        LocalDate startDate= filter.getStartDate()!=null?filter.getStartDate():LocalDate.MIN;
        LocalDate endDAte=filter.getEndDate()!=null?filter.getEndDate():LocalDate.now();
        String keyword=filter.getKeyword()!=null? filter.getKeyword() : "";
        String sortField=filter.getSortField()!=null? filter.getSortField() : "date";
        Sort.Direction direction="desc".equalsIgnoreCase(filter.getSortOrder())? Sort.Direction.DESC: Sort.Direction.ASC;
        Sort sort=Sort.by(direction,sortField);

        if("income".equals(filter.getType())){
            List<IncomeDto> incomes=incomeService.filterIncomes(startDate,endDAte,keyword,sort);
            return ResponseEntity.ok(incomes);
        }else if("expense".equals(filter.getType())){
            List<ExpenseDto> expenses=expenseService.filterExpenses(startDate,endDAte,keyword,sort);
            return ResponseEntity.ok(expenses);
        }else {
            return ResponseEntity.badRequest().body("Invalid type. Must be income or expense");
        }
    }
}

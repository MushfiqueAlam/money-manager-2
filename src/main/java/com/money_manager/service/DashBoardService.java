package com.money_manager.service;

import com.money_manager.dto.ExpenseDto;
import com.money_manager.dto.IncomeDto;
import com.money_manager.dto.RecentTransactionDto;
import com.money_manager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.swing.UIManager.put;


@Service
@RequiredArgsConstructor
public class DashBoardService {

        private final ExpenseService expenseService;
        private final IncomeService incomeService;
        private final ProfileService profileService;


        public Map<String,Object> getDashBoardData(){
            ProfileEntity profile=profileService.getCurrentProfile();
            Map<String,Object> returnVal=new LinkedHashMap<>();
            List<IncomeDto> latestIncomes=incomeService.getLatest5IncomesForCurrentUser();
            List<ExpenseDto> latestExpenses=expenseService.getLatest5ExpensesForCurrentUser();

            List<RecentTransactionDto> recentTransaction= Stream.concat(latestIncomes.stream().map(income-> RecentTransactionDto.builder()
                            .id(income.getId())
                            .profileId(profile.getId())
                            .icon(income.getIcon())
                            .name(income.getName())
                            .amount(income.getAmount())
                            .date(income.getDate())
                            .createdAt(income.getCreatedAt())
                            .updatedAt(income.getUpdatedAt())
                            .type("income")
                            .build()),
                    latestExpenses.stream().map(expense->RecentTransactionDto.builder()
                            .id(expense.getId())
                            .profileId(profile.getId())
                            .icon(expense.getIcon())
                            .name(expense.getName())
                            .amount(expense.getAmount())
                            .date(expense.getDate())
                            .createdAt(expense.getCreatedAt())
                            .updatedAt(expense.getUpdatedAt())
                            .type("expense")
                            .build()))
                    .sorted((a,b)->{
                        int cmp=b.getDate().compareTo(a.getDate());
                        if (cmp==0 && a.getCreatedAt()!=null && b.getCreatedAt()!=null){
                            return b.getCreatedAt().compareTo(a.getCreatedAt());
                        }
                        return cmp;
                    }).collect(Collectors.toList());
            returnVal.put("totalBalance",incomeService.gettotalIncomesForCurrentUser()
                    .subtract(expenseService.gettotalExpensesForCurrentUser()));
            returnVal.put("totalIncome",incomeService.gettotalIncomesForCurrentUser());
            returnVal .put("totalExpense",expenseService.gettotalExpensesForCurrentUser());
            returnVal.put("recent5Income",latestIncomes);
            returnVal.put("recent5Expense",latestExpenses);
            returnVal.put("recentTransaction",recentTransaction);
            return returnVal;


        }
}


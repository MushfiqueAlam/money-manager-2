package com.money_manager.service;

import com.money_manager.dto.ExpenseDto;
import com.money_manager.entity.CategoryEntity;
import com.money_manager.entity.ExpenseEntity;
import com.money_manager.entity.ProfileEntity;
import com.money_manager.repository.CategoryRepository;
import com.money_manager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    //Add new expanse tho the database
    public ExpenseDto addExpense(ExpenseDto dto){
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity category=categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));

        ExpenseEntity newExpense=toEntity(dto,profile,category);
        newExpense=expenseRepository.save(newExpense);
        return toDto(newExpense);
    }


    // Read the all expenses from the current month or based on the start date and end date
    public List<ExpenseDto> getCurrentMonthExpensesForCurrentUser(){
       ProfileEntity profile= profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list=expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return  list.stream().map(this::toDto).toList();
    }

    //Delete expense by id for current user
    public void deleteExpense(Long expenseId){
        ProfileEntity profile=profileService.getCurrentProfile();
        ExpenseEntity expenseEntity=expenseRepository.findById(expenseId).orElseThrow(()->
                new RuntimeException("Expense is not found"));
        if(!expenseEntity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepository.delete(expenseEntity);
    }

    //Retrieving the latest 5 expenses for user
    public List<ExpenseDto> getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<ExpenseEntity> list=expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }

    //get the total expenses for the current User
    public BigDecimal gettotalExpensesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal totalAmount=expenseRepository.findTotalExpenseByProfile(profile.getId());
        return totalAmount!=null?totalAmount:BigDecimal.ZERO;
    }

    //filter expenses
    public List<ExpenseDto> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<ExpenseEntity> list=expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
        return list.stream().map(this::toDto).toList();

    }

    //for notifications
    public List<ExpenseDto> getExpensesForUserOnDate(Long profileId, LocalDate date){

        List<ExpenseEntity> list=expenseRepository.findByProfileIdAndDate(profileId,date);
        return list.stream().map(this::toDto).toList();

    }

    //helper method
    private ExpenseEntity toEntity(ExpenseDto dto, ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDto toDto(ExpenseEntity entity){
        return ExpenseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory().getId()!=null?entity.getCategory().getId():null)
                .categoryName(entity.getCategory()!=null?entity.getCategory().getName():"N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

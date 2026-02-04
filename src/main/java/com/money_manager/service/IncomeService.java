package com.money_manager.service;


import com.money_manager.dto.ExpenseDto;
import com.money_manager.dto.IncomeDto;
import com.money_manager.entity.CategoryEntity;
import com.money_manager.entity.ExpenseEntity;
import com.money_manager.entity.IncomeEntity;
import com.money_manager.entity.ProfileEntity;
import com.money_manager.repository.CategoryRepository;
import com.money_manager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;


    //Add new income tho the database
    public IncomeDto addIncome(IncomeDto dto){
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity category=categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));

        IncomeEntity newExpense=toEntity(dto,profile,category);
        newExpense=incomeRepository.save(newExpense);
        return toDto(newExpense);
    }

    // Read the all incomes from the current month or based on the start date and end date
    public List<IncomeDto> getCurrentMonthIncomeForCurrentUser(){
        ProfileEntity profile= profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list=incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return  list.stream().map(this::toDto).toList();
    }

    //Delete income by id for current user
    public void deleteIncome(Long expenseId){
        ProfileEntity profile=profileService.getCurrentProfile();
        IncomeEntity incomeEntity=incomeRepository.findById(expenseId).orElseThrow(()->
                new RuntimeException("Income is not found"));
        if(!incomeEntity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this Income");
        }
        incomeRepository.delete(incomeEntity);
    }


    //Retrieving the latest 5 incomes for user
    public List<IncomeDto> getLatest5IncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity> list=incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }

    //get the total income for the current User
    public BigDecimal gettotalIncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal totalAmount=incomeRepository.findTotalIncomesByProfile(profile.getId());
        return totalAmount!=null?totalAmount:BigDecimal.ZERO;
    }

    //filter Incomes
    public List<IncomeDto> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity> list=incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
        return list.stream().map(this::toDto).toList();

    }

    //helper method
    private IncomeEntity toEntity(IncomeDto dto, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDto toDto(IncomeEntity entity){
        return IncomeDto.builder()
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

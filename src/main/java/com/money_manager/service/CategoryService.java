package com.money_manager.service;

import com.money_manager.dto.CategoryDto;
import com.money_manager.entity.CategoryEntity;
import com.money_manager.entity.ProfileEntity;
import com.money_manager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;


    //SAve Category
    public CategoryDto saveCategory(CategoryDto categoryDto){
        ProfileEntity profile=profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDto.getName(),profile.getId())){
            throw new RuntimeException("Category with this name is already exits!");

        }
        CategoryEntity newEntity=toEntity(categoryDto,profile);
        categoryRepository.save(newEntity);
        return toDto(newEntity);
    }

    //get categories for current user
    public List<CategoryDto> getCategoryForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity> categories=categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDto).toList();
    }


    //get category by type of current user
    public List<CategoryDto> getCategoryByTypeForCurrentUser(String type){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity>category= categoryRepository.findByTypeAndProfileId(type,profile.getId());
        return category.stream().map(this::toDto).toList();

    }

    //update category
    public CategoryDto updateCategory(Long categoryId,CategoryDto categoryDto){
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity existingCategory=categoryRepository.findByIdAndProfileId(categoryId,profile.getId())
                .orElseThrow(()->new RuntimeException("Category is not found"));

        existingCategory.setName(categoryDto.getName());
        existingCategory.setIcon(categoryDto.getIcon());
        existingCategory.setType(categoryDto.getType());
        existingCategory=categoryRepository.save(existingCategory);
        return toDto(existingCategory);
    }

    //Helper method
    private CategoryEntity toEntity(CategoryDto categoryDto, ProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDto.getName())
                .icon(categoryDto.getIcon())
                .profile(profile)
                .type(categoryDto.getType())
                .build();
    }

    private CategoryDto toDto(CategoryEntity entity){
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .profileId(entity.getProfile()!=null?entity.getProfile().getId():null)
                .icon(entity.getIcon())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}

package com.money_manager.controller;

import com.money_manager.dto.CategoryDto;
import com.money_manager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto){
        CategoryDto savedCategory=categoryService.saveCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(){
        List<CategoryDto>categoryDtos=categoryService.getCategoryForCurrentUser();
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCategoryByTypeForCurrentUser(@PathVariable String type){
        List<CategoryDto> categoryDtos=categoryService.getCategoryByTypeForCurrentUser(type);
        return ResponseEntity.ok(categoryDtos);

    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId,@RequestBody CategoryDto categoryDto){
      CategoryDto dto=  categoryService.updateCategory(categoryId,categoryDto);
      return ResponseEntity.ok(dto);
    }

}

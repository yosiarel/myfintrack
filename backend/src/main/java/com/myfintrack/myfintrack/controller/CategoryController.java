package com.myfintrack.myfintrack.controller;

import com.myfintrack.myfintrack.dto.response.ApiResponse;
import com.myfintrack.myfintrack.dto.response.CategoryResponse;
import com.myfintrack.myfintrack.entity.Category;
import com.myfintrack.myfintrack.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @RequestParam(required = false) Category.TransactionType type) {
        List<CategoryResponse> categories;
        if (type != null) {
            categories = categoryService.getCategoriesByType(type);
        } else {
            categories = categoryService.getAllCategories();
        }
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}

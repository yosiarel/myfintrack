package com.myfintrack.myfintrack.service;

import com.myfintrack.myfintrack.dto.response.CategoryResponse;
import com.myfintrack.myfintrack.entity.Category;
import com.myfintrack.myfintrack.exception.ResourceNotFoundException;
import com.myfintrack.myfintrack.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByTypeAscNameAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getCategoriesByType(Category.TransactionType type) {
        return categoryRepository.findByType(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .color(category.getColor())
                .build();
    }
}

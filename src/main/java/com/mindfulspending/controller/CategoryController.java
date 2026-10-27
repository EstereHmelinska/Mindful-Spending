
package com.mindfulspending.controller;

import com.mindfulspending.dto.category.CategoryCreateRequest;
import com.mindfulspending.dto.category.CategoryResponse;
import com.mindfulspending.dto.category.CategoryUpdateRequest;
import com.mindfulspending.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest request) {

        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long categoryId) {

        return ResponseEntity.ok(
                categoryService.getCategoryById(categoryId)
        );
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest request) {

        CategoryResponse response = categoryService.updateCategory(
                categoryId,
                request
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long categoryId) {

        categoryService.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}

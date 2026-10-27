
package com.mindfulspending.service;

import com.mindfulspending.dto.category.CategoryCreateRequest;
import com.mindfulspending.dto.category.CategoryResponse;
import com.mindfulspending.dto.category.CategoryUpdateRequest;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryCreateRequest request);

    List<CategoryResponse> getCategories();

    CategoryResponse getCategoryById(Long categoryId);

    CategoryResponse updateCategory(
            Long categoryId,
            CategoryUpdateRequest request
    );
    void deleteCategory(Long categoryId);
}


package com.mindfulspending.service.impl;

import com.mindfulspending.dto.category.CategoryCreateRequest;
import com.mindfulspending.dto.category.CategoryResponse;
import com.mindfulspending.dto.category.CategoryUpdateRequest;
import com.mindfulspending.entity.Category;
import com.mindfulspending.entity.User;
import com.mindfulspending.exception.category.CategoryInUseException;
import com.mindfulspending.exception.category.DuplicateCategoryException;
import com.mindfulspending.exception.category.CategoryNotFoundException;
import com.mindfulspending.exception.user.UserNotFoundException;
import com.mindfulspending.repository.CategoryAllowanceRepository;
import com.mindfulspending.repository.CategoryRepository;
import com.mindfulspending.repository.ExpenseRepository;
import com.mindfulspending.repository.UserRepository;
import com.mindfulspending.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryAllowanceRepository categoryAllowanceRepository;


    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        User user = getAuthenticatedUser();

        String name = request.name().trim();

        // Check for duplicates before creating the entity
        if (categoryRepository.existsByUserUserIdAndNameIgnoreCase(
                user.getUserId(), name)) {
            throw new DuplicateCategoryException(
                    "Category with this name already exists!"
            );
        }

        Category category = new Category();
        category.setName(name);
        category.setDefault(false);
        category.setUser(user);

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }


    @Override
    public List<CategoryResponse> getCategories() {
        User user = getAuthenticatedUser();

        List<Category> categories = new ArrayList<>(
                categoryRepository.findAllByIsDefaultTrue()
        );

        categories.addAll(
                categoryRepository.findAllByUserUserId(user.getUserId())
        );

        return categories.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {
        User user = getAuthenticatedUser();

        Category category = categoryRepository
                .findByCategoryIdAndUserUserId(
                        categoryId,
                        user.getUserId()
                )
                .orElseGet(() -> categoryRepository.findAllByIsDefaultTrue()
                        .stream()
                        .filter(c -> c.getCategoryId().equals(categoryId))
                        .findFirst()
                        .orElseThrow(() -> new CategoryNotFoundException(
                                "Category Not Found!"
                        )));

        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(
            Long categoryId,
            CategoryUpdateRequest request
    ) {
        User user = getAuthenticatedUser();

        Category category = categoryRepository
                .findByCategoryIdAndUserUserId(
                        categoryId,
                        user.getUserId()
                )
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category Not Found!"
                ));

        String name = request.name().trim();

        if (categoryRepository.existsByUserUserIdAndNameIgnoreCase(
                user.getUserId(), name)
                && !category.getName().equalsIgnoreCase(name)) {
            throw new DuplicateCategoryException(
                    "Category with this name already exists!"
            );
        }

        category.setName(name);

        Category updatedCategory = categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }


    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        User user = getAuthenticatedUser();

        Category category = categoryRepository
                .findByCategoryIdAndUserUserId(
                        categoryId,
                        user.getUserId()
                )
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category Not Found!"
                ));

        if (category.isDefault()) {
            throw new CategoryNotFoundException(
                    "Default categories cannot be deleted!"
            );
        }

        if (expenseRepository.existsByCategoryCategoryId(categoryId)
                || categoryAllowanceRepository
                .existsByCategoryCategoryId(categoryId)) {
            throw new CategoryInUseException(
                    "Category cannot be deleted because it is used by expenses or category allowances!"
            );
        }

        categoryRepository.delete(category);
    }


    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getName(),
                category.isDefault()
        );
    }
}

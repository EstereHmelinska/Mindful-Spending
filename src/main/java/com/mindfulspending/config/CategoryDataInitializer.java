
package com.mindfulspending.config;

import com.mindfulspending.entity.Category;
import com.mindfulspending.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        List<String> defaultCategoryNames = List.of(
                "Food",
                "Transport",
                "Health",
                "Entertainment",
                "Shopping",
                "Utilities",
                "Rent",
                "Other"
        );

        for (String name : defaultCategoryNames) {
            if (!categoryRepository
                    .existsByIsDefaultTrueAndNameIgnoreCase(name)) {

                Category category = new Category();
                category.setName(name);
                category.setDefault(true);
                category.setUser(null);

                categoryRepository.save(category);
            }
        }
    }
}

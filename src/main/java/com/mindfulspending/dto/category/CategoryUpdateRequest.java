package com.mindfulspending.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(

        @NotBlank(message = "Category name must not be blank!")
        @Size(max = 100, message = "Category name must not exceed 100 characters!")
        String name

) {
}

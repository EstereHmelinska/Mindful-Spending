package com.mindfulspending.dto.category;

public record CategoryResponse(
        Long categoryId,
        String name,
        boolean isDefault
) {
}

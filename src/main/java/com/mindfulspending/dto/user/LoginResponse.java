package com.mindfulspending.dto.user;

public record LoginResponse(
        String token,
        UserResponse user
) {
}

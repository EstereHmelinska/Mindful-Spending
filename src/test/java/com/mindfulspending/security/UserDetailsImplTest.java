package com.mindfulspending.security;

import com.mindfulspending.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    private User user;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("password");

        userDetails = new UserDetailsImpl(user);
    }

    @Test
    void shouldReturnEmailAsUsername() {

        assertEquals(
                "test@example.com",
                userDetails.getUsername()
        );
    }

    @Test
    void shouldReturnPassword() {

        assertEquals(
                "password",
                userDetails.getPassword()
        );
    }


    @Test
    void shouldReturnWrappedUser() {

        assertSame(
                user,
                userDetails.getUser()
        );
    }
}
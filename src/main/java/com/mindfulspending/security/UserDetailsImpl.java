package com.mindfulspending.security;

import com.mindfulspending.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// Wraps our own User entity so Spring Security can work with it.
// Spring Security doesn't know about our User class directly - it only
// understands the UserDetails contract, so this class bridges the two.
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // we authenticate by email, not a separate username field
    }

    public User getUser() {
        return user;
    }
}

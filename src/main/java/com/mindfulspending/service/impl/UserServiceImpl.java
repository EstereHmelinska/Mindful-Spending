package com.mindfulspending.service.impl;

import com.mindfulspending.dto.user.LoginResponse;
import com.mindfulspending.dto.user.UserLoginRequest;
import com.mindfulspending.dto.user.UserRegisterRequest;
import com.mindfulspending.dto.user.UserResponse;
import com.mindfulspending.entity.User;
import com.mindfulspending.exception.EmailAlreadyExistsException;
import com.mindfulspending.exception.UserNotFoundException;
import com.mindfulspending.repository.UserRepository;
import com.mindfulspending.security.JwtService;
import com.mindfulspending.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserResponse registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        User saved = userRepository.save(user);

        return new UserResponse(saved.getUserId(),saved.getEmail());
    }

    @Override
    public LoginResponse login(UserLoginRequest request){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(
                token,
                new UserResponse(
                        user.getUserId(),
                        user.getEmail()
                )
        );
    }

    @Override
    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        return new UserResponse(
                user.getUserId(),
                user.getEmail());
    }


    @Override
    public void deleteUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        userRepository.deleteById(user.getUserId());
    }
}

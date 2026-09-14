package com.mindfulspending.controller;


import com.mindfulspending.dto.user.LoginResponse;
import com.mindfulspending.dto.user.UserLoginRequest;
import com.mindfulspending.dto.user.UserRegisterRequest;
import com.mindfulspending.dto.user.UserResponse;
import com.mindfulspending.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final UserServiceImpl userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginRequest request){
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

}

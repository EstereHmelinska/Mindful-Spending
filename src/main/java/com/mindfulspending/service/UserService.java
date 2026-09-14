package com.mindfulspending.service;

import com.mindfulspending.dto.user.LoginResponse;
import com.mindfulspending.dto.user.UserLoginRequest;
import com.mindfulspending.dto.user.UserRegisterRequest;
import com.mindfulspending.dto.user.UserResponse;

public interface UserService {
    //The registerUser method returns a UserResponse and receives a UserRegisterRequest
    UserResponse registerUser(UserRegisterRequest request);
    LoginResponse login(UserLoginRequest request);
    UserResponse getCurrentUser();
    void deleteUser();

}

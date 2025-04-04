package com.example.tallybook.service;

import com.example.tallybook.model.User;
import com.example.tallybook.model.dto.ForgotPasswordRequest;
import com.example.tallybook.model.dto.LoginRequest;
import com.example.tallybook.model.dto.RegisterRequest;
import com.example.tallybook.model.dto.VerificationCodeRequest;

import java.util.Map;

public interface UserService {
    
    /**
     * Register a new user
     * @param request registration request
     * @return user info
     */
    Map<String, Object> register(RegisterRequest request);
    
    /**
     * User login
     * @param request login request
     * @return token and user info
     */
    Map<String, Object> login(LoginRequest request);
    
    /**
     * Get verification code
     * @param request verification code request
     * @return success message
     */
    void getVerificationCode(VerificationCodeRequest request);
    
    /**
     * Reset password using verification code
     * @param request forgot password request
     */
    void resetPassword(ForgotPasswordRequest request);
    
    /**
     * Get current user info
     * @param userId current user ID
     * @return user info
     */
    Map<String, Object> getUserInfo(Integer userId);
    
    /**
     * Logout
     * @param userId current user ID
     */
    void logout(Integer userId);
} 
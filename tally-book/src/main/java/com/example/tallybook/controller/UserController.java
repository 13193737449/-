package com.example.tallybook.controller;

import com.example.tallybook.mapper.UserMapper;
import com.example.tallybook.model.User;
import com.example.tallybook.model.dto.ApiResponse;
import com.example.tallybook.model.dto.ForgotPasswordRequest;
import com.example.tallybook.model.dto.LoginRequest;
import com.example.tallybook.model.dto.RegisterRequest;
import com.example.tallybook.service.CaptchaService;
import com.example.tallybook.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关接口控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final CaptchaService captchaService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 获取当前登录用户信息
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/info")
    public ApiResponse<?> getUserInfo(HttpServletRequest request) {
        log.debug("开始获取用户信息");
        try {
            // 从请求头中获取JWT令牌
            String token = getTokenFromRequest(request);
            log.debug("从请求头获取的令牌: {}", token != null ? "有效" : "无效");
            if (token == null) {
                return ApiResponse.error(401, "未授权，请重新登录");
            }
            
            try {
                // 从令牌中提取用户ID
                Integer userId = jwtUtil.extractUserId(token);
                log.debug("从令牌中提取的用户ID: {}", userId);
                if (userId == null) {
                    return ApiResponse.error(401, "无效的令牌，请重新登录");
                }
                
                // 查询用户信息
                User user = userMapper.findById(userId);
                log.debug("查询到的用户信息: {}", user != null ? "存在" : "不存在");
                if (user == null) {
                    return ApiResponse.error(404, "用户不存在");
                }
                
                // 构造返回数据（脱敏处理）
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("userId", user.getUserId());
                userInfo.put("username", user.getUsername());
                userInfo.put("nickname", user.getNickname());
                userInfo.put("avatar", user.getAvatar());
                
                // 手机号脱敏
                if (user.getPhone() != null) {
                    userInfo.put("phone", user.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                }
                
                userInfo.put("createTime", user.getCreatedAt());
                
                log.debug("用户信息获取成功");
                return ApiResponse.success(userInfo);
            } catch (Exception e) {
                log.error("令牌解析异常", e);
                return ApiResponse.error(401, "令牌解析失败: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ApiResponse.error(500, "获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // 使用Spring Security进行身份验证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            // 身份验证成功，获取用户详情
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 查询用户信息
            User user = userMapper.findByUsername(userDetails.getUsername());
            if (user == null) {
                user = userMapper.findByPhone(userDetails.getUsername());
            }
            
            if (user == null) {
                return ApiResponse.error(401, "用户不存在");
            }
            
            // 更新最后登录时间
            userMapper.updateLastLoginTime(user.getUserId());
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(userDetails.getUsername(), user.getUserId());
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("tokenType", "Bearer");
            data.put("expiresIn", 86400); // 24小时 (秒)
            data.put("userId", user.getUserId());
            data.put("username", userDetails.getUsername());
            
            return ApiResponse.success("登录成功", data);
        } catch (BadCredentialsException e) {
            return ApiResponse.error(401, "用户名或密码错误");
        } catch (Exception e) {
            log.error("登录异常", e);
            return ApiResponse.error(500, "登录失败，服务器内部错误: " + e.getMessage());
        }
    }
    
    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 验证图形验证码
            boolean captchaValid = captchaService.verifyCaptcha(request.getCaptchaId(), request.getCaptchaCode());
            if (!captchaValid) {
                return ApiResponse.error(400, "验证码错误或已过期");
            }
            
            // 检查用户名是否已存在
            if (userMapper.countByUsername(request.getUsername()) > 0) {
                return ApiResponse.error(400, "用户名已存在");
            }
            
            // 检查手机号是否已存在
            if (userMapper.countByPhone(request.getPhone()) > 0) {
                return ApiResponse.error(400, "手机号已被注册");
            }
            
            // 创建用户
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPhone(request.getPhone());
            user.setNickname(request.getUsername());
            user.setStatus(1); // 1-正常
            
            // 保存用户
            int result = userMapper.insert(user);
            if (result <= 0) {
                return ApiResponse.error(500, "注册失败，请稍后再试");
            }
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getUserId());
            data.put("username", user.getUsername());
            data.put("phone", user.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")); // 手机号脱敏
            
            return ApiResponse.success("注册成功", data);
        } catch (Exception e) {
            log.error("注册异常", e);
            return ApiResponse.error(500, "注册失败，服务器内部错误: " + e.getMessage());
        }
    }
    
    /**
     * 找回密码
     * @param request 找回密码请求
     * @return 处理结果
     */
    @PostMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            // 验证图形验证码
            boolean captchaValid = captchaService.verifyCaptcha(request.getCaptchaId(), request.getCaptchaCode());
            if (!captchaValid) {
                return ApiResponse.error(400, "验证码错误或已过期");
            }
            
            // 检查手机号是否存在
            if (userMapper.countByPhone(request.getPhone()) <= 0) {
                return ApiResponse.error(400, "该手机号未注册");
            }
            
            // 重置密码
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            int result = userMapper.updatePassword(request.getPhone(), encodedPassword);
            
            if (result <= 0) {
                return ApiResponse.error(500, "密码重置失败，请稍后再试");
            }
            
            return ApiResponse.success("密码重置成功");
        } catch (Exception e) {
            log.error("密码重置异常", e);
            return ApiResponse.error(500, "密码重置失败，服务器内部错误: " + e.getMessage());
        }
    }
    
    /**
     * 从请求中获取token
     * @param request HTTP请求
     * @return token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 
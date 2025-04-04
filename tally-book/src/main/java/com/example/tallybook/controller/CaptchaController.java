package com.example.tallybook.controller;

import com.example.tallybook.model.dto.ApiResponse;
import com.example.tallybook.model.dto.CaptchaResponse;
import com.example.tallybook.model.dto.CaptchaVerifyRequest;
import com.example.tallybook.service.CaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 图形验证码控制器
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {
    
    private final CaptchaService captchaService;
    
    /**
     * 获取图形验证码
     * @return 图形验证码信息
     */
    @GetMapping("/get")
    public ApiResponse<CaptchaResponse> getCaptcha() {
        CaptchaResponse captcha = captchaService.generateCaptcha();
        return ApiResponse.success(captcha);
    }
    
    /**
     * 验证图形验证码
     * @param request 验证请求
     * @return 验证结果
     */
    @PostMapping("/verify")
    public ApiResponse<Map<String, Boolean>> verifyCaptcha(@Valid @RequestBody CaptchaVerifyRequest request) {
        boolean verified = captchaService.verifyCaptcha(request.getCaptchaId(), request.getCaptchaCode());
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("verified", verified);
        
        if (verified) {
            return ApiResponse.success("验证成功", result);
        } else {
            return ApiResponse.error(400, "验证码错误或已过期", result);
        }
    }
} 
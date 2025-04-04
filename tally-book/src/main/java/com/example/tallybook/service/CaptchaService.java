package com.example.tallybook.service;

import com.example.tallybook.model.dto.CaptchaResponse;

/**
 * 图形验证码服务接口
 */
public interface CaptchaService {
    
    /**
     * 生成图形验证码
     * @return 图形验证码信息
     */
    CaptchaResponse generateCaptcha();
    
    /**
     * 验证图形验证码
     * @param captchaId 验证码ID
     * @param code 用户输入的验证码
     * @return 是否验证通过
     */
    boolean verifyCaptcha(String captchaId, String code);
} 
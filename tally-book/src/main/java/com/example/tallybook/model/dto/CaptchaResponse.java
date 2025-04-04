package com.example.tallybook.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图形验证码响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse {
    /**
     * 验证码ID
     */
    private String captchaId;
    
    /**
     * Base64编码的图片数据
     */
    private String captchaImage;
    
    /**
     * 验证码有效期，单位：秒
     */
    private Integer expireTime;
} 
package com.example.tallybook.service;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {
    
    /**
     * 生成验证码并发送到手机
     * @param phone 手机号
     * @param type 验证码类型 (register/forgot)
     * @return 生成的验证码（实际环境中不应返回，这里为了测试方便）
     */
    String generateAndSendCode(String phone, String type);
    
    /**
     * 验证验证码是否正确
     * @param phone 手机号
     * @param code 用户输入的验证码
     * @param type 验证码类型
     * @return 是否正确
     */
    boolean verifyCode(String phone, String code, String type);
} 
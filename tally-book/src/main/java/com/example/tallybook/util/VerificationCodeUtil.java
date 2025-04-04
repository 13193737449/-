package com.example.tallybook.util;

import java.util.Random;

/**
 * 验证码工具类
 */
public class VerificationCodeUtil {
    
    private static final String NUMBERS = "0123456789";
    private static final Random RANDOM = new Random();
    
    /**
     * 生成指定长度的数字验证码
     * @param length 验证码长度
     * @return 生成的验证码
     */
    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }
        return sb.toString();
    }
    
    /**
     * 生成6位数字验证码
     * @return 6位数字验证码
     */
    public static String generateCode() {
        return generateCode(6);
    }
} 
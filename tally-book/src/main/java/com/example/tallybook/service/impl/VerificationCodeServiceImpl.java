package com.example.tallybook.service.impl;

import com.example.tallybook.exception.BusinessException;
import com.example.tallybook.service.VerificationCodeService;
import com.example.tallybook.util.VerificationCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务实现类
 * 注意：在实际生产环境中，应使用Redis存储验证码，并集成短信服务商API发送短信
 * 这里使用内存存储和日志输出模拟，仅用于演示
 */
@Service
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    // 使用内存缓存存储验证码，格式：phone_type -> code
    private final Map<String, String> codeCache = new ConcurrentHashMap<>();
    
    // 验证码有效期（毫秒）
    private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000; // 5分钟
    
    // 存储验证码生成时间，用于判断是否过期
    private final Map<String, Long> codeTimeCache = new ConcurrentHashMap<>();

    @Override
    public String generateAndSendCode(String phone, String type) {
        // 验证手机号和类型
        if (phone == null || phone.length() != 11) {
            throw new BusinessException("手机号格式不正确");
        }
        
        if (!"register".equals(type) && !"forgot".equals(type)) {
            throw new BusinessException("验证码类型不正确，只能是register或forgot");
        }
        
        // 生成6位数字验证码
        String code = VerificationCodeUtil.generateCode();
        
        // 存储验证码和生成时间（在实际环境中应存入Redis）
        String key = phone + "_" + type;
        codeCache.put(key, code);
        codeTimeCache.put(key, System.currentTimeMillis());
        
        // 在实际环境中，这里应该调用短信服务商API发送短信
        // 这里仅打印日志模拟发送
        log.info("向手机号 {} 发送 {} 类型的验证码: {}", phone, type, code);
        
        return code; // 实际环境中不应返回验证码，这里为了方便测试
    }

    @Override
    public boolean verifyCode(String phone, String code, String type) {
        if (phone == null || code == null || type == null) {
            return false;
        }
        
        String key = phone + "_" + type;
        String storedCode = codeCache.get(key);
        Long storedTime = codeTimeCache.get(key);
        
        // 验证码不存在
        if (storedCode == null || storedTime == null) {
            return false;
        }
        
        // 验证码过期
        if (System.currentTimeMillis() - storedTime > CODE_EXPIRE_TIME) {
            // 删除过期验证码
            codeCache.remove(key);
            codeTimeCache.remove(key);
            return false;
        }
        
        // 验证码匹配校验
        boolean isValid = storedCode.equals(code);
        
        // 验证通过后删除验证码，一次性使用
        if (isValid) {
            codeCache.remove(key);
            codeTimeCache.remove(key);
        }
        
        return isValid;
    }
} 
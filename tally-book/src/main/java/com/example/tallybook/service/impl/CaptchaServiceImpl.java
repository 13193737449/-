package com.example.tallybook.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.example.tallybook.model.dto.CaptchaResponse;
import com.example.tallybook.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图形验证码服务实现类 - 使用Hutool-captcha
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {
    
    // 验证码宽度
    private static final int CAPTCHA_WIDTH = 150;
    
    // 验证码高度
    private static final int CAPTCHA_HEIGHT = 40;
    
    // 验证码长度
    private static final int CAPTCHA_LENGTH = 4;
    
    // 验证码干扰线数量
    private static final int CAPTCHA_INTERFERENCE_COUNT = 3;
    
    // 验证码有效期（秒）
    private static final int CAPTCHA_EXPIRE_TIME = 300; // 5分钟
    
    // 存储验证码，格式：captchaId -> code
    private final Map<String, String> captchaCache = new ConcurrentHashMap<>();
    
    // 存储验证码生成时间，用于判断是否过期
    private final Map<String, Long> captchaTimeCache = new ConcurrentHashMap<>();

    @Override
    public CaptchaResponse generateCaptcha() {
        // 使用Hutool的LineCaptcha（线段干扰验证码）
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(
                CAPTCHA_WIDTH, CAPTCHA_HEIGHT, CAPTCHA_LENGTH, CAPTCHA_INTERFERENCE_COUNT);
        
        // 获取验证码文本
        String captchaText = captcha.getCode();
        
        // 获取验证码Base64图片
        String base64Image = captcha.getImageBase64Data();
        
        // 处理验证码存储和返回逻辑
        return processCaptcha(captchaText, base64Image);
    }

    @Override
    public boolean verifyCaptcha(String captchaId, String code) {
        if (captchaId == null || code == null) {
            return false;
        }
        
        String storedCode = captchaCache.get(captchaId);
        Long storedTime = captchaTimeCache.get(captchaId);
        
        // 验证码不存在
        if (storedCode == null || storedTime == null) {
            return false;
        }
        
        // 验证码过期
        if (System.currentTimeMillis() - storedTime > CAPTCHA_EXPIRE_TIME * 1000L) {
            // 删除过期验证码
            captchaCache.remove(captchaId);
            captchaTimeCache.remove(captchaId);
            return false;
        }
        
        // 验证码匹配校验（忽略大小写）
        boolean isValid = storedCode.equalsIgnoreCase(code);
        
        // 验证通过后删除验证码，一次性使用
        if (isValid) {
            captchaCache.remove(captchaId);
            captchaTimeCache.remove(captchaId);
        }
        
        return isValid;
    }
    
    /**
     * 处理验证码生成公共逻辑
     * @param captchaText 验证码文本
     * @param base64Image Base64编码的验证码图片
     * @return 验证码响应
     */
    private CaptchaResponse processCaptcha(String captchaText, String base64Image) {
        // 生成验证码ID
        String captchaId = UUID.randomUUID().toString();
        
        // 存储验证码和生成时间（在实际环境中应存入Redis）
        captchaCache.put(captchaId, captchaText);
        captchaTimeCache.put(captchaId, System.currentTimeMillis());
        
        // 确保base64Image包含data:image前缀
        if (!base64Image.startsWith("data:image")) {
            base64Image = "data:image/png;base64," + base64Image;
        }
        
        // 打印日志
        log.debug("生成图形验证码ID: {}, 验证码: {}", captchaId, captchaText);
        
        // 返回验证码信息
        return CaptchaResponse.builder()
                .captchaId(captchaId)
                .captchaImage(base64Image)
                .expireTime(CAPTCHA_EXPIRE_TIME)
                .build();
    }
} 
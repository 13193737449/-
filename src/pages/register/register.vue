<template>
  <view class="register-container">
    <!-- 手机模型 -->
    <view class="phone-wrapper">
      <!-- Logo区域 -->
      <view class="logo-area">
        <view class="logo animate-float">
          <view class="ledger-icon">
            <view class="ledger-cover"></view>
            <view class="ledger-pages"></view>
            <view class="ledger-line"></view>
            <view class="ledger-line"></view>
            <view class="ledger-line"></view>
          </view>
        </view>
      </view>
      
      <!-- 标题区域 -->
      <view class="title-area">
        <text class="welcome-title">创建账号</text>
        <text class="welcome-subtitle">填写信息完成注册</text>
      </view>
      
      <!-- 表单区域 -->
      <view class="form-area">
        <view class="form-item">
          <text class="form-label">手机号</text>
          <view class="input-wrapper" :class="{'input-active': phoneActive, 'input-error': errors.phone}">
            <text class="input-icon">📱</text>
            <input 
              class="form-input" 
              type="number" 
              v-model="phone" 
              placeholder="请输入手机号" 
              placeholder-class="input-placeholder"
              maxlength="11"
              @focus="phoneActive = true"
              @blur="phoneActive = false"
            />
          </view>
          <text v-if="errors.phone" class="error-text">{{errors.phone}}</text>
        </view>
        
        <view class="form-item">
          <text class="form-label">验证码</text>
          <view class="input-wrapper verification-wrapper" :class="{'input-active': codeActive, 'input-error': errors.captcha}">
            <text class="input-icon">🔑</text>
            <input 
              class="form-input verification-input" 
              type="text" 
              v-model="captchaCode" 
              placeholder="请输入验证码" 
              placeholder-class="input-placeholder"
              maxlength="6"
              @focus="codeActive = true"
              @blur="codeActive = false"
            />
            <view class="captcha-image" @tap="refreshCaptcha">
              <image v-if="captchaImageUrl && !captchaLoading" :src="captchaImageUrl" mode="aspectFit" />
              <text v-else class="captcha-loading">{{captchaLoading ? '加载中...' : '点击刷新'}}</text>
            </view>
          </view>
          <text v-if="errors.captcha" class="error-text">{{errors.captcha}}</text>
        </view>
        
        <view class="form-item">
          <text class="form-label">用户名</text>
          <view class="input-wrapper" :class="{'input-active': usernameActive, 'input-error': errors.username}">
            <text class="input-icon">👤</text>
            <input 
              class="form-input" 
              type="text" 
              v-model="username" 
              placeholder="请输入用户名" 
              placeholder-class="input-placeholder"
              @focus="usernameActive = true"
              @blur="usernameActive = false"
            />
          </view>
          <text v-if="errors.username" class="error-text">{{errors.username}}</text>
        </view>
        
        <view class="form-item">
          <text class="form-label">密码</text>
          <view class="input-wrapper" :class="{'input-active': passwordActive, 'input-error': errors.password}">
            <text class="input-icon">🔒</text>
            <input 
              class="form-input" 
              :type="showPassword ? 'text' : 'password'" 
              v-model="password" 
              placeholder="请输入密码" 
              placeholder-class="input-placeholder"
              @focus="passwordActive = true"
              @blur="passwordActive = false"
            />
            <view class="password-toggle" @tap="togglePassword">
              <view class="icon-eye" :class="{'icon-eye-closed': !showPassword}"></view>
            </view>
          </view>
          <!-- 密码强度指示器 -->
          <view class="password-strength-meter" v-if="password">
            <view class="strength-bar-container">
              <view 
                class="strength-bar" 
                :style="{width: passwordStrength + '%'}"
                :class="{
                  'weak': passwordStrength > 0 && passwordStrength < 40,
                  'medium': passwordStrength >= 40 && passwordStrength < 80,
                  'strong': passwordStrength >= 80
                }"
              ></view>
            </view>
            <text class="strength-text" :class="{
              'weak-text': passwordStrength > 0 && passwordStrength < 40,
              'medium-text': passwordStrength >= 40 && passwordStrength < 80,
              'strong-text': passwordStrength >= 80
            }">
              {{ passwordStrengthText }}
            </text>
          </view>
          <text v-if="errors.password" class="error-text">{{errors.password}}</text>
        </view>
        
        <view class="form-item">
          <text class="form-label">确认密码</text>
          <view class="input-wrapper" :class="{'input-active': confirmPasswordActive, 'input-error': errors.confirm}">
            <text class="input-icon">🔒</text>
            <input 
              class="form-input" 
              :type="showPassword ? 'text' : 'password'" 
              v-model="confirmPassword" 
              placeholder="请再次输入密码" 
              placeholder-class="input-placeholder"
              @focus="confirmPasswordActive = true"
              @blur="confirmPasswordActive = false"
            />
          </view>
          <text v-if="errors.confirm" class="error-text">{{errors.confirm}}</text>
        </view>
        
        <!-- 注册协议 -->
        <view class="agreement">
          <checkbox :checked="agreed" @tap="toggleAgreement" />
          <text class="agreement-text">我已阅读并同意<text class="agreement-link">《用户协议》</text>和<text class="agreement-link">《隐私政策》</text></text>
        </view>
        
        <!-- 注册按钮 -->
        <button 
          class="register-btn" 
          :class="{'btn-active': registerActive, 'btn-disabled': !isFormValid}" 
          :disabled="!isFormValid" 
          @touchstart="registerActive = true" 
          @touchend="registerActive = false"
          @touchcancel="registerActive = false"
          @tap="register"
        >
          <text class="btn-shine"></text>
          注册
        </button>
        
        <!-- 返回登录 -->
        <button 
          class="login-btn" 
          :class="{'login-btn-active': loginActive}" 
          @touchstart="loginActive = true" 
          @touchend="loginActive = false"
          @touchcancel="loginActive = false"
          @tap="goToLogin"
        >
          返回登录
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, watch, onMounted, reactive } from 'vue';
import { userApi } from '@/api';

// 响应式数据
const phone = ref('');
const username = ref('');
const password = ref('');
const confirmPassword = ref('');
const captchaCode = ref('');
const captchaId = ref('');
const captchaImageUrl = ref('');
const phoneActive = ref(false);
const usernameActive = ref(false);
const passwordActive = ref(false);
const confirmPasswordActive = ref(false);
const codeActive = ref(false);
const registerActive = ref(false);
const loginActive = ref(false);
const showPassword = ref(false);
const agreed = ref(false);
const countDown = ref(0);
const captchaLoading = ref(false);

// 表单验证相关的错误信息
const errors = reactive({
  phone: '',
  captcha: '',
  username: '',
  password: '',
  confirm: ''
});

// 计算密码强度
const calculatePasswordStrength = (pwd) => {
  if (!pwd) return 0;
  
  let score = 0;
  
  // 基础长度分数 (最多30分)
  score += Math.min(30, pwd.length * 4);
  
  // 检查字母
  if (/[a-z]/.test(pwd)) score += 10;
  if (/[A-Z]/.test(pwd)) score += 10;
  
  // 检查数字
  const numCount = (pwd.match(/\d/g) || []).length;
  score += Math.min(20, numCount * 5);
  
  // 检查特殊字符
  const specialCount = (pwd.match(/[^a-zA-Z0-9]/g) || []).length;
  score += Math.min(25, specialCount * 5);
  
  // 内容多样性额外加分
  const uniqueChars = [...new Set(pwd.split(''))].length;
  score += uniqueChars;
  
  return Math.min(100, score);
};

// 密码强度计算
const passwordStrength = computed(() => {
  return calculatePasswordStrength(password.value);
});

// 密码强度文本
const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value;
  if (strength === 0) return '';
  if (strength < 40) return '弱';
  if (strength < 80) return '中';
  return '强';
});

// 表单验证
const isPhoneValid = computed(() => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  const valid = phoneRegex.test(phone.value);
  errors.phone = !phone.value ? '' : (!valid ? '请输入正确的手机号码' : '');
  return valid;
});

const isCodeValid = computed(() => {
  const valid = captchaCode.value && captchaCode.value.length >= 3;
  errors.captcha = !captchaCode.value ? '' : (!valid ? '验证码长度不足' : '');
  return valid;
});

const isUsernameValid = computed(() => {
  const valid = username.value && username.value.length >= 3 && username.value.length <= 20;
  errors.username = !username.value ? '' : (!valid ? '用户名长度必须在3-20个字符之间' : '');
  return valid;
});

const isPasswordValid = computed(() => {
  // 密码至少8位，包含字母和数字
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
  if (password.value) {
    const hasLetter = /[A-Za-z]/.test(password.value);
    const hasNumber = /\d/.test(password.value);
    const hasLength = password.value.length >= 8;
    
    if (!hasLength) {
      errors.password = '密码长度至少8位';
    } else if (!hasLetter) {
      errors.password = '密码必须包含字母';
    } else if (!hasNumber) {
      errors.password = '密码必须包含数字';
    } else {
      errors.password = '';
    }
    
    return passwordRegex.test(password.value);
  }
  
  errors.password = '';
  return false;
});

// 主动监听密码变化，实时更新验证状态
watch(password, (newVal) => {
  if (newVal) {
    isPasswordValid.value; // 触发计算属性
    
    // 如果有确认密码，检查是否匹配
    if (confirmPassword.value) {
      errors.confirm = newVal !== confirmPassword.value ? '两次输入的密码不一致' : '';
    }
  } else {
    errors.password = '';
  }
});

// 监听确认密码变化
watch(confirmPassword, (newVal) => {
  if (newVal && password.value) {
    errors.confirm = newVal !== password.value ? '两次输入的密码不一致' : '';
  } else {
    errors.confirm = '';
  }
});

const passwordMismatch = computed(() => {
  const mismatch = confirmPassword.value && password.value !== confirmPassword.value;
  errors.confirm = mismatch ? '两次输入的密码不一致' : '';
  return mismatch;
});

const isFormValid = computed(() => {
  return isPhoneValid.value && 
         isCodeValid.value &&
         isUsernameValid.value &&
         isPasswordValid.value && 
         confirmPassword.value && 
         !passwordMismatch.value &&
         agreed.value;
});

// 开始倒计时
const startCountDown = (seconds = 60) => {
  countDown.value = seconds;
  const timer = setInterval(() => {
    if (countDown.value > 0) {
      countDown.value--;
    } else {
      clearInterval(timer);
    }
  }, 1000);
};

// 获取验证码
const getCaptcha = () => {
  captchaLoading.value = true;
  captchaImageUrl.value = ''; // 清空图片触发loading效果
  
  console.log('正在请求验证码...');
  
  userApi.getCaptcha().then(res => {
    console.log('验证码请求成功:', res);
    if (res.data) {
      captchaId.value = res.data.captchaId;
      captchaImageUrl.value = res.data.captchaImage;
    } else {
      console.error('验证码响应格式错误:', res);
      uni.showToast({
        title: '验证码格式错误',
        icon: 'none'
      });
    }
  }).catch(error => {
    console.error('获取验证码失败:', error);
    
    uni.showToast({
      title: '获取验证码失败，请刷新重试',
      icon: 'none'
    });
  }).finally(() => {
    captchaLoading.value = false;
  });
};

// 刷新验证码
const refreshCaptcha = () => {
  if (captchaLoading.value) return; // 防止重复点击
  
  getCaptcha();
};

// 切换密码显示状态
const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

// 切换协议同意状态
const toggleAgreement = () => {
  agreed.value = !agreed.value;
};

// 表单校验 (增强验证)
const validateForm = () => {
  // 重置所有错误
  Object.keys(errors).forEach(key => {
    errors[key] = '';
  });

  // 手机号验证
  if (!phone.value) {
    errors.phone = '请输入手机号码';
    return false;
  } else if (!isPhoneValid.value) {
    errors.phone = '请输入正确的手机号码';
    return false;
  }
  
  // 验证码验证
  if (!captchaCode.value) {
    errors.captcha = '请输入验证码';
    return false;
  } else if (!isCodeValid.value) {
    errors.captcha = '验证码不正确';
    return false;
  }
  
  // 验证码ID检查
  if (!captchaId.value) {
    console.error('验证码ID为空，刷新验证码');
    refreshCaptcha();
    uni.showToast({
      title: '验证码已失效，请刷新',
      icon: 'none'
    });
    return false;
  }
  
  // 用户名验证
  if (!username.value) {
    errors.username = '请输入用户名';
    return false;
  } else if (username.value.length < 3) {
    errors.username = '用户名长度不能少于3个字符';
    return false;
  } else if (username.value.length > 20) {
    errors.username = '用户名长度不能超过20个字符';
    return false;
  } else if (!isUsernameValid.value) {
    errors.username = '用户名长度必须在3-20个字符之间';
    return false;
  }
  
  if (!password.value) {
    errors.password = '请输入密码';
    return false;
  } else if (!isPasswordValid.value) {
    errors.password = '密码至少8位，包含字母和数字';
    return false;
  }
  
  if (!confirmPassword.value) {
    errors.confirm = '请确认密码';
    return false;
  } else if (passwordMismatch.value) {
    errors.confirm = '两次输入的密码不一致';
    return false;
  }
  
  if (!agreed.value) {
    uni.showToast({
      title: '请阅读并同意用户协议',
      icon: 'none'
    });
    return false;
  }
  
  return true;
};

// 注册方法
const register = () => {
  if (!validateForm()) {
    // 显示第一个错误信息
    for (const key in errors) {
      if (errors[key]) {
        uni.showToast({
          title: errors[key],
          icon: 'none'
        });
        return;
      }
    }
    return;
  }
  
  uni.showLoading({
    title: '注册中...'
  });
  
  // 调用注册API
  userApi.register({
    phone: phone.value,
    captchaId: captchaId.value,
    captchaCode: captchaCode.value,
    username: username.value,
    password: password.value
  }).then(res => {
    uni.hideLoading();
    
    uni.showToast({
      title: '注册成功',
      icon: 'success'
    });
    
    // 注册成功后返回登录页
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }).catch(error => {
    uni.hideLoading();
    console.error('注册失败:', error);
    
    // 刷新验证码
    refreshCaptcha();
    
    uni.showToast({
      title: error.message || '注册失败，请重试',
      icon: 'none'
    });
  });
};

// 初始化时获取验证码
onMounted(() => {
  console.log('组件已挂载，准备获取验证码...');
  
  // 延迟获取验证码，确保网络已就绪
  setTimeout(() => {
    getCaptcha();
  }, 300);
});

// 返回登录页
const goToLogin = () => {
  uni.navigateBack();
};
</script>

<style lang="scss">
// 导入样式文件
@import "@/static/login/style.scss";

.register-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: $background-color;
  box-sizing: border-box;
  padding: 30rpx;
  
  // 添加背景渐变效果
  background-image: 
    radial-gradient(circle at 90% 20%, rgba($secondary-color, 0.08) 0%, transparent 25%),
    radial-gradient(circle at 10% 80%, rgba($primary-color, 0.08) 0%, transparent 25%);
}

.phone-wrapper {
  width: 100%;
  max-width: 650rpx;
  background-color: $card-color;
  border-radius: $radius-large;
  box-shadow: $shadow-medium;
  overflow: hidden;
  padding: 40rpx 40rpx;
  box-sizing: border-box;
  animation: fade-in 0.8s ease-out forwards;
  border: 1px solid $border-color;
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-area {
  display: flex;
  justify-content: center;
  margin: 20rpx 0;
}

.logo {
  width: 120rpx;
  height: 120rpx;
  border-radius: $radius-large;
  background: $gradient-secondary;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: $shadow-large;
  position: relative;
  overflow: hidden;
}

.ledger-icon {
  width: 90%;
  height: 90%;
  position: relative;
}

.ledger-cover {
  position: absolute;
  width: 75%;
  height: 100%;
  left: 12%;
  background-color: white;
  border-radius: 10rpx;
  border: 2rpx solid rgba(0, 0, 0, 0.3);
  z-index: 2;
  box-shadow: 2rpx 2rpx 6rpx rgba(0, 0, 0, 0.2);
}

.ledger-pages {
  position: absolute;
  width: 70%;
  height: 92%;
  right: 8%;
  top: 4%;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 6rpx 0 0 6rpx;
  border-left: 2rpx solid rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.ledger-line {
  position: absolute;
  width: 50%;
  height: 3rpx;
  background-color: rgba(0, 0, 0, 0.3);
  left: 22%;
  z-index: 3;
  
  &:nth-child(3) {
    top: 30%;
  }
  
  &:nth-child(4) {
    top: 50%;
  }
  
  &:nth-child(5) {
    top: 70%;
  }
}

.animate-float {
  animation: float 5s infinite ease-in-out;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15rpx);
  }
}

.title-area {
  text-align: center;
  margin-bottom: 40rpx;
  animation: fade-in 0.8s ease-out 0.2s both;
}

.welcome-title {
  font-size: 48rpx;
  font-weight: 600;
  color: $text-dark;
  margin-bottom: 20rpx;
  display: block;
}

.welcome-subtitle {
  font-size: 28rpx;
  color: $text-light;
  display: block;
}

.form-area {
  width: 100%;
  animation: fade-in 0.8s ease-out 0.4s both;
}

.form-item {
  margin-bottom: 25rpx;
}

.form-label {
  font-size: 28rpx;
  color: $text-muted;
  display: block;
  margin-bottom: 15rpx;
}

.input-wrapper {
  position: relative;
  width: 100%;
  transition: all 0.3s ease;
}

.input-active {
  transform: translateY(-3rpx);
  box-shadow: 0 0 0 3rpx rgba($primary-color, 0.15), 0 8rpx 16rpx rgba(0, 0, 0, 0.2);
}

.input-error {
  border-color: #ff4d4f;
  animation: shake 0.5s;
}

@keyframes shake {
  10%, 90% {
    transform: translateX(-2rpx);
  }
  
  20%, 80% {
    transform: translateX(4rpx);
  }
  
  30%, 50%, 70% {
    transform: translateX(-6rpx);
  }
  
  40%, 60% {
    transform: translateX(6rpx);
  }
}

.input-icon {
  position: absolute;
  left: 30rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 32rpx;
  color: $text-light;
  transition: color 0.3s ease;
}

.password-toggle {
  position: absolute;
  right: 30rpx;
  top: 50%;
  transform: translateY(-50%);
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:active {
    transform: translateY(-50%) scale(0.95);
  }
}

.icon-eye {
  position: relative;
  width: 34rpx;
  height: 17rpx;
  border: 2rpx solid $text-light;
  border-bottom-width: 0;
  border-top-left-radius: 34rpx;
  border-top-right-radius: 34rpx;
  &::after {
    content: '';
    position: absolute;
    width: 8rpx;
    height: 8rpx;
    border-radius: 50%;
    background-color: $text-light;
    top: 42%;
    left: 50%;
    transform: translate(-50%, -50%);
  }
}

.icon-eye-closed {
  border: none;
  &::before {
    content: '';
    position: absolute;
    width: 34rpx;
    height: 2rpx;
    background-color: $text-light;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }
  &::after {
    display: none;
  }
}

.input-active .input-icon {
  color: $primary-color;
}

.input-active .icon-eye {
  border-color: $primary-color;
  &::after {
    background-color: $primary-color;
  }
}

.input-active .icon-eye-closed::before {
  background-color: $primary-color;
}

.form-input {
  width: 100%;
  height: 90rpx;
  background-color: $input-bg;
  border: 1px solid $border-color;
  border-radius: $radius-large;
  padding: 0 30rpx 0 80rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  transition: all 0.3s ease;
  color: $text-dark;
}

.input-active .form-input {
  border-color: $primary-color;
  background-color: rgba($primary-color, 0.05);
}

.input-placeholder {
  color: rgba($text-light, 0.6);
}

// 协议相关样式
.agreement {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
  margin-top: 10rpx;
}

.agreement-text {
  font-size: 24rpx;
  color: $text-light;
  margin-left: 10rpx;
}

.agreement-link {
  color: $primary-color;
}

.register-btn {
  position: relative;
  width: 100%;
  height: 90rpx;
  background: $gradient-secondary;
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 600;
  border-radius: $radius-large;
  margin-bottom: 30rpx;
  box-shadow: $shadow-medium;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  transition: all 0.3s ease;
}

.btn-active {
  transform: translateY(3rpx);
  box-shadow: $shadow-small;
}

.btn-disabled {
  opacity: 0.7;
}

.btn-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0.2) 50%,
    rgba(255, 255, 255, 0) 100%
  );
  animation: shine 4s infinite;
}

@keyframes shine {
  0% {
    left: -100%;
  }
  20%, 100% {
    left: 100%;
  }
}

.login-btn {
  width: 100%;
  height: 90rpx;
  background-color: transparent;
  color: $primary-color;
  font-size: 32rpx;
  font-weight: 600;
  border: 2rpx solid $primary-color;
  border-radius: $radius-large;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.login-btn-active {
  background-color: rgba($primary-color, 0.1);
  transform: translateY(3rpx);
  color: $secondary-color;
  border-color: $secondary-color;
}

// 添加错误提示样式
.error-text {
  font-size: 24rpx;
  color: #ff4d4f;
  margin-top: 10rpx;
  display: block;
}

// 验证码输入框样式
.verification-wrapper {
  display: flex;
  align-items: center;
}

.verification-input {
  flex: 1;
}

.captcha-image {
  height: 70rpx;
  width: 200rpx;
  margin-right: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: $radius-small;
}

.captcha-image image {
  width: 100%;
  height: 100%;
  border-radius: $radius-small;
}

.captcha-loading {
  font-size: 24rpx;
  color: $text-muted;
}

// 密码强度指示器样式
.password-strength-meter {
  margin-top: 10rpx;
  margin-bottom: 10rpx;
}

.strength-bar-container {
  height: 10rpx;
  background-color: #f0f0f0;
  border-radius: 5rpx;
  overflow: hidden;
}

.strength-bar {
  height: 100%;
  transition: width 0.3s ease;
}

.weak {
  background-color: #ff4d4f;
}

.medium {
  background-color: #ffa500;
}

.strong {
  background-color: #00b300;
}

.strength-text {
  font-size: 24rpx;
  color: $text-light;
  margin-left: 10rpx;
}

.weak-text {
  color: #ff4d4f;
}

.medium-text {
  color: #ffa500;
}

.strong-text {
  color: #00b300;
}
</style> 
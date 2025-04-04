<template>
  <view class="forgot-container">
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
        <text class="welcome-title">找回密码</text>
        <text class="welcome-subtitle">重置您的账号密码</text>
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
          <text class="form-label">新密码</text>
          <view class="input-wrapper" :class="{'input-active': passwordActive, 'input-error': errors.password}">
            <text class="input-icon">🔒</text>
            <input 
              class="form-input" 
              :type="showPassword ? 'text' : 'password'" 
              v-model="newPassword" 
              placeholder="请输入新密码" 
              placeholder-class="input-placeholder"
              @focus="passwordActive = true"
              @blur="passwordActive = false"
            />
            <view class="password-toggle" @tap="togglePassword">
              <view class="icon-eye" :class="{'icon-eye-closed': !showPassword}"></view>
            </view>
          </view>
          <!-- 密码强度指示器 -->
          <view class="password-strength-meter" v-if="newPassword">
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
              placeholder="请再次输入新密码" 
              placeholder-class="input-placeholder"
              @focus="confirmPasswordActive = true"
              @blur="confirmPasswordActive = false"
            />
          </view>
          <text v-if="errors.confirm" class="error-text">{{errors.confirm}}</text>
        </view>
        
        <!-- 重置按钮 -->
        <button 
          class="reset-btn" 
          :class="{'btn-active': resetActive, 'btn-disabled': !isFormValid || loading}" 
          :disabled="!isFormValid || loading" 
          @touchstart="resetActive = true" 
          @touchend="resetActive = false"
          @touchcancel="resetActive = false"
          @tap="resetPassword"
        >
          <text class="btn-shine"></text>
          <text class="btn-icon" v-if="!loading">🔄</text>
          <text class="btn-loading" v-if="loading">
            <text class="dot"></text>
            <text class="dot"></text>
            <text class="dot"></text>
          </text>
          <text class="btn-text">{{loading ? '处理中' : '重置密码'}}</text>
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
          <text class="btn-icon back-icon">←</text>
          <text>返回登录</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, reactive, watch } from 'vue';
import { userApi } from '@/api';

// 响应式数据
const phone = ref('');
const captchaCode = ref('');
const captchaId = ref('');
const captchaImageUrl = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
const phoneActive = ref(false);
const codeActive = ref(false);
const passwordActive = ref(false);
const confirmPasswordActive = ref(false);
const resetActive = ref(false);
const loginActive = ref(false);
const showPassword = ref(false);
const loading = ref(false);
const captchaLoading = ref(false);

// 表单验证相关的错误信息
const errors = reactive({
  phone: '',
  captcha: '',
  password: '',
  confirm: ''
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

const isPasswordValid = computed(() => {
  // 密码至少8位，包含字母和数字
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
  if (newPassword.value) {
    const hasLetter = /[A-Za-z]/.test(newPassword.value);
    const hasNumber = /\d/.test(newPassword.value);
    const hasLength = newPassword.value.length >= 8;
    
    if (!hasLength) {
      errors.password = '密码长度至少8位';
    } else if (!hasLetter) {
      errors.password = '密码必须包含字母';
    } else if (!hasNumber) {
      errors.password = '密码必须包含数字';
    } else {
      errors.password = '';
    }
    
    return passwordRegex.test(newPassword.value);
  }
  
  errors.password = '';
  return false;
});

const passwordMismatch = computed(() => {
  const mismatch = confirmPassword.value && newPassword.value !== confirmPassword.value;
  errors.confirm = mismatch ? '两次输入的密码不一致' : '';
  return mismatch;
});

const isFormValid = computed(() => {
  return isPhoneValid.value && 
         isCodeValid.value &&
         isPasswordValid.value && 
         confirmPassword.value && 
         !passwordMismatch.value;
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
  return calculatePasswordStrength(newPassword.value);
});

// 密码强度文本
const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value;
  if (strength === 0) return '';
  if (strength < 40) return '弱';
  if (strength < 80) return '中';
  return '强';
});

// 主动监听密码变化，实时更新验证状态
watch(newPassword, (newVal) => {
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
  if (newVal && newPassword.value) {
    errors.confirm = newVal !== newPassword.value ? '两次输入的密码不一致' : '';
  } else {
    errors.confirm = '';
  }
});

// 获取图形验证码
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

// 表单校验
const validateForm = () => {
  if (!phone.value) {
    errors.phone = '请输入手机号码';
    return false;
  } else if (!isPhoneValid.value) {
    errors.phone = '请输入正确的手机号码';
    return false;
  }
  
  if (!captchaCode.value) {
    errors.captcha = '请输入验证码';
    return false;
  } else if (!isCodeValid.value) {
    errors.captcha = '验证码不正确';
    return false;
  }
  
  if (!newPassword.value) {
    errors.password = '请输入新密码';
    return false;
  } else if (!isPasswordValid.value) {
    errors.password = '密码至少8位，包含字母和数字';
    return false;
  }
  
  if (!confirmPassword.value) {
    errors.confirm = '请确认新密码';
    return false;
  } else if (passwordMismatch.value) {
    errors.confirm = '两次输入的密码不一致';
    return false;
  }
  
  return true;
};

// 找回密码方法
const resetPassword = () => {
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
  
  loading.value = true;
  
  uni.showLoading({
    title: '验证中...'
  });
  
  // 调用找回密码API
  userApi.forgotPassword({
    phone: phone.value,
    captchaId: captchaId.value,
    captchaCode: captchaCode.value,
    newPassword: newPassword.value
  }).then(res => {
    uni.hideLoading();
    
    uni.showToast({
      title: '密码重置成功',
      icon: 'success'
    });
    
    // 重置密码成功后返回登录页
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }).catch(error => {
    uni.hideLoading();
    console.error('重置密码失败:', error);
    
    // 刷新验证码
    refreshCaptcha();
    
    uni.showToast({
      title: error.message || '重置失败，请重试',
      icon: 'none'
    });
  }).finally(() => {
    loading.value = false;
  });
};

// 返回登录页
const goToLogin = () => {
  uni.navigateBack();
};

// 初始化时获取验证码，添加延迟确保DOM已渲染
onMounted(() => {
  console.log('组件已挂载，准备获取验证码...');
  
  // 延迟获取验证码，确保网络已就绪
  setTimeout(() => {
    getCaptcha();
  }, 300);
});
</script>

<style lang="scss">
// 导入样式文件
@import "@/static/login/style.scss";

.forgot-container {
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
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 4rpx;
    background: $gradient-primary;
  }
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
  margin: 20rpx 0 30rpx;
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
  margin-bottom: 50rpx;
  animation: fade-in 0.8s ease-out 0.2s both;
  position: relative;
  padding: 20rpx 0;
  
  // 添加背景高光效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(circle at center, rgba($primary-color, 0.1) 0%, transparent 70%);
    z-index: -1;
    opacity: 0.7;
    animation: glow 3s infinite alternate;
  }
}

@keyframes glow {
  from {
    opacity: 0.5;
    transform: scale(0.95);
  }
  to {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

.welcome-title {
  font-size: 60rpx;
  font-weight: 700;
  color: $text-dark;
  margin-bottom: 15rpx;
  display: block;
  background-image: $gradient-primary;
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  text-shadow: $glow-primary;
  position: relative;
  letter-spacing: 2rpx;
  
  // 添加底部装饰线
  &::after {
    content: '';
    position: absolute;
    bottom: -10rpx;
    left: 50%;
    transform: translateX(-50%);
    width: 60rpx;
    height: 6rpx;
    background: $gradient-primary;
    border-radius: 3rpx;
    animation: pulse 2s infinite;
  }
  
  // 添加文字阴影效果
  text-shadow: 0 0 15rpx rgba($primary-color, 0.5), 0 0 30rpx rgba($secondary-color, 0.3);
}

@keyframes pulse {
  0% {
    opacity: 0.6;
    width: 60rpx;
  }
  50% {
    opacity: 1;
    width: 100rpx;
  }
  100% {
    opacity: 0.6;
    width: 60rpx;
  }
}

.welcome-subtitle {
  font-size: 30rpx;
  color: $text-light;
  display: block;
  opacity: 0.8;
  margin-top: 25rpx;
  position: relative;
  
  // 添加闪光效果
  &::before {
    content: '✦';
    margin-right: 8rpx;
    color: $primary-color;
    animation: twinkle 1.5s infinite;
  }
  
  &::after {
    content: '✦';
    margin-left: 8rpx;
    color: $secondary-color;
    animation: twinkle 1.5s infinite 0.5s;
  }
}

@keyframes twinkle {
  0%, 100% {
    opacity: 0.4;
    transform: scale(0.8);
  }
  50% {
    opacity: 1;
    transform: scale(1.2);
  }
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
  height: 90rpx;
  background-color: $input-bg;
  border-radius: $radius-medium;
  overflow: hidden;
  border: 1px solid $border-color;
  transition: all 0.3s ease;
  
  // 发光边框效果
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    border-radius: $radius-medium;
    border: 1px solid transparent;
    pointer-events: none;
  }
}

.input-active {
  transform: translateY(-3rpx);
  box-shadow: $shadow-small;
  
  &::after {
    border-color: $primary-color;
    box-shadow: $glow-primary;
  }
}

.form-input {
  width: 100%;
  height: 100%;
  background: transparent;
  border: none;
  color: $text-dark;
  font-size: 30rpx;
  padding: 0 90rpx 0 80rpx;
  box-sizing: border-box;
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

.input-active .input-icon {
  color: $primary-color;
}

.verification-wrapper {
  display: flex;
  align-items: center;
}

.verification-input {
  flex: 1;
  padding-right: 10rpx;
}

.captcha-image {
  height: 70rpx;
  width: 200rpx;
  margin-right: 15rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.05);
  border-radius: $radius-small;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.98);
    opacity: 0.8;
  }
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

.error-text {
  color: #ff4d4f;
  font-size: 24rpx;
  margin-top: 10rpx;
  display: block;
}

.reset-btn {
  width: 100%;
  height: 90rpx;
  border-radius: $radius-medium;
  background: $gradient-primary;
  color: $text-dark;
  font-size: 32rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border: none;
  box-shadow: $shadow-large;
  margin-top: 40rpx;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  
  // 玻璃质感效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 50%;
    background: rgba(255, 255, 255, 0.1);
    border-radius: $radius-medium $radius-medium 50rpx 50rpx;
    pointer-events: none;
  }
  
  .btn-shine {
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.4),
      transparent
    );
    animation: btn-shine 3s infinite;
  }
}

@keyframes btn-shine {
  0% {
    left: -100%;
  }
  20% {
    left: 100%;
  }
  100% {
    left: 100%;
  }
}

.btn-active {
  transform: translateY(3rpx) scale(0.98);
  box-shadow: $shadow-small;
  
  &::after {
    opacity: 1;
  }
}

.btn-disabled {
  opacity: 0.7;
  background: rgba($text-light, 0.2);
  color: $text-muted;
  box-shadow: none;
  
  &::before {
    opacity: 0.3;
  }
  
  .btn-shine {
    animation: none;
  }
}

.login-btn {
  width: 100%;
  height: 80rpx;
  border-radius: $radius-medium;
  background: transparent;
  border: 2rpx solid $border-color;
  color: $text-light;
  font-size: 30rpx;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 30rpx;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  
  // 添加波纹效果
  &::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 5rpx;
    height: 5rpx;
    background: rgba($primary-color, 0.3);
    opacity: 0;
    border-radius: 100%;
    transform: scale(1) translate(-50%, -50%);
    transform-origin: 0 0;
  }
  
  &:active::after {
    animation: ripple 0.6s ease-out;
  }
}

@keyframes ripple {
  0% {
    opacity: 0.6;
    transform: scale(0) translate(-50%, -50%);
  }
  100% {
    opacity: 0;
    transform: scale(20) translate(-50%, -50%);
  }
}

.login-btn-active {
  background-color: rgba($text-muted, 0.1);
  border-color: $primary-color;
  transform: scale(0.98);
  color: $primary-color;
  text-shadow: 0 0 8rpx rgba($primary-color, 0.3);
  
  &::before {
    opacity: 1;
  }
}

// 为按钮添加悬停效果的替代实现
.reset-btn:not(.btn-disabled):active {
  box-shadow: 0 0 15rpx rgba($primary-color, 0.5), 0 0 30rpx rgba($secondary-color, 0.3);
}

.login-btn:active {
  background-color: rgba($primary-color, 0.08);
}

// 设置输入框占位符样式
.input-placeholder {
  color: rgba($text-muted, 0.6);
  font-size: 28rpx;
}

.input-error {
  border-color: #ff4d4f;
  animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both;
  
  &::after {
    border-color: #ff4d4f;
    box-shadow: 0 0 8rpx rgba(255, 77, 79, 0.4);
  }
  
  .input-icon {
    color: #ff4d4f;
  }
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

.btn-icon {
  font-size: 32rpx;
  margin-right: 10rpx;
  display: inline-block;
  transition: transform 0.3s ease;
}

.back-icon {
  margin-right: 10rpx;
  transform: translateX(0);
  transition: transform 0.3s ease;
}

.login-btn-active .back-icon {
  transform: translateX(-5rpx);
}

.btn-text {
  margin-left: 8rpx;
}

.btn-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10rpx;
}

.dot {
  display: inline-block;
  width: 8rpx;
  height: 8rpx;
  border-radius: 50%;
  background-color: $text-dark;
  margin: 0 4rpx;
  opacity: 0.7;
}

.dot:nth-child(1) {
  animation: dot-flashing 1s infinite alternate 0s;
}

.dot:nth-child(2) {
  animation: dot-flashing 1s infinite 0.3s;
}

.dot:nth-child(3) {
  animation: dot-flashing 1s infinite alternate 0.6s;
}

@keyframes dot-flashing {
  0% {
    opacity: 0.2;
    transform: scale(0.8);
  }
  100% {
    opacity: 1;
    transform: scale(1.2);
  }
}

.label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15rpx;
}

.captcha-switch {
  font-size: 24rpx;
  color: $primary-color;
  opacity: 0.8;
  transition: all 0.3s ease;
  
  &:active {
    opacity: 1;
    transform: scale(0.98);
  }
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
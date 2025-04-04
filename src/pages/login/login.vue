<template>
  <view class="login-container">
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
        <text class="welcome-title">欢迎回来</text>
        <text class="welcome-subtitle">请登录您的账号继续</text>
      </view>
      
      <!-- 表单区域 -->
      <view class="form-area">
        <view class="form-item">
          <text class="form-label">用户名</text>
          <view class="input-wrapper" :class="{'input-active': usernameActive}">
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
        </view>
        
        <view class="form-item">
          <text class="form-label">密码</text>
          <view class="input-wrapper" :class="{'input-active': passwordActive}">
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
        </view>
        
        <view class="form-item">
          <text class="form-label">验证码</text>
          <view class="input-wrapper verification-wrapper" :class="{'input-active': codeActive}">
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
        </view>
        
        <!-- 忘记密码 -->
        <view class="forgot-password">
          <text class="forgot-text" @tap="forgotPassword">忘记密码?</text>
        </view>
        
        <!-- 登录按钮 -->
        <button 
          class="login-btn" 
          :class="{'btn-active': loginActive, 'btn-disabled': !isFormValid}" 
          :disabled="!isFormValid" 
          @touchstart="loginActive = true" 
          @touchend="loginActive = false"
          @touchcancel="loginActive = false"
          @tap="login"
        >
          <text class="btn-shine"></text>
          登录
        </button>
        
        <!-- 注册按钮 -->
        <button 
          class="register-btn" 
          :class="{'register-btn-active': registerActive}" 
          @touchstart="registerActive = true" 
          @touchend="registerActive = false"
          @touchcancel="registerActive = false"
          @tap="register"
        >
          注册新账号
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { userApi } from '@/api';

// 响应式数据
const username = ref('');
const password = ref('');
const captchaCode = ref('');
const captchaId = ref('');
const captchaImageUrl = ref('');
const usernameActive = ref(false);
const passwordActive = ref(false);
const codeActive = ref(false);
const loginActive = ref(false);
const registerActive = ref(false);
const showPassword = ref(false);
const captchaLoading = ref(false);

// 表单验证
const isCodeValid = computed(() => {
  return captchaCode.value && captchaCode.value.length >= 3;
});

const isFormValid = computed(() => {
  return username.value && password.value && isCodeValid.value;
});

// 页面加载时检查登录状态并获取验证码
onMounted(() => {
  checkLoginStatus();
  getCaptcha();
});

// 检查登录状态
const checkLoginStatus = () => {
  try {
    const token = uni.getStorageSync('token');
    const userInfo = uni.getStorageSync('userInfo');
    
    if (token && userInfo) {
      console.log('用户已登录，跳转到首页');
      // 已登录，直接跳转到首页
      uni.switchTab({
        url: '/pages/index/index'
      });
    }
  } catch (e) {
    console.error('检查登录状态时发生错误:', e);
  }
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

// 登录方法
const login = () => {
  if (!isFormValid.value) {
    uni.showToast({
      title: '请填写所有必填项',
      icon: 'none'
    });
    return;
  }
  
  uni.showLoading({
    title: '登录中...'
  });
  
  // 调用登录API
  userApi.login({
    username: username.value,
    password: password.value,
    captchaId: captchaId.value,
    captchaCode: captchaCode.value
  }).then(res => {
    uni.hideLoading();
    
    // 登录成功，获取token和用户信息
    const { token, userInfo } = res.data;
    
    // 将登录信息存储到本地存储中
    try {
      uni.setStorageSync('token', token);
      uni.setStorageSync('userInfo', JSON.stringify(userInfo));
    } catch (e) {
      console.error('保存登录信息失败:', e);
    }
    
    uni.showToast({
      title: '登录成功',
      icon: 'success'
    });
    
    // 登录成功后跳转到首页
    setTimeout(() => {
      uni.reLaunch({
        url: '/pages/index/index'
      });
    }, 1500);
  }).catch(error => {
    uni.hideLoading();
    console.error('登录失败:', error);
    
    // 刷新验证码
    refreshCaptcha();
    
    uni.showToast({
      title: error.message || '登录失败，请重试',
      icon: 'none'
    });
  });
};

// 注册方法
const register = () => {
  uni.navigateTo({
    url: '/pages/register/register'
  });
};

// 忘记密码
const forgotPassword = () => {
  uni.navigateTo({
    url: '/pages/forgot-password/forgot-password'
  });
};
</script>

<style lang="scss">
// 直接导入样式文件
@import "@/static/login/style.scss";

.login-container {
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
    radial-gradient(circle at 10% 20%, rgba($primary-color, 0.08) 0%, transparent 25%),
    radial-gradient(circle at 90% 80%, rgba($secondary-color, 0.08) 0%, transparent 25%);
}

.phone-wrapper {
  width: 100%;
  max-width: 650rpx;
  background-color: $card-color;
  border-radius: $radius-large;
  box-shadow: $shadow-medium;
  overflow: hidden;
  padding: 50rpx 40rpx;
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
  margin: 40rpx 0;
}

.logo {
  width: 140rpx;
  height: 140rpx;
  border-radius: $radius-large;
  background: $gradient-primary;
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

.logo-text {
  color: #ffffff;
  font-size: 80rpx;
  font-weight: bold;
  display: none;
}

.title-area {
  text-align: center;
  margin-bottom: 50rpx;
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
  margin-bottom: 30rpx;
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

.forgot-password {
  text-align: right;
  margin-bottom: 40rpx;
}

.forgot-text {
  color: $primary-color;
  font-size: 26rpx;
  transition: color 0.3s ease;
}

.forgot-text:active {
  color: $secondary-color;
}

.login-btn {
  position: relative;
  width: 100%;
  height: 90rpx;
  background: $gradient-primary;
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

.register-btn {
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

.register-btn-active {
  background-color: rgba($primary-color, 0.1);
  transform: translateY(3rpx);
  color: $secondary-color;
  border-color: $secondary-color;
}

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
</style> 
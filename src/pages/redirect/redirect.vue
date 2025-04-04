<template>
  <view class="redirect-container">
    <view class="loading-spinner"></view>
    <text class="redirect-text">正在跳转到登录页...</text>
  </view>
</template>

<script setup>
import { onMounted, onBeforeMount } from 'vue';

// 在组件挂载前执行重定向
onBeforeMount(() => {
  redirectToLogin();
});

// 在组件挂载后也执行重定向，确保能正常跳转
onMounted(() => {
  redirectToLogin();
});

// 重定向到登录页的函数
function redirectToLogin() {
  console.log('正在重定向到登录页面...');
  
  try {
    const token = uni.getStorageSync('token');
    if (token) {
      // 如果已登录，跳转到首页
      console.log('检测到已登录，跳转到首页');
      uni.reLaunch({
        url: '/pages/index/index'
      });
    } else {
      // 未登录，跳转到登录页
      console.log('未检测到登录状态，跳转到登录页');
      uni.reLaunch({
        url: '/pages/login/login'
      });
    }
  } catch (e) {
    console.error('重定向过程中发生错误:', e);
    // 出错时默认跳转到登录页
    uni.reLaunch({
      url: '/pages/login/login'
    });
  }
}
</script>

<style>
.redirect-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #121212;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.1);
  border-top: 4rpx solid #00e5ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.redirect-text {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.7);
}
</style> 
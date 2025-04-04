<script>
export default {
  onLaunch: function() {
    console.log('App Launch');
    this.checkLoginAndRedirect();
  },
  onShow: function() {
    console.log('App Show');
    // 每次应用显示时也检查登录状态
    this.checkLoginAndRedirect();
  },
  onHide: function() {
    console.log('App Hide');
  },
  methods: {
    // 检查登录状态并重定向
    checkLoginAndRedirect() {
      // 获取当前页面路径
      const pages = getCurrentPages();
      const currentPath = pages.length > 0 ? (pages[0].route || '') : '';
      
      console.log('当前页面路径:', currentPath);
      
      // 如果是首页路径或为空，则检查并重定向
      if (!currentPath || currentPath === 'pages/redirect/redirect' || currentPath === '') {
        try {
          console.log('检查登录状态...');
          const token = uni.getStorageSync('token');
          
          if (token) {
            console.log('检测到已登录，跳转到首页');
            // 已登录，跳转到首页
            setTimeout(() => {
              uni.reLaunch({
                url: '/pages/index/index'
              });
            }, 100);
          } else {
            console.log('未登录，跳转到登录页');
            // 未登录，跳转到登录页
            setTimeout(() => {
              uni.reLaunch({
                url: '/pages/login/login'
              });
            }, 100);
          }
        } catch (e) {
          console.error('检查登录状态时发生错误:', e);
          // 错误时默认跳转到登录页
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/login/login'
            });
          }, 100);
        }
      } else {
        console.log('当前不在重定向路径，无需重定向');
      }
    }
  }
};
</script>

<style lang="scss">
/*每个页面公共css */
@import "@/static/login/style.scss";

page {
  font-size: 28rpx;
  box-sizing: border-box;
  background-color: $background-color;
  color: $text-dark;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

/* 全局暗色主题相关样式 */
.app-container {
  min-height: 100vh;
  background-color: $background-color;
}

/* 全局渐变文本效果 */
.gradient-text {
  background: $gradient-primary;
  -webkit-background-clip: text;
  color: transparent;
}

/* 霓虹灯文本效果 */
.neon-text {
  color: $primary-color;
  text-shadow: 0 0 10rpx rgba($primary-color, 0.5), 0 0 20rpx rgba($primary-color, 0.3);
}

/* 霓虹灯按钮效果 */
.neon-button {
  background-color: transparent;
  border: 1px solid $primary-color;
  color: $primary-color;
  position: relative;
  overflow: hidden;
  z-index: 1;
  box-shadow: $glow-primary;
  transition: all 0.3s ease;

  &:active {
    box-shadow: $glow-inner-primary;
    transform: translateY(3rpx);
  }
}

/* 彩色卡片样式 */
.colorful-card {
  background-color: $card-color;
  border-radius: $radius-large;
  padding: 30rpx;
  box-shadow: $shadow-medium;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba($primary-color, 0.1);
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      linear-gradient(217deg, rgba($primary-color, 0.03), rgba($primary-color, 0) 70%),
      linear-gradient(127deg, rgba($secondary-color, 0.03), rgba($secondary-color, 0) 70%),
      linear-gradient(336deg, rgba($accent1-color, 0.03), rgba($accent1-color, 0) 70%);
    z-index: -1;
  }
}

/* 渐变边框效果 */
.gradient-border {
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: $gradient-primary;
    border-radius: inherit;
    z-index: -1;
    opacity: 0.5;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: $card-color;
    border-radius: inherit;
    z-index: -1;
  }
}

/* 闪光动画效果 */
.shine-effect {
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
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
    animation: shine 3s infinite;
    z-index: 1;
  }
}

@keyframes shine {
  0% {
    left: -100%;
  }
  20%, 100% {
    left: 100%;
  }
}

/* 全局动画效果 */
.float-animation {
  animation: float 5s infinite ease-in-out;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10rpx);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: rgba($background-color, 0.8);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: $gradient-primary;
  border-radius: 3px;
}
</style>

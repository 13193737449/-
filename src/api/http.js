import axios from 'axios';

// 创建axios实例
const http = axios.create({
  baseURL: 'http://localhost:8080/api', // 修改为本地开发服务器地址并添加上下文路径 
  timeout: 15000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器：在请求发送前处理
http.interceptors.request.use(
  config => {
    // 从本地存储获取token
    const token = uni.getStorageSync('token');
    // 如果有token，添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    console.error('请求拦截器错误:', error);
    uni.showToast({
      title: '请求发送失败，请稍后再试',
      icon: 'none'
    });
    return Promise.reject(error);
  }
);

// 响应拦截器：在收到响应后处理
http.interceptors.response.use(
  response => {
    const res = response.data;
    
    // 判断响应状态码
    if (res.code !== 200) {
      // 401: 未登录或token失效
      if (res.code === 401) {
        // 清除本地存储的登录信息
        uni.removeStorageSync('token');
        uni.removeStorageSync('userInfo');
        
        // 跳转到登录页
        uni.reLaunch({
          url: '/pages/login/login'
        });
        
        // 提示用户
        uni.showToast({
          title: '登录信息已过期，请重新登录',
          icon: 'none'
        });
      } else {
        // 其他错误，显示错误消息
        let title = res.message || '操作失败';
        
        // 针对不同的业务错误码，提供更友好的错误提示
        switch (res.code) {
          case 400:
            title = title || '输入参数有误，请检查后重试';
            break;
          case 403:
            title = title || '您没有权限执行此操作';
            break;
          case 404:
            title = title || '请求的数据不存在';
            break;
          case 500:
            title = title || '服务器异常，请稍后再试';
            break;
        }
        
        uni.showToast({
          title: title,
          icon: 'none',
          duration: 2000
        });
      }
      
      return Promise.reject(new Error(res.message || '请求处理失败'));
    } else {
      // 正常返回数据
      return res;
    }
  },
  error => {
    console.error('响应拦截器错误:', error);
    
    // 网络错误处理
    if (!error.response) {
      uni.showToast({
        title: '网络连接失败，请检查您的网络设置',
        icon: 'none',
        duration: 3000
      });
    } else {
      // HTTP状态码错误处理
      const status = error.response.status;
      let message = '请求失败，请稍后重试';
      
      switch (status) {
        case 400:
          message = '请求数据格式有误，请检查输入';
          break;
        case 401:
          message = '登录已失效，请重新登录';
          // 清除登录信息
          uni.removeStorageSync('token');
          uni.removeStorageSync('userInfo');
          // 跳转到登录页
          uni.reLaunch({
            url: '/pages/login/login'
          });
          break;
        case 403:
          message = '您没有权限执行此操作';
          break;
        case 404:
          message = '请求的内容不存在';
          break;
        case 408:
          message = '请求超时，请检查网络后重试';
          break;
        case 500:
          message = '服务器发生错误，请稍后再试';
          break;
        case 502:
          message = '服务器维护中，请稍后再试';
          break;
        case 503:
          message = '服务暂时不可用，请稍后再试';
          break;
        case 504:
          message = '服务器响应超时，请稍后再试';
          break;
        default:
          message = `服务异常(${status})，请稍后再试`;
      }
      
      uni.showToast({
        title: message,
        icon: 'none',
        duration: 3000
      });
    }
    
    return Promise.reject(error);
  }
);

export default http; 
import http from './http';

// 用户相关接口
const userApi = {
  /**
   * 用户注册
   * @param {Object} data - 注册信息
   * @param {String} data.username - 用户名
   * @param {String} data.password - 密码
   * @param {String} data.phone - 手机号码
   * @param {String} data.captchaId - 验证码ID
   * @param {String} data.captchaCode - 用户输入的验证码
   * @returns {Promise}
   */
  register(data) {
    return http.post('user/register', data);
  },
  
  /**
   * 用户登录
   * @param {Object} data - 登录信息
   * @param {String} data.username - 用户名
   * @param {String} data.password - 密码
   * @returns {Promise}
   */
  login(data) {
    return http.post('user/login', data);
  },
  
  /**
   * 退出登录
   * @returns {Promise}
   */
  logout() {
    return http.post('user/logout');
  },
  
  /**
   * 找回密码
   * @param {Object} data - 找回密码信息
   * @param {String} data.phone - 手机号码
   * @param {String} data.newPassword - 新密码
   * @param {String} data.captchaId - 验证码ID
   * @param {String} data.captchaCode - 用户输入的验证码
   * @returns {Promise}
   */
  forgotPassword(data) {
    return http.post('user/forgot-password', data);
  },
  
  /**
   * 获取用户信息
   * @returns {Promise}
   */
  getUserInfo() {
    return http.get('user/info');
  },
  
  /**
   * 获取用户设置
   * @returns {Promise}
   */
  getUserSettings() {
    return http.get('user/settings');
  },
  
  /**
   * 更新用户设置
   * @param {Object} data - 设置信息
   * @param {String} [data.theme] - 主题
   * @param {String} [data.currency] - 货币单位
   * @param {String} [data.language] - 语言
   * @param {Boolean} [data.notification] - 是否开启通知
   * @returns {Promise}
   */
  updateUserSettings(data) {
    return http.put('user/settings', data);
  },
  
  /**
   * 设置月度预算
   * @param {Object} data - 预算信息
   * @param {Number} data.budget - 总预算金额
   * @param {Array} [data.categoryBudgets] - 分类预算数组
   * @returns {Promise}
   */
  setBudget(data) {
    return http.post('user/budget', data);
  },
  
  /**
   * 获取手机验证码
   * @param {Object} data - 请求信息
   * @param {String} data.phone - 手机号码
   * @param {String} data.type - 验证码类型(register/forgot)
   * @returns {Promise}
   */
  getVerificationCode(data) {
    return http.post('user/verification-code', data);
  },

  /**
   * 获取图形验证码
   * @returns {Promise} 返回captchaId和图形验证码base64数据
   */
  getCaptcha() {
    const timestamp = new Date().getTime();
    return http.get(`captcha/get?t=${timestamp}`);
  },

  /**
   * 验证图形验证码
   * @param {Object} data - 验证信息
   * @param {String} data.captchaId - 验证码ID
   * @param {String} data.captchaCode - 用户输入的验证码
   * @returns {Promise}
   */
  verifyCaptcha(data) {
    return http.post('captcha/verify', data);
  }
};

export default userApi; 
import http from './http';

// 系统配置相关接口
const configApi = {
  /**
   * 获取账本分类列表
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getCategoryList(params) {
    // 只返回支出类别
    const queryParams = { ...params };
    return http.get('category/list', { params: queryParams });
  },
  
  /**
   * 获取收支汇总统计
   * @param {Object} params 查询参数
   * @param {String} params.startDate 开始日期，格式：yyyy-MM-dd
   * @param {String} params.endDate 结束日期，格式：yyyy-MM-dd
   * @returns {Promise}
   */
  getSummary(params) {
    return http.get('statistics/summary', { params });
  },
  
  /**
   * 获取分类统计
   * @param {Object} params 查询参数
   * @param {String} params.startDate 开始日期，格式：yyyy-MM-dd
   * @param {String} params.endDate 结束日期，格式：yyyy-MM-dd
   * @returns {Promise}
   */
  getCategoryStatistics(params) {
    // 固定只查询支出类型的统计
    const queryParams = { ...params };
    return http.get('statistics/category', { params: queryParams });
  },
  
  /**
   * 获取趋势统计
   * @param {Object} params 查询参数
   * @param {String} params.period 统计周期：day-按天，month-按月，year-按年
   * @param {String} params.startDate 开始日期，格式：yyyy-MM-dd
   * @param {String} params.endDate 结束日期，格式：yyyy-MM-dd
   * @returns {Promise}
   */
  getTrendStatistics(params) {
    // 固定只查询支出类型的统计
    const queryParams = { ...params };
    return http.get('statistics/trend', { params: queryParams });
  }
};

export default configApi; 
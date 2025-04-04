import http from './http';

// 记账记录相关接口
const recordApi = {
  /**
   * 添加记账记录
   * @param {Object} data - 记账数据
   * @param {String} data.category - 分类代码，如'food', 'transport'等
   * @param {Number} data.amount - 金额
   * @param {String} [data.description] - 备注
   * @param {String} data.record_time - 记账时间（格式：YYYY-MM-DD HH:mm:ss）
   * @returns {Promise}
   */
  addRecord(data) {
    return http.post('record/add', data);
  },
  
  /**
   * 获取记账记录列表
   * @param {Object} params - 查询参数
   * @param {Number} [params.page=1] - 页码
   * @param {Number} [params.size=10] - 每页条数
   * @param {String} [params.category] - 分类代码，如'food', 'transport'等
   * @param {String} [params.startDate] - 开始日期，格式：yyyy-MM-dd
   * @param {String} [params.endDate] - 结束日期，格式：yyyy-MM-dd
   * @returns {Promise}
   */
  getRecordList(params = {}) {
    // 设置默认值
    const defaultParams = {
      page: 1,
      size: 10
    };
    // 合并参数
    const queryParams = { ...defaultParams, ...params };
    return http.get('record/list', { params: queryParams });
  },
  
  /**
   * 获取记账记录详情
   * @param {Number} record_id - 记录ID
   * @returns {Promise}
   */
  getRecordDetail(record_id) {
    return http.get(`record/detail/${record_id}`);
  },
  
  /**
   * 更新记账记录
   * @param {Object} data - 更新数据
   * @param {Number} data.record_id - 记录ID
   * @param {String} [data.category] - 分类代码
   * @param {Number} [data.amount] - 金额
   * @param {String} [data.description] - 备注
   * @param {String} [data.record_time] - 记账时间（格式：YYYY-MM-DD HH:mm:ss）
   * @returns {Promise}
   */
  updateRecord(data) {
    return http.put('record/update', data);
  },
  
  /**
   * 删除记账记录
   * @param {Number} record_id - 记录ID
   * @returns {Promise}
   */
  deleteRecord(record_id) {
    return http.delete(`record/delete/${record_id}`);
  },
  
  /**
   * 批量删除记账记录
   * @param {Array} record_ids - 记录ID数组
   * @returns {Promise}
   */
  batchDeleteRecords(record_ids) {
    return http.delete('record/batch-delete', { data: { record_ids } });
  }
};

export default recordApi; 
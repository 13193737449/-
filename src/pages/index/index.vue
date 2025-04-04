<template>
  <view class="home-container">
    <!-- 头部区域 -->
    <view class="header-section">
      <text class="page-title">花哪儿了</text>
    </view>

    <!-- 分类筛选区域 -->
    <view class="filter-area">
      <view class="total-expense">
        <text class="expense-label">本月总支出</text>
        <text class="expense-amount">¥{{ getTotalMonthExpense() }}</text>
      </view>
      <view class="filter-row">
        <view class="filter-item">
          <text class="filter-label">类型</text>
          <view class="filter-value" @tap="toggleCategoryFilter">
            <text class="value-text">{{ currentCategoryFilter.label }}</text>
            <text class="value-arrow">▼</text>
          </view>
        </view>
        <view class="filter-item">
          <text class="filter-label">年月</text>
          <view class="filter-value" @tap="showDatePickerModal">
            <text class="value-text">{{ selectedYear }}年{{ selectedMonth }}月</text>
            <text class="value-arrow">▼</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 分类筛选下拉菜单 -->
    <view class="category-filter-dropdown" v-if="showCategoryFilter">
      <view class="dropdown-mask" @tap="toggleCategoryFilter"></view>
      <view class="dropdown-content">
        <view 
          class="dropdown-item" 
          :class="{'active': currentCategoryFilter.value === filter.value}"
          v-for="filter in categoryFilters" 
          :key="filter.value"
          @tap="selectCategoryFilter(filter)"
        >
          <text class="dropdown-text">{{ filter.label }}</text>
          <text class="dropdown-check" v-if="currentCategoryFilter.value === filter.value">✓</text>
        </view>
      </view>
    </view>
    
    <!-- 年月选择器弹窗 -->
    <view class="date-picker-modal" v-if="showDatePicker">
      <view class="modal-mask" @tap="hideDatePicker"></view>
      <view class="date-picker-content">
        <view class="date-picker-header">
          <text class="modal-close" @tap="hideDatePicker">取消</text>
          <text class="date-picker-title">选择年月</text>
          <text class="modal-confirm" @tap="confirmDateSelection">确定</text>
        </view>
        <view class="date-picker-tabs">
          <view class="tab-item" :class="{'active': pickerMode === 'year'}" @tap="pickerMode = 'year'">年份</view>
          <view class="tab-item" :class="{'active': pickerMode === 'month'}" @tap="pickerMode = 'month'">月份</view>
        </view>
        <view class="date-picker-body">
          <!-- 年份选择 -->
          <scroll-view 
            v-if="pickerMode === 'year'" 
            class="year-grid" 
            scroll-y="true"
            :scroll-top="yearScrollTop"
            show-scrollbar="false"
          >
            <view class="year-row">
              <view 
                class="year-item" 
                v-for="year in years" 
                :key="year"
                :class="{'active': tempYear === year}"
                @tap="selectYear(year)"
              >
                {{ year }}
              </view>
            </view>
          </scroll-view>
          
          <!-- 月份选择 -->
          <view v-if="pickerMode === 'month'" class="month-grid">
            <view 
              class="month-item" 
              v-for="month in months" 
              :key="month"
              :class="{'active': tempMonth === month}"
              @tap="selectMonth(month)"
            >
              {{ month }}月
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 记录列表 -->
    <scroll-view 
      class="records-container" 
      scroll-y="true"
      show-scrollbar="false"
      :scroll-with-animation="true"
    >
      <!-- 加载状态 -->
      <view class="loading-container" v-if="isLoading">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
      
      <!-- 正常内容 -->
      <template v-else>
        <!-- 按日期分组的记录列表 -->
        <view 
          class="day-group" 
          v-for="(records, date) in filteredRecords" 
          :key="date"
        >
          <view class="day-header">
            <text class="day-date">{{ formatDateHeader(new Date(date)) }}</text>
            <text class="day-expense">-{{ getTotalExpense(records) }}</text>
          </view>

          <view 
            class="record-item-container" 
            v-for="(record, index) in records" 
            :key="record.id"
            @touchstart="handleTouchStart(date, index, $event)"
            @touchend="handleTouchEnd"
            @touchcancel="handleTouchEnd"
            @touchmove="handleTouchMove"
          >
            <view class="record-item-wrapper">
              <view class="record-item">
                <view class="record-icon" :class="getCategoryClass(record.categoryValue)">
                  <text class="icon-emoji">{{ record.icon }}</text>
                </view>
                <view class="record-details">
                  <view class="record-top">
                    <text class="record-category">{{ record.category }}</text>
                    <text class="record-amount expense">-{{ record.amount.toFixed(2) }}</text>
                  </view>
                  <view class="record-bottom">
                    <text class="record-desc">{{ record.remark }}</text>
                    <text class="record-time">{{ formatTime(record.time) }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 无记录提示 -->
        <view class="no-record-tips" v-if="Object.keys(filteredRecords).length === 0">
          <text class="tips-text">没有符合条件的记录</text>
  </view>
</template>
    </scroll-view>

    <!-- 添加按钮 -->
    <view class="add-record-btn" @tap="showAddRecord">
      <text class="add-record-icon">+</text>
      <text class="add-record-text">记一笔</text>
    </view>

    <!-- 用户信息和退出按钮 -->
    <view class="user-info-container">
      <view class="user-info">
        <text class="user-avatar">👤</text>
        <text class="username">{{ username }}</text>
      </view>
      <view class="logout-btn" @tap="showLogoutConfirm">
        <text class="logout-icon">🚪</text>
        <text class="logout-text">退出</text>
      </view>
    </view>

    <!-- 添加记录弹窗 -->
    <view class="add-record-modal" v-if="isAddRecordVisible">
      <view class="modal-mask" @tap="hideAddRecord"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">添加记录</text>
          <text class="modal-close" @tap="hideAddRecord">✕</text>
        </view>
        <view class="modal-body">
          <!-- 类型选择 -->
          <view class="input-group">
            <text class="input-label">类型</text>
            <view class="category-selector">
              <view 
                class="category-item" 
                :class="{'active': selectedCategory === category.value}"
                v-for="category in categories" 
                :key="category.value"
                @tap="selectedCategory = category.value"
              >
                <view class="category-icon" :class="getCategoryClass(category.value)">
                  <text class="category-emoji">{{category.icon}}</text>
                </view>
                <text class="category-text">{{category.label}}</text>
              </view>
            </view>
          </view>
          
          <!-- 金额输入 -->
          <view class="input-group">
            <text class="input-label">金额</text>
            <view class="amount-input-wrapper">
              <text class="amount-prefix">¥</text>
              <input 
                class="amount-input" 
                type="digit" 
                v-model="amount" 
                placeholder="0.00"
                focus
              />
            </view>
          </view>
          
          <!-- 日期选择 -->
          <view class="input-group">
            <text class="input-label">日期</text>
            <view class="date-input-wrapper">
              <view class="date-picker-value" @tap="openFullDatePicker">
                <text>{{ recordDate }}</text>
                <text class="date-picker-arrow">▼</text>
              </view>
            </view>
          </view>
          
          <!-- 备注输入 -->
          <view class="input-group">
            <text class="input-label">备注</text>
            <input 
              class="remark-input" 
              type="text" 
              v-model="remark" 
              placeholder="添加备注"
            />
          </view>
        </view>
        
        <view class="modal-footer">
          <button class="cancel-btn" @tap="hideAddRecord">取消</button>
          <button class="confirm-btn" :disabled="!canSave" @tap="saveRecord">保存</button>
        </view>
      </view>
    </view>

    <!-- 自定义日期选择器弹窗 -->
    <view class="date-picker-modal" v-if="showFullDatePicker">
      <view class="modal-mask" @tap="hideFullDatePicker"></view>
      <view class="date-picker-content">
        <view class="date-picker-header">
          <text class="modal-close" @tap="hideFullDatePicker">取消</text>
          <text class="date-picker-title">选择日期</text>
          <text class="modal-confirm" @tap="confirmFullDate">确定</text>
        </view>
        <view class="date-picker-tabs">
          <view class="tab-item" :class="{'active': fullDateMode === 'year'}" @tap="fullDateMode = 'year'">年份</view>
          <view class="tab-item" :class="{'active': fullDateMode === 'month'}" @tap="fullDateMode = 'month'">月份</view>
          <view class="tab-item" :class="{'active': fullDateMode === 'day'}" @tap="fullDateMode = 'day'">日期</view>
        </view>
        <view class="date-picker-body">
          <!-- 年份选择 -->
          <scroll-view 
            v-if="fullDateMode === 'year'" 
            class="year-grid" 
            scroll-y="true"
            show-scrollbar="false"
          >
            <view class="year-row">
              <view 
                class="year-item" 
                v-for="year in recordYears" 
                :key="year"
                :class="{'active': tempFullYear === year}"
                @tap="selectFullYear(year)"
              >
                {{ year }}
              </view>
            </view>
          </scroll-view>
          
          <!-- 月份选择 -->
          <view v-if="fullDateMode === 'month'" class="month-grid">
            <view 
              class="month-item" 
              v-for="month in months" 
              :key="month"
              :class="{'active': tempFullMonth === month}"
              @tap="selectFullMonth(month)"
            >
              {{ month }}月
            </view>
          </view>
          
          <!-- 日期选择 -->
          <view v-if="fullDateMode === 'day'" class="day-grid">
            <scroll-view class="day-scroll" scroll-y="true" show-scrollbar="false">
              <view class="day-row">
                <view 
                  class="day-item" 
                  v-for="day in fullDateDays" 
                  :key="day"
                  :class="{'active': tempFullDay === day}"
                  @tap="selectFullDay(day)"
                >
                  {{ day }}日
                </view>
              </view>
            </scroll-view>
          </view>
        </view>
      </view>
    </view>

    <!-- 长按操作弹窗 -->
    <view class="action-modal" v-if="showActionModal">
      <view class="modal-mask" @tap="hideActions"></view>
      <view class="action-menu">
        <view class="action-item edit" @tap="editRecord(activeGroup, activeIndex)">
          <text class="action-icon">✎</text>
          <text class="action-text">修改</text>
        </view>
        <view class="action-item delete" @tap="deleteRecord(activeGroup, activeIndex)">
          <text class="action-icon">×</text>
          <text class="action-text">删除</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, reactive, onBeforeMount } from 'vue';
import { userApi, recordApi } from '@/api';

// 获取当前年份和日期，用于限制日期选择器范围
const currentYear = new Date().getFullYear();
const currentDate = new Date();

// 用户信息
const username = ref('张三');

// 添加记录相关变量
const isAddRecordVisible = ref(false);
const selectedCategory = ref(1); // 默认餐饮类别
const amount = ref('');
const remark = ref('');

// 日期选择相关变量
const recordDate = ref(currentDate.toISOString().slice(0, 10));
const showRecordDatePicker = ref(false);
const recordPickerMode = ref('year'); // 'year', 'month', 或 'day'
const recordYearScrollTop = ref(0);

// 临时日期变量
const tempRecordYear = ref(currentDate.getFullYear());
const tempRecordMonth = ref(currentDate.getMonth() + 1);
const tempRecordDay = ref(currentDate.getDate());

// 分类筛选相关变量
const showCategoryFilter = ref(false);
const categoryFilters = [
  { label: '全部类型', value: 'all' },
  { label: '餐饮', value: 1 },
  { label: '交通', value: 2 },
  { label: '购物', value: 3 },
  { label: '娱乐', value: 4 },
  { label: '居住', value: 5 },
  { label: '医疗', value: 6 },
  { label: '教育', value: 7 },
  { label: '其他', value: 8 }
];
const currentCategoryFilter = ref(categoryFilters[0]);

// 年月选择器变量
const showDatePicker = ref(false);
const selectedYear = ref(currentDate.getFullYear());
const selectedMonth = ref(currentDate.getMonth() + 1);
const tempYear = ref(currentDate.getFullYear());
const tempMonth = ref(currentDate.getMonth() + 1);
const pickerMode = ref('year'); // 'year' 或 'month'
const yearScrollTop = ref(0);

// 生成年份范围(当前年-5年到当前年)，倒序排列
const generateYears = () => {
  const currentYear = new Date().getFullYear();
  const years = [];
  for (let i = 0; i <= 5; i++) {
    years.push(currentYear - i);
  }
  return years;
};
const years = generateYears();

// 生成月份范围(1-12)
const months = Array.from({length: 12}, (_, i) => i + 1);

// 更新日期筛选器
const currentDateFilter = computed(() => {
  const formatted = `${selectedYear.value}-${selectedMonth.value.toString().padStart(2, '0')}`;
  return { label: `${selectedYear.value}年${selectedMonth.value}月`, value: formatted };
});

// 可选类别
const categories = [
  { label: '餐饮', value: 1, icon: '🍜' },
  { label: '交通', value: 2, icon: '🚇' },
  { label: '购物', value: 3, icon: '🛒' },
  { label: '娱乐', value: 4, icon: '🎮' },
  { label: '居住', value: 5, icon: '🏠' },
  { label: '医疗', value: 6, icon: '💊' },
  { label: '教育', value: 7, icon: '📚' },
  { label: '其他', value: 8, icon: '📦' }
];

// 在页面加载前检查登录状态
onBeforeMount(() => {
  // 仅在onBeforeMount中检查一次登录状态
  checkLoginStatus();
});

// 页面加载时获取数据
onMounted(() => {
  // 获取用户信息
  getUserInfo();
  
  // 获取账单记录
  getRecordsData();
});

// 检查登录状态
const checkLoginStatus = () => {
  console.log('检查登录状态...');
  const token = uni.getStorageSync('token');
  
  if (!token) {
    console.log('未登录，跳转到登录页');
    // 未登录，跳转到登录页
    const currentPages = getCurrentPages();
    if (currentPages.length && currentPages[currentPages.length - 1].route !== 'pages/login/login') {
      uni.reLaunch({
        url: '/pages/login/login'
      });
    }
    return false;
  }
  
  console.log('已登录，继续加载页面');
  return true;
};

// 获取用户信息
const getUserInfo = () => {
  // 如果页面已加载，说明用户已登录，无需重复检查
  // 先尝试从本地存储获取用户信息
  const userInfo = uni.getStorageSync('userInfo');
  if (userInfo) {
    try {
      const parsedUserInfo = typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo;
      username.value = parsedUserInfo.username || '用户';
      console.log('从本地存储获取用户名:', username.value);
    } catch (e) {
      console.error('解析用户信息失败:', e);
      username.value = '用户';
    }
  } else {
    // 如果本地没有，则从API获取
    userApi.getUserInfo()
      .then(res => {
        if (res.code === 200 && res.data) {
          username.value = res.data.username || '用户';
          console.log('从API获取用户名:', username.value);
          
          // 将用户信息存储到本地
          uni.setStorageSync('userInfo', JSON.stringify(res.data));
        } else {
          username.value = '用户';
          console.warn('获取用户信息响应格式有误:', res);
        }
      })
      .catch(error => {
        console.error('获取用户信息失败:', error);
        username.value = '用户';
      });
  }
};

// 获取记录数据
const getRecordsData = () => {
  // 页面已加载，无需重复检查登录状态
  isLoading.value = true;
  
  // 构建查询参数
  const params = {
    page: 1,
    size: 20,
    startDate: `${selectedYear.value}-${selectedMonth.value.toString().padStart(2, '0')}-01`,
    endDate: getLastDayOfMonth(selectedYear.value, selectedMonth.value)
  };
  
  // 如果用户选择了特定分类，添加分类查询参数
  // 后端已不再根据分类代码筛选记录，因此无需添加category参数
  // 前端将根据返回的所有记录在UI上进行筛选
  
  console.log('查询参数:', params);
  
  // 调用记录API
  recordApi.getRecordList(params)
    .then(res => {
      console.log('获取记录成功:', res);
      // 处理API返回的数据
      processRecordsData(res.data || []);
      isLoading.value = false;
    })
    .catch(error => {
      console.error('获取记录失败:', error);
      isLoading.value = false;
      // 清空数据
      recordsData.value = { today: [], yesterday: [], other: [] };
    });
};

// 获取月份最后一天
const getLastDayOfMonth = (year, month) => {
  const lastDay = new Date(year, month, 0).getDate();
  return `${year}-${month.toString().padStart(2, '0')}-${lastDay.toString().padStart(2, '0')}`;
};

// 根据分类代码获取分类ID
const getCategoryIdByCode = (categoryCode) => {
  if (!categoryCode) return 8; // 默认返回"其他"分类ID
  
  console.log(`尝试将分类代码 [${categoryCode}] 转换为ID`);
  
  const categoryMap = {
    'food': 1,
    'transport': 2,
    'shopping': 3,
    'entertainment': 4,
    'housing': 5,
    'medical': 6,
    'education': 7,
    'other': 8
  };
  
  // 先尝试直接匹配
  const id = categoryMap[categoryCode.toLowerCase()];
  
  // 如果没有找到匹配项，再尝试部分匹配
  if (id === undefined) {
    console.log(`没有找到精确匹配 [${categoryCode}]，尝试部分匹配`);
    for (const [code, value] of Object.entries(categoryMap)) {
      if (categoryCode.toLowerCase().includes(code) || code.includes(categoryCode.toLowerCase())) {
        console.log(`找到部分匹配: ${code} -> ${value}`);
        return value;
      }
    }
    console.log(`未找到任何匹配，返回默认值8`);
    return 8; // 默认返回"其他"分类ID
  }
  
  console.log(`找到精确匹配: ${categoryCode} -> ${id}`);
  return id;
};

// 根据分类代码获取图标
const getCategoryIcon = (categoryCode) => {
  const iconMap = {
    'food': '🍜',
    'transport': '🚇',
    'shopping': '🛒',
    'entertainment': '🎮',
    'housing': '🏠',
    'medical': '💊',
    'education': '📚',
    'other': '📦'
  };
  
  return iconMap[categoryCode] || '📦'; // 默认返回"其他"分类图标
};

// 格式化日期为YYYY-MM-DD
const formatDateString = (date) => {
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// 按时间排序
const sortByTime = (a, b) => {
  // 先按日期
  const dateCompare = b.date - a.date;
  if (dateCompare !== 0) return dateCompare;
  
  // 相同日期按时间
  return b.time.localeCompare(a.time);
};

// 添加加载状态
const isLoading = ref(true);

// 实际数据初始状态为空
const recordsData = ref({
  today: [],
  yesterday: [],
  other: []
});

// 根据筛选条件过滤记录
const filteredRecords = computed(() => {
  if (isLoading.value) {
    return {};
  }
  
  console.log('当前筛选条件:', currentCategoryFilter.value);
  console.log('所有记录数据:', recordsData.value);
  
  const result = {};
  
  // 如果选择"全部类型"，直接返回所有记录
  if (currentCategoryFilter.value.value === 'all') {
    console.log('选择了全部类型，返回所有记录');
    return recordsData.value;
  }
  
  // 获取用户选择的分类ID，确保它是数字
  const selectedCategoryId = parseInt(currentCategoryFilter.value.value);
  
  console.log('筛选分类ID:', selectedCategoryId);
  
  // 对每个日期组进行筛选
  Object.keys(recordsData.value).forEach(date => {
    const filteredGroup = recordsData.value[date].filter(record => {
      const match = parseInt(record.categoryValue) === selectedCategoryId;
      console.log(`${date} 记录 ID:${record.id}, 分类:${record.category}, 值:${record.categoryValue}, 匹配:${match}`);
      return match;
    });
    
    // 只有当筛选结果有记录时，才添加到结果中
    if (filteredGroup.length > 0) {
      result[date] = filteredGroup;
    }
  });
  
  console.log('筛选结果:', Object.keys(result).map(date => ({
    date, 
    count: result[date].length
  })));
  
  return result;
});

// 计算当前选中月份的总支出
const getTotalMonthExpense = () => {
  if (isLoading.value) {
    return '0.00';
  }
  
  // 将所有日期分组中的记录合并为一个数组
  const allRecords = [];
  Object.keys(recordsData.value).forEach(date => {
    allRecords.push(...recordsData.value[date]);
  });
  
  // 计算所有记录的总金额
  const total = allRecords.reduce((sum, record) => sum + record.amount, 0);
    
  return total.toFixed(2);
};

// 计算指定记录列表的总支出
const getTotalExpense = (records) => {
  if (!records || records.length === 0) {
    return '0.00';
  }
  
  const total = records.reduce((sum, record) => sum + record.amount, 0);
  return total.toFixed(2);
};

// 格式化时间
const formatTime = (time) => {
  return time;
};

// 获取昨天日期
const getYesterday = () => {
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  return yesterday;
};

// 格式化日期标题
const formatDateHeader = (date) => {
  if (!date) return '';
  
  const today = new Date();
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  
  const isToday = formatDateString(date) === formatDateString(today);
  const isYesterday = formatDateString(date) === formatDateString(yesterday);
  
  const month = (date.getMonth() + 1).toString();
  const day = date.getDate().toString();
  
  if (isToday) {
    return `今天 (${month}月${day}日)`;
  } else if (isYesterday) {
    return `昨天 (${month}月${day}日)`;
  } else {
    return `${month}月${day}日`;
  }
};

// 根据分类ID获取CSS类名
const getCategoryClass = (categoryId) => {
  const categoryMap = {
    1: 'food',
    2: 'transport',
    3: 'shopping',
    4: 'entertainment',
    5: 'housing',
    6: 'medical',
    7: 'education',
    8: 'other'
  };
  
  return categoryMap[categoryId] || 'other';
};

// 切换分类筛选显示
const toggleCategoryFilter = () => {
  showCategoryFilter.value = !showCategoryFilter.value;
  // 关闭另一个筛选
  if (showCategoryFilter.value) {
    showDatePicker.value = false;
  }
};

// 选择分类筛选
const selectCategoryFilter = (filter) => {
  currentCategoryFilter.value = filter;
  showCategoryFilter.value = false;
  // 我们不需要重新获取数据，只需要对现有数据进行前端筛选
  console.log('选择了新的筛选分类:', filter);
};

// 判断是否可以保存记录
const canSave = computed(() => {
  return selectedCategory.value && amount.value && parseFloat(amount.value) > 0;
});

// 显示添加记录弹窗
const showAddRecord = () => {
  isAddRecordVisible.value = true;
};

// 长按操作相关变量
const showActionModal = ref(false);
const activeGroup = ref('');
const activeIndex = ref(-1);
const longPressTimer = ref(null);
const longPressDuration = 500; // 0.5秒

// 触摸开始
const handleTouchStart = (group, index, event) => {
  // 清除之前的定时器
  if (longPressTimer.value) {
    clearTimeout(longPressTimer.value);
  }
  
  // 设置新的定时器，1秒后触发长按事件
  longPressTimer.value = setTimeout(() => {
    showActions(group, index);
  }, longPressDuration);
};

// 触摸结束或取消
const handleTouchEnd = () => {
  // 清除定时器
  if (longPressTimer.value) {
    clearTimeout(longPressTimer.value);
    longPressTimer.value = null;
  }
};

// 触摸移动
const handleTouchMove = () => {
  // 如果手指移动，也清除定时器，防止误触发
  handleTouchEnd();
};

// 显示操作菜单
const showActions = (group, index) => {
  activeGroup.value = group;
  activeIndex.value = index;
  showActionModal.value = true;
  
  // 触发震动反馈
  if (uni.vibrateShort) {
    uni.vibrateShort({
      success: () => {
        console.log('震动成功');
      }
    });
  }
};

// 隐藏操作菜单
const hideActions = () => {
  showActionModal.value = false;
  activeGroup.value = '';
  activeIndex.value = -1;
};

// 修改记录
const editRecord = (dateKey, index) => {
  if (!recordsData.value[dateKey] || !recordsData.value[dateKey][index]) return;
  
  const record = recordsData.value[dateKey][index];
  
  // 设置编辑表单的值
  selectedCategory.value = record.categoryValue;
  amount.value = record.amount.toString();
  remark.value = record.remark;
  
  // 将日期转换为YYYY-MM-DD格式
  const date = record.date;
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  recordDate.value = `${year}-${month}-${day}`;
  
  // 记录当前编辑的项目，用于保存时更新
  currentEditGroup.value = dateKey;
  currentEditIndex.value = index;
  
  // 显示编辑弹窗并隐藏操作菜单
  isAddRecordVisible.value = true;
  isEditMode.value = true;
  showActionModal.value = false;
};

// 删除记录
const deleteRecord = (dateKey, index) => {
  // 检查是否已登录
  if (!checkLoginStatus()) return;
  
  if (!recordsData.value[dateKey] || !recordsData.value[dateKey][index]) return;
  
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这条记录吗？',
    success: (res) => {
      if (res.confirm) {
        const recordId = recordsData.value[dateKey][index].id;
        
        if (recordId) {
          uni.showLoading({
            title: '删除中...'
          });
          
          recordApi.deleteRecord(recordId)
            .then(res => {
              uni.hideLoading();
              
              // 更新本地数据
              recordsData.value[dateKey].splice(index, 1);
              
              // 如果该日期下没有记录了，删除整个日期键
              if (recordsData.value[dateKey].length === 0) {
                delete recordsData.value[dateKey];
              }
              
              uni.showToast({
                title: '删除成功',
                icon: 'success'
              });
            })
            .catch(error => {
              uni.hideLoading();
              console.error('删除记录失败:', error);
              
              uni.showToast({
                title: '删除失败，请重试',
                icon: 'none'
              });
            });
        }
      }
      
      // 无论用户是否确认，都隐藏操作菜单
      showActionModal.value = false;
    }
  });
};

// 编辑模式相关变量
const isEditMode = ref(false);
const currentEditGroup = ref('');
const currentEditIndex = ref(-1);

// 保存记录 (修改现有方法)
const saveRecord = () => {
  // 检查是否已登录
  if (!checkLoginStatus()) return;
  
  // 检查输入
  if (!canSave.value) {
    uni.showToast({
      title: '请输入有效金额',
      icon: 'none'
    });
    return;
  }
  
  // 获取选中的分类
  const category = categories.find(c => c.value === selectedCategory.value);
  if (!category) {
    uni.showToast({
      title: '请选择有效分类',
      icon: 'none'
    });
    return;
  }
  
  // 将yyyy-MM-dd格式的日期转换为yyyy-MM-dd HH:mm:ss格式
  const recordDateTime = recordDate.value + ' ' + new Date().toTimeString().slice(0, 8);

  // 获取用户ID
  const userInfo = uni.getStorageSync('userInfo');
  let userId = null;
  if (userInfo) {
    try {
      const parsedUserInfo = typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo;
      userId = parsedUserInfo.userId;
    } catch (e) {
      console.error('解析用户信息失败:', e);
    }
  }

  if (!userId) {
    uni.showToast({
      title: '用户信息获取失败，请重新登录',
      icon: 'none'
    });
    return;
  }
  
  // 获取分类代码
  const categoryCode = getCategoryCode(category.value);
  
  // 构建记录数据 - 所有字段均按照后端要求严格命名
  const recordData = {
    user_id: userId,
    category: categoryCode, // 使用分类代码
    amount: parseFloat(amount.value),
    description: remark.value || category.label,
    record_time: recordDateTime
  };
  
  // 如果是编辑模式，添加记录ID
  if (isEditMode.value) {
    const dateKey = currentEditGroup.value;
    const index = currentEditIndex.value;
    
    if (recordsData.value[dateKey] && recordsData.value[dateKey][index]) {
      const editRecord = recordsData.value[dateKey][index];
      if (editRecord && editRecord.id) {
        recordData.record_id = editRecord.id;
      }
    }
  }
  
  console.log('发送记录数据:', recordData);
  
  uni.showLoading({
    title: isEditMode.value ? '更新中...' : '保存中...'
  });
  
  // 调用API保存记录
  const apiCall = isEditMode.value 
    ? recordApi.updateRecord(recordData) 
    : recordApi.addRecord(recordData);
  
  apiCall.then(res => {
    uni.hideLoading();
    
    console.log('记录保存成功:', res);
    
    uni.showToast({
      title: isEditMode.value ? '修改成功' : '添加成功',
      icon: 'success'
    });
    
    // 重新加载记录数据
    getRecordsData();
    
    // 隐藏弹窗并重置表单
    hideAddRecord();
  }).catch(error => {
    uni.hideLoading();
    console.error(isEditMode.value ? '修改记录失败:' : '添加记录失败:', error);
    
    // 显示具体错误信息
    let errorMsg = '操作失败，请重试';
    if (error.response && error.response.data && error.response.data.message) {
      errorMsg = error.response.data.message;
    } else if (error.message) {
      errorMsg = error.message;
    }
    
    uni.showToast({
      title: errorMsg,
      icon: 'none',
      duration: 3000
    });
  });
};

// 隐藏添加记录弹窗 (修改现有方法)
const hideAddRecord = () => {
  isAddRecordVisible.value = false;
  isEditMode.value = false;
  currentEditGroup.value = '';
  currentEditIndex.value = -1;
  
  // 重置表单
  selectedCategory.value = 1;
  amount.value = '';
  remark.value = '';
  recordDate.value = currentDate.toISOString().slice(0, 10);
};

// 显示年月选择器
const showDatePickerModal = () => {
  tempYear.value = selectedYear.value;
  tempMonth.value = selectedMonth.value;
  pickerMode.value = 'year';
  showDatePicker.value = true;
  showCategoryFilter.value = false;
  
  // 计算年份滚动位置，使选中的年份居中显示
  const yearIndex = years.findIndex(y => y === tempYear.value);
  if (yearIndex > -1) {
    // 延迟设置是为了确保DOM已渲染
    setTimeout(() => {
      yearScrollTop.value = Math.max(0, (yearIndex - 2) * 100);
    }, 100);
  }
};

// 选择年份
const selectYear = (year) => {
  tempYear.value = year;
  pickerMode.value = 'month';
};

// 选择月份
const selectMonth = (month) => {
  tempMonth.value = month;
  // 直接确认选择
  confirmDateSelection();
};

// 隐藏年月选择器
const hideDatePicker = () => {
  showDatePicker.value = false;
};

// 确认年月选择
const confirmDateSelection = () => {
  selectedYear.value = tempYear.value;
  selectedMonth.value = tempMonth.value;
  showDatePicker.value = false;
  
  // 在这里触发加载对应年月的数据
  console.log(`已选择: ${selectedYear.value}年${selectedMonth.value}月`);
  getRecordsData();
};

// 完整日期选择变量
const showFullDatePicker = ref(false);
const fullDateMode = ref('year');
const tempFullYear = ref(new Date().getFullYear());
const tempFullMonth = ref(new Date().getMonth() + 1);
const tempFullDay = ref(new Date().getDate());

// 计算完整日期的天数
const fullDateDays = computed(() => {
  const daysInMonth = new Date(tempFullYear.value, tempFullMonth.value, 0).getDate();
  return Array.from({length: daysInMonth}, (_, i) => i + 1);
});

// 显示完整日期选择器
const openFullDatePicker = () => {
  // 解析当前日期
  const [year, month, day] = recordDate.value.split('-').map(Number);
  tempFullYear.value = year;
  tempFullMonth.value = month;
  tempFullDay.value = day;
  
  fullDateMode.value = 'year';
  showFullDatePicker.value = true;
};

// 隐藏完整日期选择器
const hideFullDatePicker = () => {
  showFullDatePicker.value = false;
};

// 选择年份
const selectFullYear = (year) => {
  tempFullYear.value = year;
  fullDateMode.value = 'month';
};

// 选择月份
const selectFullMonth = (month) => {
  tempFullMonth.value = month;
  fullDateMode.value = 'day';
};

// 选择日期
const selectFullDay = (day) => {
  tempFullDay.value = day;
  confirmFullDate();
};

// 确认完整日期
const confirmFullDate = () => {
  // 格式化日期
  const year = tempFullYear.value;
  const month = tempFullMonth.value.toString().padStart(2, '0');
  const day = tempFullDay.value.toString().padStart(2, '0');
  recordDate.value = `${year}-${month}-${day}`;
  
  showFullDatePicker.value = false;
};

// 生成年份范围(2020-当前年份)，倒序排列
const recordYears = Array.from({length: currentYear - 2020 + 1}, (_, i) => currentYear - i);

// 计算当前选择月份的天数
const recordDays = computed(() => {
  const daysInMonth = new Date(tempRecordYear.value, tempRecordMonth.value, 0).getDate();
  return Array.from({length: daysInMonth}, (_, i) => i + 1);
});

// 显示日期选择器
const handleShowRecordDatePicker = () => {
  // 这个函数可以保留但改为使用原生picker
};

// 隐藏日期选择器
const hideRecordDatePicker = () => {
  // 这个函数可以保留但不再使用
};

const selectRecordYear = () => {};
const selectRecordMonth = () => {};
const selectRecordDay = () => {};
const confirmRecordDateSelection = () => {};

// 退出登录 (修改现有方法)
const showLogoutConfirm = () => {
  // 检查是否已登录
  if (!checkLoginStatus()) return;
  
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        // 执行退出操作
        uni.showLoading({
          title: '退出中...'
        });
        
        userApi.logout()
          .then(res => {
            uni.hideLoading();
            
            // 清除本地存储的token和用户信息
            uni.removeStorageSync('token');
            uni.removeStorageSync('userInfo');
            
            uni.showToast({
              title: '已退出登录',
              icon: 'success',
              duration: 2000,
              success: () => {
                // 延迟跳转到登录页
                setTimeout(() => {
                  uni.reLaunch({
                    url: '/pages/login/login'
                  });
                }, 1500);
              }
            });
          })
          .catch(error => {
            uni.hideLoading();
            console.error('退出登录失败:', error);
            
            // 即使API调用失败，也清除本地存储并跳转
            uni.removeStorageSync('token');
            uni.removeStorageSync('userInfo');
            
            uni.showToast({
              title: '已退出登录',
              icon: 'success',
              duration: 2000,
              success: () => {
                setTimeout(() => {
                  uni.reLaunch({
                    url: '/pages/login/login'
                  });
                }, 1500);
              }
            });
          });
      }
    }
  });
};

// 根据分类ID获取分类代码
const getCategoryCode = (categoryId) => {
  const categoryMap = {
    1: 'food',
    2: 'transport',
    3: 'shopping',
    4: 'entertainment',
    5: 'housing',
    6: 'medical',
    7: 'education',
    8: 'other'
  };
  
  return categoryMap[categoryId] || 'other';
};

// 处理记录数据
const processRecordsData = (data) => {
  // 如果是空数据，初始化为空记录
  if (!data || data.length === 0) {
    recordsData.value = {};
    return;
  }
  
  console.log('原始记录数据:', JSON.stringify(data));
  
  // 按日期分组的记录对象
  const groupedByDate = {};
  
  // 测试输出一下分类映射，确保映射正确
  console.log('分类代码映射:', {
    'food': 1,
    'transport': 2,
    'shopping': 3,
    'entertainment': 4,
    'housing': 5,
    'medical': 6,
    'education': 7,
    'other': 8
  });
  
  // 处理API返回的数据并分组
  data.forEach((record, index) => {
    try {
      console.log(`处理第${index+1}条记录:`, JSON.stringify(record));
      
      // 根据record_time获取日期部分
      const recordTime = record.record_time || '';
      if (!recordTime) {
        console.error('记录没有时间字段:', record);
        return;
      }
      
      const recordDate = recordTime.split(' ')[0];
      
      // 直接根据category确定分类ID
      let categoryName = '';
      let categoryValue = 0;
      
      // 根据category确定分类ID和名称
      if (record.category) {
        console.log(`记录的category值: ${record.category}`);
        
        // 映射category到ID
        const categoryMap = {
          'food': 1,
          'transport': 2,
          'shopping': 3,
          'entertainment': 4,
          'housing': 5,
          'medical': 6,
          'education': 7,
          'other': 8
        };
        
        // 直接映射
        categoryValue = categoryMap[record.category.toLowerCase()] || 8;
        
        // 查找分类名称
        for (const category of categories) {
          if (category.value === categoryValue) {
            categoryName = category.label;
            break;
          }
        }
        
        if (!categoryName) {
          categoryName = record.category; // 如果没找到，使用原始category作为名称
        }
        
        console.log(`解析后的分类名称: ${categoryName}, 分类ID: ${categoryValue}`);
      }
      
      // 日期对象
      const dateObj = new Date(recordDate);
      
      // 创建格式化后的记录对象
      const formattedRecord = {
        id: record.record_id,
        category: categoryName,
        categoryValue: categoryValue,
        icon: getCategoryIcon(record.category),
        amount: parseFloat(record.amount),
        remark: record.description || '无备注',
        time: recordTime.split(' ')[1].substring(0, 5),
        date: dateObj
      };
      
      console.log('格式化后的记录:', formattedRecord);
      
      // 使用日期作为分组键
      if (!groupedByDate[recordDate]) {
        groupedByDate[recordDate] = [];
      }
      
      // 添加记录到对应日期
      groupedByDate[recordDate].push(formattedRecord);
      console.log(`添加到 ${recordDate} 组`);
    } catch (error) {
      console.error('处理记录出错:', error, record);
    }
  });
  
  // 按日期降序排序（最新的日期在前）
  const sortedDates = Object.keys(groupedByDate).sort().reverse();
  
  // 创建最终分组
  const finalGroups = {};
  
  // 对每个日期组内的记录按时间排序
  sortedDates.forEach(date => {
    finalGroups[date] = groupedByDate[date].sort((a, b) => sortByTime(a, b));
  });
  
  // 更新响应式数据
  recordsData.value = finalGroups;
  
  console.log('处理后的所有记录数据:', Object.keys(finalGroups).map(date => ({
    date,
    count: finalGroups[date].length
  })));
};

// 格式化完整日期
const formatFullDate = (date) => {
  if (!date) return '';
  
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  return `${month}月${day}日 (${year})`;
};
</script>

<style lang="scss">
// 导入样式文件
@import "@/static/login/style.scss";

.home-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: $background-color;
  position: relative;
  background: linear-gradient(to bottom, #121212, #1f1f1f);
  overflow: hidden; /* 防止整个页面滚动 */
}

// 头部区域
.header-section {
  padding: 80rpx 30rpx 50rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(to right, rgba(0, 229, 255, 0.1), rgba(255, 0, 255, 0.1));
}

.header-section::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 2rpx;
  background: $gradient-primary;
  box-shadow: $glow-primary;
  animation: neonPulse 3s infinite;
}

.page-title {
  font-size: 56rpx;
  font-weight: 700;
  color: $text-dark;
  text-shadow: $glow-primary;
  letter-spacing: 2rpx;
  position: relative;
  text-transform: uppercase;
  animation: titleGlow 3s infinite alternate;
}

.page-title::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: -12rpx;
  width: 80%;
  height: 4rpx;
  background: $gradient-primary;
  transform: translateX(-50%);
  border-radius: 4rpx;
}

@keyframes titleGlow {
  0% { text-shadow: 0 0 5rpx rgba($primary-color, 0.5), 0 0 10rpx rgba($primary-color, 0.3); }
  100% { text-shadow: 0 0 15rpx rgba($primary-color, 0.8), 0 0 30rpx rgba($primary-color, 0.5), 0 0 50rpx rgba($secondary-color, 0.3); }
}

@keyframes neonPulse {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 0.4; }
}

// 分类筛选区域
.filter-area {
  margin: 0 30rpx 15rpx;
  background-color: rgba(255, 255, 255, 0.05);
  border-radius: $radius-medium;
  padding: 20rpx;
  border: 1rpx solid $border-color;
  box-shadow: $shadow-small;
}

.filter-row {
  display: flex;
  justify-content: space-between;
  margin-top: 15rpx;
  border-top: 1rpx solid $border-color;
  padding-top: 15rpx;
}

.filter-item {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.filter-label {
  font-size: 22rpx;
  color: $text-muted;
  margin-bottom: 5rpx;
}

.filter-value {
  display: flex;
  align-items: center;
}

.value-text {
  font-size: 28rpx;
  color: $text-dark;
  font-weight: 500;
}

.value-arrow {
  font-size: 22rpx;
  color: $primary-color;
  margin-left: 5rpx;
}

.total-expense {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 15rpx;
}

.expense-label {
  font-size: 22rpx;
  color: $text-muted;
  margin-bottom: 5rpx;
}

.expense-amount {
  font-size: 36rpx;
  font-weight: 600;
  color: $primary-color;
  text-shadow: $glow-primary;
}

// 记录列表区域
.records-container {
  flex: 1;
  background-color: $card-color;
  border-top-left-radius: 30rpx;
  border-top-right-radius: 30rpx;
  padding: 30rpx 0 150rpx 0; /* 增加底部内边距，避免内容被底部固定元素遮挡 */
  position: relative;
  box-shadow: 0 -8rpx 20rpx rgba(0, 0, 0, 0.3);
  height: calc(100vh - 360rpx); /* 调整高度，减去头部和筛选区域高度 */
  
  /* 隐藏滚动条，兼容不同浏览器 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
  &::-webkit-scrollbar {
    display: none; /* Chrome, Safari, Opera */
    width: 0;
    background: transparent;
  }
}

.records-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2rpx;
  background: $gradient-primary;
}

.day-group {
  margin-bottom: 30rpx;
  padding: 0 30rpx;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.day-date {
  font-size: 28rpx;
  color: $text-muted;
  font-weight: 500;
}

.day-expense {
  font-size: 26rpx;
  color: $text-muted;
}

.record-item-container {
  width: 100%;
  margin-bottom: 16rpx;
}

.record-item-wrapper {
  position: relative;
  width: 100%;
  border-radius: $radius-medium;
  box-shadow: $shadow-small;
  overflow: hidden;
}

.record-item {
  background-color: lighten($card-color, 5%);
  padding: 24rpx;
  display: flex;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
  border-radius: $radius-medium;
  border: 1rpx solid $border-color;
}

.record-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: $radius-circle;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  box-shadow: $shadow-small;
}

.food {
  background-color: #ea7a57;
}

.transport {
  background-color: #78cdd1;
}

.shopping {
  background-color: #e9b355;
}

.entertainment {
  background-color: #a891e9;
}

.housing {
  background-color: #7ac74f;
}

.medical {
  background-color: #ff87a3;
}

.education {
  background-color: #5a98de;
}

.other {
  background-color: #9e9e9e;
}

.icon-emoji {
  font-size: 36rpx;
}

.record-details {
  flex: 1;
}

.record-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8rpx;
}

.record-category {
  font-size: 30rpx;
  color: $text-dark;
  font-weight: 500;
}

.record-amount {
  font-size: 30rpx;
  font-weight: 600;
}

.expense {
  color: #ff6b6b;
}

.record-bottom {
  display: flex;
  justify-content: space-between;
}

.record-desc {
  font-size: 24rpx;
  color: $text-muted;
}

.record-time {
  font-size: 24rpx;
  color: $text-muted;
}

// 添加按钮样式
.add-record-btn {
  position: fixed;
  bottom: 100rpx;
  right: 30rpx;
  z-index: 51;
  background: $gradient-primary;
  display: flex;
  align-items: center;
  padding: 15rpx 25rpx;
  border-radius: 40rpx;
  box-shadow: $glow-primary;
}

.add-record-icon {
  font-size: 36rpx;
  color: $text-dark;
  font-weight: bold;
  margin-right: 8rpx;
}

.add-record-text {
  font-size: 28rpx;
  color: $text-dark;
  font-weight: 500;
}

// 无记录提示
.no-record-tips {
  padding: 100rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.tips-text {
  font-size: 28rpx;
  color: $text-muted;
}

// 添加记录弹窗样式
.add-record-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
}

.modal-content {
  position: relative;
  z-index: 101;
  background-color: $card-color;
  border-top-left-radius: 30rpx;
  border-top-right-radius: 30rpx;
  overflow: hidden;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  border-top: 1rpx solid $border-color;
  box-shadow: $glow-primary;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx;
  border-bottom: 1rpx solid $border-color;
  background: linear-gradient(to right, rgba($primary-color, 0.05), rgba($secondary-color, 0.05));
}

.modal-title {
  font-size: 32rpx;
  font-weight: 500;
  color: $text-dark;
  letter-spacing: 1rpx;
}

.modal-close {
  font-size: 30rpx;
  padding: 10rpx;
  color: $text-muted;
}

.modal-body {
  padding: 30rpx;
  flex: 1;
  overflow-y: auto;
  
  /* 隐藏滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
  &::-webkit-scrollbar {
    display: none; /* Chrome, Safari, Opera */
    width: 0;
    background: transparent;
  }
}

.input-group {
  margin-bottom: 30rpx;
}

.input-label {
  font-size: 28rpx;
  color: $text-muted;
  margin-bottom: 15rpx;
  display: block;
}

.category-selector {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -10rpx;
}

.category-item {
  width: 25%;
  padding: 10rpx;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20rpx;
}

.category-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: $radius-circle;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;
}

.category-item.active .category-icon {
  border-color: $primary-color;
  box-shadow: $glow-primary;
}

.category-emoji {
  font-size: 36rpx;
}

.category-text {
  font-size: 24rpx;
  color: $text-muted;
}

.category-item.active .category-text {
  color: $primary-color;
  font-weight: 500;
}

.amount-input-wrapper {
  display: flex;
  align-items: center;
  border-bottom: 1rpx solid $border-color;
  padding-bottom: 10rpx;
}

.amount-prefix {
  font-size: 40rpx;
  color: $text-dark;
  margin-right: 10rpx;
}

.amount-input {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 40rpx;
  color: $text-dark;
  background-color: transparent;
}

.remark-input {
  height: 80rpx;
  border-bottom: 1rpx solid $border-color;
  font-size: 30rpx;
  background-color: transparent;
  color: $text-dark;
}

.modal-footer {
  display: flex;
  padding: 20rpx;
  border-top: 1rpx solid $border-color;
}

.cancel-btn, .confirm-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  border-radius: 40rpx;
  margin: 0 10rpx;
  font-size: 28rpx;
}

.cancel-btn {
  background-color: rgba(255, 255, 255, 0.1);
  color: $text-muted;
  border: 1rpx solid $border-color;
}

.confirm-btn {
  background: $gradient-primary;
  color: $text-dark;
  box-shadow: $glow-primary;
}

.confirm-btn[disabled] {
  opacity: 0.5;
  box-shadow: none;
}

// 分类筛选下拉菜单样式
.category-filter-dropdown {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 60; /* 确保显示在其他元素之上 */
}

.dropdown-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.dropdown-content {
  position: absolute;
  top: 240rpx; /* 调整顶部距离，确保显示在筛选区域下方 */
  left: 30rpx;
  width: 300rpx;
  background-color: $card-color;
  border-radius: $radius-medium;
  overflow: hidden;
  box-shadow: $shadow-medium;
  border: 1rpx solid $border-color;
}

.dropdown-item {
  padding: 20rpx 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
}

.dropdown-item.active {
  background-color: rgba($primary-color, 0.2);
}

.dropdown-text {
  font-size: 28rpx;
  color: $text-dark;
}

.dropdown-item.active .dropdown-text {
  color: $primary-color;
  font-weight: 500;
  text-shadow: 0 0 5rpx rgba($primary-color, 0.5);
}

.dropdown-check {
  font-size: 28rpx;
  color: $primary-color;
}

// 年月选择器样式
.date-picker-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.date-picker-content {
  position: relative;
  z-index: 101;
  background-color: $card-color;
  border-top-left-radius: 30rpx;
  border-top-right-radius: 30rpx;
  overflow: hidden;
  border-top: 1rpx solid $border-color;
  box-shadow: $glow-primary;
}

.date-picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid $border-color;
  background: linear-gradient(to right, rgba($primary-color, 0.05), rgba($secondary-color, 0.05));
}

.date-picker-title {
  font-size: 32rpx;
  font-weight: 500;
  color: $text-dark;
  letter-spacing: 1rpx;
}

.modal-close, .modal-confirm {
  font-size: 30rpx;
  padding: 10rpx;
}

.modal-close {
  color: $text-muted;
}

.modal-confirm {
  color: $primary-color;
  font-weight: 500;
}

.date-picker-tabs {
  display: flex;
  border-bottom: 1rpx solid $border-color;
  background-color: $card-color;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
  color: $text-muted;
  position: relative;
}

.tab-item.active {
  color: $primary-color;
  font-weight: 500;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background-color: $primary-color;
  border-radius: 2rpx;
  box-shadow: $glow-primary;
}

.date-picker-body {
  height: 600rpx;
  position: relative;
  background-color: $card-color;
}

.year-grid, .day-scroll, .month-grid {
  height: 100%;
  width: 100%;
  
  /* 隐藏滚动条，兼容不同浏览器 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
  &::-webkit-scrollbar {
    display: none; /* Chrome, Safari, Opera */
    width: 0;
    background: transparent;
  }
}

.year-row {
  display: flex;
  flex-wrap: wrap;
  padding: 20rpx;
}

.year-item {
  width: 25%;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: $text-muted;
}

.year-item.active {
  color: $primary-color;
  font-weight: 500;
  background-color: rgba($primary-color, 0.15);
  border-radius: $radius-small;
  box-shadow: $glow-inner-primary;
}

.month-grid {
  display: flex;
  flex-wrap: wrap;
  height: 100%;
  padding: 40rpx 20rpx;
}

.month-item {
  width: 33.33%;
  height: 120rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: $text-muted;
}

.month-item.active {
  color: $primary-color;
  font-weight: 500;
  background-color: rgba($primary-color, 0.15);
  border-radius: $radius-small;
  box-shadow: $glow-inner-primary;
}

.date-input-wrapper {
  border-bottom: 1rpx solid $border-color;
  padding: 20rpx 0;
}

.date-picker-value {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 30rpx;
  color: $text-dark;
}

.date-picker-arrow {
  font-size: 22rpx;
  color: $text-muted;
}

.day-grid {
  height: 100%;
  width: 100%;
}

.day-scroll {
  height: 100%;
  width: 100%;
}

.day-row {
  display: flex;
  flex-wrap: wrap;
  padding: 20rpx;
}

.day-item {
  width: 25%;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: $text-muted;
  box-sizing: border-box;
  margin-bottom: 10rpx;
}

.day-item.active {
  color: $primary-color;
  font-weight: 500;
  background-color: rgba($primary-color, 0.15);
  border-radius: $radius-small;
  box-shadow: $glow-inner-primary;
}

// 长按操作弹窗样式
.action-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 90;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-menu {
  background-color: $card-color;
  border-radius: $radius-medium;
  overflow: hidden;
  width: 300rpx;
  position: relative;
  z-index: 91;
  box-shadow: $shadow-medium, $glow-primary;
  border: 1rpx solid $border-color;
}

.action-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid $border-color;
}

.action-item:last-child {
  border-bottom: none;
}

.action-icon {
  font-size: 36rpx;
  margin-right: 16rpx;
}

.action-text {
  font-size: 30rpx;
  font-weight: 500;
  color: $text-dark;
}

.action-item.edit {
  color: $primary-color;
}

.action-item.delete {
  color: #ff6b6b;
}

// 加载状态样式
.loading-container {
  padding: 40rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid rgba($text-dark, 0.1);
  border-top: 4rpx solid $primary-color;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
  box-shadow: $glow-primary;
}

.loading-text {
  font-size: 28rpx;
  color: $text-muted;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

// 快速添加区域样式移除
.quick-add {
  display: none;
}

.quick-add-btn {
  display: none;
}

.quick-add-icon {
  display: none;
}

.quick-add-text {
  display: none;
}

// 删除不需要的样式
.day-amounts {
  display: none;
}

.day-separator {
  display: none;
}

.day-income {
  display: none;
}

// 用户信息和退出按钮
.user-info-container {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 30rpx;
  background-color: $card-color;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 -4rpx 10rpx rgba(0, 0, 0, 0.1);
  z-index: 50;
  border-top: 1rpx solid $border-color;
}

.user-info {
  display: flex;
  align-items: center;
  margin-right: 20rpx;
}

.user-avatar {
  width: 50rpx;
  height: 50rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(45deg, $primary-color, $secondary-color);
  border-radius: 50%;
  font-size: 30rpx;
  margin-right: 12rpx;
  box-shadow: $glow-primary;
}

.username {
  font-size: 26rpx;
  color: $text-dark;
  font-weight: 500;
  max-width: 140rpx;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.logout-btn {
  display: flex;
  align-items: center;
  padding: 8rpx 16rpx;
  border-radius: 30rpx;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.logout-btn:active {
  background: rgba(255, 0, 0, 0.2);
}

.logout-icon {
  font-size: 26rpx;
  margin-right: 6rpx;
  color: $text-dark;
}

.logout-text {
  font-size: 22rpx;
  color: $text-dark;
}
</style>
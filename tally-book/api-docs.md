# 记账系统API文档

## 接口基本信息

- 基础URL: `http://localhost:8080/api`
- 接口认证: 所有接口除登录、注册、找回密码外，均需要在请求头中携带`Authorization: Bearer {token}`进行认证

## 记录相关接口

### 获取记录列表
- 请求方式: `GET`
- 接口路径: `/record/list`
- 请求参数:
  | 参数名 | 类型 | 必填 | 说明 |
  |-------|-----|------|-----|
  | page | int | 否 | 页码，默认1 |
  | size | int | 否 | 每页条数，默认10 |
  | category | string | 否 | 分类代码，如'food', 'transport'等，不传则查询所有分类 |
  | startDate | string | 否 | 开始日期，格式yyyy-MM-dd |
  | endDate | string | 否 | 结束日期，格式yyyy-MM-dd |

- 响应结果:
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": [
      {
        "record_id": 1,
        "user_id": 1,
        "amount": 35.50,
        "category": "food",
        "description": "午餐",
        "record_time": "2025-04-01 12:30:00",
        "year": 2025,
        "month": 4,
        "day": 1,
        "created_at": "2025-04-01 12:30:00",
        "updated_at": "2025-04-01 12:30:00",
        "is_deleted": 0
      },
      {
        "record_id": 2,
        "user_id": 1,
        "amount": 15.00,
        "category": "transport",
        "description": "公交车",
        "record_time": "2025-04-01 08:45:00",
        "year": 2025,
        "month": 4,
        "day": 1,
        "created_at": "2025-04-01 08:45:00",
        "updated_at": "2025-04-01 08:45:00",
        "is_deleted": 0
      }
    ]
  }
  ```

### 添加记录
- 请求方式: `POST`
- 接口路径: `/record/add`
- 请求参数:
  ```json
  {
    "user_id": 1,
    "category": "food",
    "amount": 35.50,
    "description": "午餐",
    "record_time": "2025-04-01 12:30:00"
  }
  ```
- 响应结果:
  ```json
  {
    "code": 200,
    "message": "添加成功",
    "data": {
      "record_id": 5
    }
  }
  ```

### 更新记录
- 请求方式: `PUT`
- 接口路径: `/record/update`
- 请求参数:
  ```json
  {
    "record_id": 1,
    "user_id": 1,
    "category": "food",
    "amount": 40.00,
    "description": "午餐修改",
    "record_time": "2025-04-01 12:30:00"
  }
  ```
- 响应结果:
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": null
  }
  ```

### 删除记录
- 请求方式: `DELETE`
- 接口路径: `/record/delete/{record_id}`
- 响应结果:
  ```json
  {
    "code": 200,
    "message": "删除成功",
    "data": null
  }
  ```

## 数据库结构与字段说明

### 记录表结构
后端数据库中的记录表结构如下：
```sql
CREATE TABLE `record` (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `category` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `record_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `year` int NOT NULL,
  `month` int NOT NULL,
  `day` int NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`record_id`)
)
```

### 字段说明
| 字段名 | 类型 | 说明 |
|-------|-----|------|
| record_id | int | 记录ID，主键 |
| user_id | int | 用户ID，关联用户表 |
| amount | decimal(10,2) | 金额 |
| category | varchar(30) | 分类代码，如'food', 'transport'等 |
| description | varchar(255) | 备注描述 |
| record_time | datetime | 记录时间 |
| year | int | 年份，由record_time自动生成 |
| month | int | 月份，由record_time自动生成 |
| day | int | 日期，由record_time自动生成 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |
| is_deleted | tinyint | 是否删除，0-否，1-是 |

### 分类对应关系
| 分类ID | 分类代码 | 分类名称 |
|-------|---------|---------|
| 1 | food | 餐饮 |
| 2 | transport | 交通 |
| 3 | shopping | 购物 |
| 4 | entertainment | 娱乐 |
| 5 | housing | 住房 |
| 6 | medical | 医疗 |
| 7 | education | 教育 |
| 8 | other | 其他 |

### 注意事项
- 本系统只记录支出类型的记录，不包含收入类型
- 所有金额均为正数，前端显示时添加负号表示支出
- `year`, `month`, `day`字段可由后端根据`record_time`自动生成，前端无需提供
- `created_at`, `updated_at`由数据库自动管理，前端无需提供
- `is_deleted`字段用于软删除功能，前端无需关心

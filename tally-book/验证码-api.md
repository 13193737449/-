# 花哪儿了记账应用 - 图形验证码API接口文档

## 目录
1. [接口规范](#接口规范)
2. [验证码接口](#验证码接口)

## 接口规范

### 通用返回格式

```json
{
  "code": 200,        // 状态码，200表示成功
  "message": "操作成功", // 响应消息
  "data": {}          // 响应数据，可能是对象、数组或null
}
```

### 请求格式

- 对于GET请求，参数应当放在URL中
- 对于POST/PUT/DELETE请求，参数应当使用JSON格式放在请求体中
- Content-Type应当设置为`application/json`

### 认证规范

验证码相关接口无需认证即可访问。

## 验证码接口

### 1.1 获取图形验证码

- **接口URL**: `/api/captcha/get`
- **请求方式**: GET
- **接口描述**: 获取普通字符图形验证码
- **认证要求**: 无需认证

#### 请求参数

无

#### 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "captchaId": "f8e7d6c3-b2a1-4567-8901-2345abcdef67",
    "captchaImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
    "expireTime": 300
  }
}
```

#### 参数说明

| 参数名 | 类型 | 描述 |
| ------ | ---- | ---- |
| captchaId | String | 验证码ID，用于后续验证 |
| captchaImage | String | Base64编码的图片数据，可直接用于img标签的src属性 |
| expireTime | Integer | 验证码有效期，单位为秒 |

### 1.2 验证图形验证码

- **接口URL**: `/api/captcha/verify`
- **请求方式**: POST
- **接口描述**: 验证用户输入的验证码是否正确
- **认证要求**: 无需认证

#### 请求参数

| 参数名 | 类型 | 必填 | 描述 |
| ------ | ---- | ---- | ---- |
| captchaId | String | 是 | 验证码ID |
| captchaCode | String | 是 | 用户输入的验证码 |

#### 请求示例

```json
{
  "captchaId": "f8e7d6c3-b2a1-4567-8901-2345abcdef67",
  "captchaCode": "A2B3C"
}
```

#### 成功响应

```json
{
  "code": 200,
  "message": "验证成功",
  "data": {
    "verified": true
  }
}
```

#### 失败响应

```json
{
  "code": 400,
  "message": "验证码错误或已过期",
  "data": {
    "verified": false
  }
}
```

## 使用说明

1. 在用户注册或找回密码等需要验证码的场景中，前端应先调用获取验证码接口。
2. 获取到验证码后，将验证码图片显示给用户，并记录返回的captchaId。
3. 用户输入验证码后，将captchaId和用户输入的验证码一起提交到后端。
4. 验证码仅能使用一次，验证通过后将自动失效。
5. 验证码有效期为5分钟，过期需重新获取。

## 注册/找回密码接口使用验证码

用户注册和找回密码接口需要在请求参数中包含验证码信息：

```json
// 注册请求示例
{
  "phone": "13800138000",
  "username": "zhangsan",
  "password": "123456",
  "captchaId": "f8e7d6c3-b2a1-4567-8901-2345abcdef67",
  "captchaCode": "A2B3C"
}

// 找回密码请求示例
{
  "phone": "13800138000",
  "newPassword": "654321",
  "captchaId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "captchaCode": "ABC1"
}
```

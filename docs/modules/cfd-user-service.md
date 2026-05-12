# cfd-user-service 用户服务文档

## 服务概述

用户服务负责用户注册、资料管理和 KYC 状态跟踪。

- **端口**: 8085
- **数据库**: `cfd_user`
- **核心表**: `user_profile`

## 包结构

```
com.cfd.user
├── api/
│   ├── UserController.java             # REST 接口
│   ├── UserProfileResponse.java        # 用户信息响应
│   └── UserRegistrationRequest.java    # 注册请求 DTO
├── client/
│   └── UserServiceFeignClient.java
├── config/
│   └── UserModuleConfiguration.java
├── domain/
│   ├── UserProfile.java                # 用户资料聚合
│   ├── UserRepository.java            # 仓储接口
│   └── InMemoryUserRepository.java
├── persistence/
│   ├── UserProfileEntity.java
│   ├── UserProfileDbMapper.java
│   └── MybatisPlusUserRepository.java
└── service/
    └── UserApplicationService.java
```

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/users/register` | 用户注册 |
| GET | `/users/{userId}` | 查询用户资料 |

## 数据模型

| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | VARCHAR(64) | 用户唯一标识 |
| username | VARCHAR(128) | 用户名 |
| email | VARCHAR(256) | 邮箱 |
| phone | VARCHAR(32) | 手机号 |
| kyc_passed | TINYINT | KYC 是否通过 |
| created_at | TIMESTAMP | 创建时间 |

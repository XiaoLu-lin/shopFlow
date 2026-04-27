# litemall-plus 项目结构分析

## 1. 项目概述

litemall-plus 是一个企业级电商平台系统，采用前后端分离的分层架构。项目包含四个子系统、七个模块，覆盖微信小程序端、管理后台端的完整业务链路。

---

## 2. 整体架构

```
litemall-plus (v0.1.0)
├── 基础系统 (Platform)
│   ├── litemall-core         核心模块（权限、缓存、存储、通知等公共能力）
│   ├── litemall-db           数据库模块（43张表、ORM映射、数据服务）
│   └── litemall-all          打包模块（聚合所有模块，单JAR一键部署）
│
├── 管理后台子系统 (Admin)
│   ├── litemall-admin-api    管理后台后端API（端口6914，37个Controller）
│   └── litemall-admin        管理后台前端（Vue 2 + Element UI，端口9527）
│
├── 微信小程序子系统 (WxMall)
│   ├── litemall-wx-api       小程序后端API（端口6915）
│   └── litemall-wx           小程序前端（微信原生 + ColorUI）
│
└── 部署
    └── docker/               Docker 容器化部署
```

模块之间的依赖关系：

```
litemall-admin  ──(HTTP)──>  litemall-admin-api
                                    │
litemall-wx    ──(HTTP)──>   litemall-wx-api
                                    │
                             ┌──────┴──────┐
                             │             │
                        litemall-core  litemall-db
                             │             │
                             └──────┬──────┘
                                    │
                              litemall-all（聚合打包）
```

---

## 3. 技术栈

### 3.1 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.6.8 | 应用框架 |
| Spring Cloud | 2021.0.4 | 微服务框架 |
| Spring Cloud Alibaba | 2021.0.4.0 | 阿里云集成 |
| MyBatis Plus | 3.5.2 | ORM 框架 |
| PageHelper | 1.4.1 | 分页插件 |
| Sa-Token | 1.34.0 | 权限认证 |
| Redis / Redisson | 3.19.0 | 缓存 / 分布式锁 |
| MySQL | 8.0.32 | 关系型数据库 |
| Druid | 1.2.13 | 数据库连接池 |
| Hutool | 5.8.9 | Java 工具库 |
| Lombok | - | 代码简化 |
| Baidu AIP | 4.16.14 | 内容审核 |
| WeChat SDK | 4.5.0 | 微信集成 |

### 3.2 管理后台前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 2.6.12 | 前端框架 |
| Vue Router | 3.4.9 | 路由管理 |
| Vuex | 3.6.0 | 状态管理 |
| Element UI | 2.15.8 | UI 组件库 |
| Axios | 0.24.0 | HTTP 请求 |
| ECharts | 4.9.0 | 数据可视化 |
| Quill | 1.3.7 | 富文本编辑 |
| XLSX | >=0.17.0 | Excel 导出 |

### 3.3 微信小程序前端

| 技术 | 用途 |
|------|------|
| 微信小程序原生 | 小程序开发 |
| ColorUI | UI 组件库 |

---

## 4. 各模块详细说明

### 4.1 litemall-core — 核心模块

提供系统级基础能力，被 admin-api 和 wx-api 共同依赖。

**包结构**：

```
org.ysling.litemall.core
├── annotation/          自定义注解（@JsonBody 等）
├── baidu/               百度内容审核（文本、图片）
├── config/              全局配置
│   ├── AsyncConfig         异步配置
│   ├── CorsConfig          跨域配置
│   ├── JacksonConfig       JSON序列化配置
│   ├── MyBatisPlusConfig   MyBatis Plus 配置
│   ├── ScheduleConfig      定时任务配置
│   ├── ThreadPoolConfig    线程池配置
│   ├── ValidatorConfig     参数校验配置
│   └── WebSocketConfig     WebSocket 配置
├── express/             物流查询服务
├── filter/              过滤器
├── handler/             全局异常处理
├── jobs/                定时任务
├── notify/              通知服务
│   ├── sender/             短信发送（阿里云、腾讯云）
│   └── service/            邮件通知、短信通知
├── redis/               Redis 相关
│   ├── annotation/         缓存注解
│   ├── aspect/             缓存切面
│   ├── cache/              缓存服务（RedisCacheService）
│   ├── client/             Redis 客户端
│   ├── config/             Redis 配置
│   ├── lock/               分布式锁（Redisson）
│   └── writer/             缓存写入
├── satoken/             Sa-Token 权限认证
│   └── config/             SaTokenConfigure 配置类
├── service/             核心业务服务
│   ├── OrderCoreService    订单核心服务
│   ├── GoodsCoreService    商品核心服务
│   ├── CouponAssignService 优惠券分配服务
│   └── NotifyCoreService   通知核心服务
├── storage/             文件存储
│   ├── storage/            存储实现（本地 / 阿里云 OSS / 腾讯 COS / 七牛云）
│   └── service/            StorageService 服务
├── system/              系统配置
├── tasks/               异步任务
├── tenant/              多租户支持
├── transaction/         事务管理
├── utils/               工具类
│   ├── bcrypt/             密码加密
│   ├── captcha/            验证码
│   ├── http/               HTTP 工具
│   ├── ip/                 IP 定位
│   ├── response/           统一响应（ResponseUtil）
│   └── token/              Token 工具
└── weixin/              微信集成（支付、小程序、模板消息）
```

**核心文件**：

| 文件 | 作用 |
|------|------|
| `utils/response/ResponseUtil.java` | 统一接口响应格式 |
| `satoken/config/SaTokenConfigure.java` | 权限认证配置 |
| `redis/cache/RedisCacheService.java` | Redis 缓存服务 |
| `storage/service/StorageService.java` | 文件存储服务 |
| `notify/service/NotifyMailService.java` | 邮件通知 |
| `notify/service/NotifyMobileService.java` | 短信通知 |

---

### 4.2 litemall-db — 数据库模块

数据持久化层，包含所有数据库表的 ORM 映射和数据访问服务。

**包结构**：

```
org.ysling.litemall.db
├── domain/              数据库实体类（43 个，对应 43 张表）
├── entity/              业务实体（GoodsAllinone、PageResult、PageBody 等）
├── enums/               枚举类（OrderStatus、GoodsStatus、UserStatus 等）
├── handler/             MyBatis 类型处理器
├── mapper/              MyBatis Mapper 接口（43 个）
├── mybatis/             MyBatis 配置
│   ├── IBaseService         基础服务接口
│   ├── IBaseServiceImpl     基础服务实现
│   ├── MyBatisGeneratorRun  代码生成器
│   └── MyMetaObjectHandler  自动填充处理器
├── service/             数据服务接口（43 个）
├── service/impl/        数据服务实现（43 个）
└── validator/           数据校验
```

**数据库表分类**（共 43 张表）：

| 分类 | 数量 | 核心表 |
|------|------|--------|
| 用户相关 | 3 | `litemall_user`, `litemall_address`, `litemall_admin` |
| 商品相关 | 6 | `litemall_goods`, `litemall_goods_product`, `litemall_goods_specification`, `litemall_goods_attribute`, `litemall_category`, `litemall_brand` |
| 订单相关 | 3 | `litemall_order`, `litemall_order_goods`, `litemall_aftersale` |
| 营销相关 | 6 | `litemall_coupon`, `litemall_coupon_user`, `litemall_groupon`, `litemall_groupon_rules`, `litemall_reward`, `litemall_share` |
| 内容相关 | 5 | `litemall_topic`, `litemall_ad`, `litemall_dynamic`, `litemall_comment`, `litemall_issue` |
| 系统相关 | 8 | `litemall_admin`, `litemall_role`, `litemall_permission`, `litemall_log`, `litemall_notice`, `litemall_system`, `litemall_tenant`, `litemall_notice_admin` |
| 其他 | 12 | `litemall_region`, `litemall_storage`, `litemall_cart`, `litemall_collect`, `litemall_footprint`, `litemall_search_history`, `litemall_like`, `litemall_message` 等 |

**数据库设计特点**：

- 字符集：UTF8MB4（支持表情符号）
- 引擎：InnoDB
- 逻辑删除：所有表都有 `deleted` 字段
- 多租户：所有表都有 `tenant_id` 字段
- 乐观锁：所有表都有 `version` 字段
- 时间戳：`add_time`（创建时间）、`update_time`（更新时间）

---

### 4.3 litemall-admin-api — 管理后台 API

为管理后台前端提供 RESTful API 接口，运行端口 **6914**。

**包结构**：

```
org.ysling.litemall.admin
├── annotation/          自定义注解
│   ├── RequiresPermissionsDesc    权限描述注解
│   ├── context/                   权限上下文
│   ├── entity/                    权限实体
│   └── handler/                   权限处理器
├── model/               请求/响应模型（按业务分包）
│   ├── ad/                 广告
│   ├── address/            地址
│   ├── admin/              管理员
│   ├── aftersale/          售后
│   ├── auth/               认证（LoginBody 等）
│   ├── brand/              品牌
│   ├── category/           分类
│   ├── coupon/             优惠券
│   ├── goods/              商品
│   ├── order/              订单
│   ├── user/               用户
│   └── ...                 其他模型
├── service/             业务服务层
│   ├── AdminUserService
│   ├── AdminGoodsService
│   ├── AdminOrderService
│   ├── AdminCouponService
│   └── ...（共 20+ 个 Service）
├── web/                 REST 控制器（共 37 个）
│   ├── AdminAuthController      认证登录
│   ├── AdminUserController      用户管理
│   ├── AdminGoodsController     商品管理
│   ├── AdminOrderController     订单管理
│   ├── AdminCouponController    优惠券管理
│   ├── AdminTenantController    租户管理
│   └── ...
├── socket/              WebSocket
│   ├── AdminWebSocketServer
│   ├── AdminLogSocketServer
│   └── AdminWebSocketContext
└── Application.java    启动类
```

**主要功能模块**：

| 功能 | Controller | 说明 |
|------|-----------|------|
| 认证登录 | AdminAuthController | 登录、登出、Token 刷新 |
| 用户管理 | AdminUserController | 用户 CRUD、查询 |
| 商品管理 | AdminGoodsController | 商品 CRUD、上下架 |
| 订单管理 | AdminOrderController | 订单查询、发货、退款 |
| 售后管理 | AdminAftersaleController | 售后处理 |
| 优惠券 | AdminCouponController | 优惠券 CRUD、发放 |
| 团购 | AdminGrouponController | 团购规则、活动 |
| 广告管理 | AdminAdController | 广告 CRUD |
| 专题管理 | AdminTopicController | 专题 CRUD |
| 系统管理 | AdminSystemController | 系统配置 |
| 角色权限 | AdminRoleController | 角色、权限管理 |
| 操作日志 | AdminLogController | 日志查询 |
| 租户管理 | AdminTenantController | SaaS 多租户 |

---

### 4.4 litemall-wx-api — 小程序 API

为微信小程序提供后端 API 接口，运行端口 **6915**。

**主要功能**：

| 功能 | 说明 |
|------|------|
| 用户认证 | 微信登录、账号注册、扫码登录 |
| 商品浏览 | 首页、分类、搜索、商品详情 |
| 购物车 | 加入购物车、修改数量、删除 |
| 订单 | 下单、支付、发货、评价 |
| 营销 | 优惠券领取使用、团购、赏金 |
| 用户中心 | 收藏、足迹、地址管理 |
| 消息 | 通知、聊天 |

---

### 4.5 litemall-admin — 管理后台前端

基于 Vue 2 + Element UI 构建的管理后台 SPA 应用。

**目录结构**：

```
litemall-admin/src/
├── api/                 API 接口调用（31 个模块）
├── views/               页面组件
│   ├── dashboard/          仪表板（数据统计图表）
│   ├── goods/              商品管理（列表、创建、编辑）
│   ├── order/              订单管理
│   ├── user/               用户管理
│   ├── promotion/          营销管理（优惠券、团购）
│   ├── mall/               商城管理（分类、品牌、问题）
│   ├── system/             系统管理（管理员、角色、日志）
│   ├── profile/            个人中心
│   ├── login/              登录页
│   └── errorPage/          错误页（401、404）
├── components/          公共组件
├── router/              路由配置
├── store/               Vuex 状态管理
├── utils/               工具函数
├── directive/           自定义指令
├── plugins/             插件
├── layout/              布局组件
├── assets/              静态资源（图标、图片、样式）
├── App.vue              根组件
├── main.js              入口文件
├── permission.js        权限控制
└── settings.js          应用配置
```

**开发配置**：

| 配置项 | 值 |
|--------|-----|
| 开发端口 | 9527 |
| API 代理 | `/admin` → `http://localhost:6914` |
| 构建工具 | Webpack (vue-cli) |

---

### 4.6 litemall-wx — 微信小程序前端

**页面结构**：

```
litemall-wx/pages/
├── index/                首页
├── catalog/              分类
├── goods/                商品
│   ├── hotGoods/            热销商品
│   └── newGoods/            新品
├── goodsDetail/          商品详情
├── cart/                 购物车
├── search/               搜索
├── topic/                专题
│   ├── topicList/           专题列表
│   └── topicDetail/         专题详情
├── comment/              评论
│   ├── commentList/         评论列表
│   └── commentPost/         发表评论
├── issue/                问题
│   ├── issueDaily/          日常问题
│   ├── issueGoods/          商品问题
│   └── issueReward/         赏金问题
├── auth/                 认证
│   ├── login/               登录
│   ├── accountLogin/        账号登录
│   ├── register/            注册
│   └── qrauth/              扫码登录
└── ucenter/              用户中心
    ├── index/               个人中心
    ├── address/             地址列表
    ├── addressAdd/          添加地址
    ├── orderDetail/         订单详情
    ├── aftersaleDetail/     售后详情
    ├── couponSelect/        优惠券选择
    ├── help/                帮助
    └── profile/             个人资料
```

**公共组件**：

```
litemall-wx/lib/components/
├── commentModal/     评论弹窗
├── goodList/         商品列表
├── goodsItem/        商品卡片
├── redPacket/        红包组件
├── sharePop/         分享弹窗
└── uploadImg/        图片上传
```

---

### 4.7 litemall-all — 打包模块

聚合所有模块，打包成单一可执行 JAR 文件。

**作用**：

1. 引入 litemall-core、litemall-db、litemall-admin-api、litemall-wx-api 依赖
2. 将 litemall-admin 前端构建产物打包到 static 目录
3. 输出 `litemall-all-0.1.0-exec.jar` 可直接运行

---

## 5. 核心功能特性

### 5.1 权限认证

- 基于 Sa-Token 1.34 框架
- 支持角色、权限、数据范围权限
- JWT Token 支持
- Redis 会话存储（支持分布式集群）

### 5.2 缓存与性能

- Redis 分布式缓存
- Redisson 分布式锁
- Redis Stream / Pub-Sub 消息队列
- 接口限流

### 5.3 文件存储

- 本地存储
- 阿里云 OSS
- 腾讯云 COS
- 七牛云

### 5.4 微信集成

- 微信小程序登录
- 微信支付
- 模板消息推送
- 二维码生成

### 5.5 多租户支持

- 租户数据隔离（`tenant_id` 字段）
- 动态数据源
- 租户级别配置

### 5.6 通知服务

- 邮件通知
- 短信通知（阿里云短信、腾讯云短信）
- 微信模板消息

### 5.7 异步处理

- 异步任务（线程池）
- 定时任务（ScheduleConfig）
- 延时任务
- 异步事务

---

## 6. 构建与部署

### 6.1 环境要求

| 环境 | 版本 |
|------|------|
| JDK | 1.8+ |
| Maven | 3.6+ |
| MySQL | 5.7 / 8.0+ |
| Redis | 6.0+（可选） |
| Node.js | 12+ |

### 6.2 本地开发启动

```bash
# 1. 数据库初始化
mysql -u root -p
CREATE DATABASE litemall_plus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
mysql -u root -p litemall_plus < litemall-db/sql/litemall_plus.sql

# 2. 编译后端
mvn clean install -DskipTests

# 3. 启动管理后台 API（端口 6914）
cd litemall-admin-api
mvn spring-boot:run

# 4. 启动小程序 API（端口 6915）
cd litemall-wx-api
mvn spring-boot:run

# 5. 启动管理后台前端（端口 9527）
cd litemall-admin
npm install
npm run dev
```

### 6.3 访问地址

| 服务 | 地址 |
|------|------|
| 管理后台前端 | http://localhost:9527 |
| 管理后台 API | http://localhost:6914 |
| 小程序 API | http://localhost:6915 |

### 6.4 一键部署

通过 litemall-all 模块打包成单一 JAR：

```bash
mvn clean package -DskipTests
java -jar litemall-all/target/litemall-all-0.1.0-exec.jar
```

### 6.5 Docker 部署

```bash
# 构建镜像
cd docker
docker build -t litemall-plus .

# 运行容器
docker run -d -p 6914:6914 --name litemall litemall-plus
```

---

## 7. 配置文件说明

| 配置文件 | 位置 | 用途 |
|----------|------|------|
| `application.yml` | litemall-admin-api/src/main/resources/ | 管理后台 API 配置（端口、profile 引入） |
| `application.yml` | litemall-wx-api/src/main/resources/ | 小程序 API 配置 |
| `application-db.yml` | litemall-db/src/main/resources/ | 数据库连接、Druid 连接池 |
| `application-core.yml` | litemall-core/src/main/resources/ | 核心配置（微信、存储、通知、物流） |
| `application.yml` | litemall-all/src/main/resources/ | 聚合启动配置 |
| `vue.config.js` | litemall-admin/ | Vue 前端配置（代理、构建） |
| `.env.development` | litemall-admin/ | 前端开发环境变量 |
| `package.json` | litemall-admin/ | 前端依赖管理 |

---

## 8. 项目统计

| 指标 | 数量 |
|------|------|
| Maven 模块 | 5 个 |
| 数据库表 | 43 张 |
| Java 实体类 | 43 个 |
| Mapper 接口 | 43 个 |
| DB Service | 43 个 |
| Admin Controller | 37 个 |
| 前端 API 模块 | 31 个 |
| 前端页面模块 | 11 个 |
| 小程序页面 | 20+ 个 |
| 核心依赖 | 50+ 个 |

---

## 9. 请求处理链路

一个典型的管理后台请求链路如下：

```
浏览器
  │
  ▼
litemall-admin (Vue 前端)
  │  axios 发起 HTTP 请求
  │  例如: GET /admin/demo-crud/list?page=1&limit=10
  ▼
litemall-admin-api (Spring Boot 后端)
  │
  ├── Controller 层：接收请求参数，调用 Service
  │   └── AdminDemoCrudController.list(DemoCrudListBody body)
  │
  ├── Service 层：业务逻辑处理、参数校验
  │   └── AdminDemoCrudService.querySelective(body)
  │
  ├── DB Service 层：数据查询
  │   └── IssueServiceImpl (MyBatis Plus CRUD)
  │
  ├── Mapper 层：SQL 执行
  │   └── IssueMapper (MyBatis)
  │
  └── ResponseUtil：统一响应格式
      └── { errno: 0, errmsg: "成功", data: { list: [...], total: 100 } }
```

---

## 10. 关键文件索引

### 后端入口

| 文件 | 路径 |
|------|------|
| 管理后台启动类 | `litemall-admin-api/src/main/java/org/ysling/litemall/admin/Application.java` |
| 聚合启动类 | `litemall-all/src/main/java/.../Application.java` |

### 认证相关

| 文件 | 路径 |
|------|------|
| 登录 Controller | `litemall-admin-api/.../web/AdminAuthController.java` |
| Sa-Token 配置 | `litemall-core/.../satoken/config/SaTokenConfigure.java` |
| 统一响应 | `litemall-core/.../utils/response/ResponseUtil.java` |

### 数据库相关

| 文件 | 路径 |
|------|------|
| 数据库脚本 | `litemall-db/sql/litemall_plus.sql` |
| 数据库配置 | `litemall-db/src/main/resources/application-db.yml` |

### 前端入口

| 文件 | 路径 |
|------|------|
| 前端入口 | `litemall-admin/src/main.js` |
| 路由配置 | `litemall-admin/src/router/index.js` |
| 权限控制 | `litemall-admin/src/permission.js` |
| API 请求封装 | `litemall-admin/src/utils/request.js` |

### 部署相关

| 文件 | 路径 |
|------|------|
| Dockerfile | `docker/Dockerfile` |
| Docker 运行脚本 | `docker/DockerRun.sh` |
| JAR 运行脚本 | `docker/JarRun.sh` |

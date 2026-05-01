# shopflow 后端架构说明（前端开发视角）

## 1. 文档目标

这份文档面向前端开发同学，目的是快速回答 4 个问题：

1. 前端请求到底打到哪个后端模块
2. 请求在后端是怎么流转的
3. 登录与权限是怎么校验的
4. 出问题时该优先查哪里

适用范围：管理后台前端 shopflow-admin 与管理后端 shopflow-admin-api 的联调、排错、部署。

---

## 2. 后端整体模块结构

项目后端是 Maven 多模块，父工程在 [pom.xml](../pom.xml)。

核心模块：

1. shopflow-db
- 主要职责：数据访问层、实体、Mapper、分页、动态数据源
- 关键依赖：MyBatis-Plus、Druid、MySQL、PageHelper
- 参考文件：[shopflow-db/pom.xml](../shopflow-db/pom.xml)

2. shopflow-core
- 主要职责：通用基础能力（鉴权、拦截器、Redis、工具类、统一响应、通知、任务）
- 关键依赖：Sa-Token、Redis、WebSocket、支付/对象存储 SDK
- 参考文件：[shopflow-core/pom.xml](../shopflow-core/pom.xml)

3. shopflow-admin-api
- 主要职责：管理端业务接口（给 shopflow-admin 前端调用）
- 运行端口：当前为 6914
- 参考文件：[shopflow-admin-api/src/main/resources/application.yml](../shopflow-admin-api/src/main/resources/application.yml)
- 参考文件：[shopflow-admin-api/pom.xml](../shopflow-admin-api/pom.xml)

4. shopflow-wx-api
- 主要职责：小程序端业务接口

5. shopflow-all
- 主要职责：聚合启动模块（将 admin + wx + core + db 组合成一个进程）
- 用途：一体化部署场景

---

## 3. 前端调用后端的真实链路

以管理前端为例：

1. 页面或 Vuex 调用 api 文件
- 例如登录信息请求在 [shopflow-admin/src/api/login.js](../shopflow-admin/src/api/login.js)

2. 统一 axios 封装处理
- 入口在 [shopflow-admin/src/utils/request.js](../shopflow-admin/src/utils/request.js)
- baseURL 来自 VUE_APP_BASE_API
- request 拦截器会附带：
  - X-ShopFlow-Admin-Token
  - X-ShopFlow-TenantId

3. 开发环境代理转发
- 配置在 [shopflow-admin/vue.config.js](../shopflow-admin/vue.config.js)
- /admin 代理到本地后端 6914

4. 后端 Controller 接口处理
- 例如鉴权相关在 [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java)

5. 返回统一 JSON 协议
- 封装类在 [shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java](../shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java)
- 统一结构：errno / errmsg / data

---

## 4. 认证与权限模型（前端必须理解）

### 4.1 登录核心接口

管理端主要鉴权接口在 AdminAuthController：

1. POST /admin/auth/login
- 入参：username、password
- 登录参数模型在 [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/model/auth/body/LoginBody.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/model/auth/body/LoginBody.java)

2. GET /admin/auth/info
- 用于拉取当前用户信息、角色、权限

3. POST /admin/auth/logout
- 登出并清理会话

### 4.2 鉴权拦截

Sa-Token 全局拦截器在 core 模块注册：
- [shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java](../shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java)

请求默认会进入鉴权逻辑，部分接口通过注解放行。

### 4.3 前端如何判断成功/失败

前端不是仅靠 HTTP 状态判断，而是业务字段 errno：

1. errno = success：认为成功
2. errno = A0223：未登录，前端会触发重新登录
3. 其它 errno：统一弹错误提示

逻辑在 [shopflow-admin/src/utils/request.js](../shopflow-admin/src/utils/request.js)。

---

## 5. 数据与基础设施依赖

管理端后端启动依赖：

1. MySQL
- 数据源配置在 [shopflow-db/src/main/resources/application-db.yml](../shopflow-db/src/main/resources/application-db.yml)

2. Redis
- 用于 token、限流、任务等能力

如果 MySQL/Redis 异常，常见表现是：

1. 后端启动失败或启动后报连接异常
2. 登录后 info 接口返回未登录或系统错误

---

## 6. 从前端角度看目录分工

前端日常联调最常看这几类目录：

1. 管理端接口实现
- [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web)

2. 通用响应与鉴权能力
- [shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response](../shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response)
- [shopflow-core/src/main/java/org/ysling/shopflow/core/satoken](../shopflow-core/src/main/java/org/ysling/shopflow/core/satoken)

3. 前端请求封装与接口定义
- [shopflow-admin/src/utils/request.js](../shopflow-admin/src/utils/request.js)
- [shopflow-admin/src/api](../shopflow-admin/src/api)

4. 运行配置
- [shopflow-admin/vue.config.js](../shopflow-admin/vue.config.js)
- [shopflow-admin-api/src/main/resources/application.yml](../shopflow-admin-api/src/main/resources/application.yml)
- [shopflow-db/src/main/resources/application-db.yml](../shopflow-db/src/main/resources/application-db.yml)

---

## 7. 联调排错手册（按优先级）

### 第一步：确认端口与代理

1. 前端 dev 端口是否为 9527
2. 后端 admin-api 是否监听 6914
3. 前端代理 /admin 是否指向 6914

### 第二步：确认接口路径

管理端接口前缀是 /admin。

例如：

1. /admin/auth/login
2. /admin/auth/info

### 第三步：确认 token 头是否带上

看请求头是否包含：

1. X-ShopFlow-Admin-Token
2. X-ShopFlow-TenantId

### 第四步：确认业务返回协议

看响应体 errno：

1. success：成功
2. A0223：未登录
3. 其它：按 errmsg 排查

### 第五步：确认基础依赖

1. MySQL 连通
2. Redis 连通
3. 数据库账号、密码、库名配置正确

---

## 8. 打包与部署关系（前端最容易混淆）

### 后端

后端打包后常见产物：

1. shopflow-admin-api 的 exec jar（单独管理端后端）
2. shopflow-all 的 exec jar（聚合模式）

### 前端

管理前端打包产物目录：

1. shopflow-admin/dist

### 常见误区

1. 改了 .env.deployment 但不重新 build 前端
- 结果：线上依旧用旧地址

2. 改了 application.yml 但不重启后端
- 结果：运行进程仍是旧配置

---

## 9. 当前项目与你本地环境的对接结论

结合当前仓库状态与本地联调历史：

1. 管理后端主联调端口使用 6914
2. 管理前端代理已对齐 6914
3. 登录流程当前是用户名 + 密码模式
4. 前后端打包流程可独立执行，也可由聚合模块统一打包后端

---

## 10. 给前端同学的建议

1. 日常开发优先用 shopflow-admin-api 单独联调，定位问题更快
2. 先看 errno 再看 HTTP 状态
3. 先看代理和端口，再看业务代码
4. 把请求头与响应体打印完整，联调效率会明显提高
5. 生产发布前做一次全链路 smoke test：登录、获取用户信息、核心列表页

---

## 11. 快速索引

1. 后端父工程模块定义
- [pom.xml](../pom.xml)

2. 管理端后端端口配置
- [shopflow-admin-api/src/main/resources/application.yml](../shopflow-admin-api/src/main/resources/application.yml)

3. 管理端鉴权控制器
- [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java)

4. 统一响应协议
- [shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java](../shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java)

5. Sa-Token 拦截器注册
- [shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java](../shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java)

6. 前端请求封装
- [shopflow-admin/src/utils/request.js](../shopflow-admin/src/utils/request.js)

7. 前端登录 API
- [shopflow-admin/src/api/login.js](../shopflow-admin/src/api/login.js)

8. 前端 dev 代理
- [shopflow-admin/vue.config.js](../shopflow-admin/vue.config.js)

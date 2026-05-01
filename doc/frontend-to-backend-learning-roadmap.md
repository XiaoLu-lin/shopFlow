# 前端转后端学习路线（基于 shopflow 实战）

## 1. 你现在的起点

你已经具备前端开发经验，这意味着你在以下方面有优势：

1. 理解 HTTP 请求/响应
2. 理解接口对业务页面的驱动作用
3. 会读接口入参/出参并做联调

后端学习的核心不是从 0 开始，而是把你熟悉的“前端调用接口”反过来，变成“自己设计并实现接口”。

---

## 2. 学习目标（按 8 周设计）

最终目标：你可以独立完成一个后端功能闭环。

1. 能写一个带鉴权的 CRUD 接口
2. 能排查常见后端问题（启动失败、数据库连接、权限异常）
3. 能完成本地部署、打包、上线前自测
4. 能和前端一起定义并维护接口契约

---

## 3. 技术地图（先看全景）

本项目后端核心栈：

1. Spring Boot 2.6.x
2. MyBatis/MyBatis-Plus
3. MySQL + Redis
4. Sa-Token（鉴权）
5. Maven 多模块

对应模块关系：

1. shopflow-admin-api：管理端接口（你最先接触）
2. shopflow-core：公共能力（鉴权、响应封装、工具）
3. shopflow-db：数据层（实体、Mapper、SQL）
4. shopflow-all：聚合启动模块

参考文件：

1. [pom.xml](../pom.xml)
2. [shopflow-admin-api/src/main/resources/application.yml](../shopflow-admin-api/src/main/resources/application.yml)
3. [shopflow-db/src/main/resources/application-db.yml](../shopflow-db/src/main/resources/application-db.yml)

---

## 4. 学习方法（非常重要）

不要“先看完再写”，而是按下面节奏：

1. 先跑起来
2. 看一个真实接口
3. 改一个小需求
4. 自测并联调
5. 总结一次

你每学一块，都要在当前项目里落一个可运行改动。

---

## 5. 8 周学习计划（项目实战版）

## 第 1 周：环境与后端启动

目标：理解后端怎么跑起来。

任务：

1. 启动 MySQL、Redis
2. 启动 shopflow-admin-api
3. 用 curl 访问 /admin/auth/info

验收：

1. 知道端口在哪配置
2. 知道启动失败看什么日志

重点文件：

1. [shopflow-admin-api/src/main/resources/application.yml](../shopflow-admin-api/src/main/resources/application.yml)
2. [shopflow-db/src/main/resources/application-db.yml](../shopflow-db/src/main/resources/application-db.yml)

---

## 第 2 周：请求链路与统一返回

目标：弄清接口从 Controller 到响应的完整过程。

任务：

1. 跟读登录接口
2. 跟读统一响应类
3. 在一个简单接口里加一条日志

验收：

1. 能解释 errno/errmsg/data 的来源
2. 能定位一个接口返回值在哪组装

重点文件：

1. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java)
2. [shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java](../shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java)

---

## 第 3 周：数据库与数据层

目标：会从接口追到表，会从表反推接口。

任务：

1. 找一个列表接口，定位对应 Mapper 与 SQL
2. 新增一个查询条件（例如按用户名模糊查询）
3. 验证分页与总数是否正确

验收：

1. 能看懂实体、Mapper、Service 的基本协作
2. 能改一条 SQL 并让前端生效

重点目录：

1. [shopflow-db/src/main/java](../shopflow-db/src/main/java)
2. [shopflow-db/src/main/resources/mappers](../shopflow-db/src/main/resources/mappers)

---

## 第 4 周：鉴权与权限

目标：理解接口为什么能访问/不能访问。

任务：

1. 找 3 个带权限注解接口
2. 对比登录前后调用差异
3. 新增一个需要登录的测试接口

验收：

1. 能解释 token 如何传递
2. 能解释未登录时为什么返回 A0223

重点文件：

1. [shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java](../shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java)
2. [shopflow-admin/src/utils/request.js](../shopflow-admin/src/utils/request.js)

---

## 第 5 周：后端业务开发规范

目标：你能按规范写一个新接口。

任务：

1. 新增一个简单模块（如“公告分类”）
2. 完成增删改查
3. 补齐参数校验与错误码

验收：

1. API 命名统一
2. 入参有校验
3. 返回走统一 ResponseUtil

---

## 第 6 周：联调与错误处理

目标：能独立处理常见线上前后端问题。

任务：

1. 故意制造 3 类错误：参数错、权限错、数据库错
2. 写出每类错误的排查步骤
3. 让前端错误提示更可读

验收：

1. 你有一份自己的排错手册

---

## 第 7 周：打包部署与环境配置

目标：理解 dev、test、prod 差异。

任务：

1. 后端打包（mvn clean package）
2. 前端打包（npm run build:prod）
3. 用 Nginx + Jar 方案完成一次部署演练

验收：

1. 知道哪些配置是运行时生效
2. 知道哪些配置必须重新 build

---

## 第 8 周：做一个完整功能闭环

目标：从需求到上线前自测完整走一遍。

任务：

1. 提需求说明
2. 设计接口
3. 实现后端
4. 联调前端
5. 编写测试清单

验收：

1. 你能独立交付一个完整功能

---

## 6. 你每天怎么学（建议 1.5 小时）

1. 20 分钟：读代码（只读一个链路）
2. 50 分钟：动手改一个最小点
3. 20 分钟：验证（接口 + 前端页面）
4. 10 分钟：写学习笔记

坚持 4 周，你会明显从“会调接口”变成“会写接口”。

---

## 7. 前端转后端最容易踩的坑

1. 只看 Controller，不看 Service 和 Mapper
2. 忽略错误码协议，只看 HTTP 状态
3. 改了配置不重启后端
4. 改了前端环境变量不重新 build
5. 只看功能通不通，不看边界条件

---

## 8. 推荐你立刻做的 3 个练习

1. 读懂登录链路
- 从 [shopflow-admin/src/api/login.js](../shopflow-admin/src/api/login.js) 到 AdminAuthController

2. 给一个列表接口加筛选字段
- 前端加输入框 + 后端加查询条件

3. 新增一个“只需登录即可访问”的测试接口
- 验证鉴权是否生效

---

## 9. 学习成果自检清单

满足以下 8 条，说明你已经完成前端到后端的第一阶段转型：

1. 能独立启动后端并解释关键配置
2. 能独立定位一个接口的全链路
3. 能独立改 SQL 并让前端看到效果
4. 能独立解释 token 鉴权流程
5. 能独立新增一个 CRUD 接口
6. 能独立做基础异常处理
7. 能独立完成打包并说明产物用途
8. 能把功能从需求写到联调通过

---

## 10. 下一步建议

你可以直接从这个顺序开始：

1. 第 1 周任务当天完成
2. 第 2 周和第 3 周连续做
3. 每周至少提交 2 次可运行改动

如果你愿意，我下一步可以按这份路线给你出“第 1 周到第 2 周的每日任务版清单（每天具体做哪几个文件）”。

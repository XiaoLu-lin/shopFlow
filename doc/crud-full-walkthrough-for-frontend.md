# 从 0 到 1：前端同学在本项目里做一个完整 CRUD 页面

## 1. 你会得到什么

完成本文后，你可以在当前仓库里独立做出一个最小可用 CRUD 功能：

1. 后端新增一个资源模块（增删改查）
2. 前端新增一个管理页面（列表、搜索、新增、编辑、删除）
3. 前后端联调打通
4. 本地启动并验收

本文选择一个示例资源名：DemoTag（演示标签）。

你可以照抄流程，之后把 DemoTag 替换成你真实业务名即可。

---

## 2. 先理解当前项目的 CRUD 规律

先记住这个固定模式：

1. 后端接口层在 admin-api 的 web 包
2. 后端数据访问层在 db 模块（domain + mapper + mappers xml）
3. 前端接口定义在 src/api
4. 前端页面在 src/views
5. 前端请求统一走 request 封装

参考入口：

1. [shopflow-admin/src/utils/request.js](../shopflow-admin/src/utils/request.js)
2. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web)
3. [shopflow-db/src/main/java](../shopflow-db/src/main/java)

---

## 3. 第 0 步：先选一个可抄的现有模块

为了少踩坑，先选一个结构简单的现有模块当模板（例如 issue、keyword、brand）。

你要做的不是直接照搬代码，而是照搬结构和调用关系。

你观察时重点看：

1. Controller 提供了哪些接口
2. Service 如何组织业务
3. Mapper 和 SQL 怎么分页
4. 前端列表页怎么请求、怎么弹窗编辑

---

## 4. 后端部分（从数据库开始）

## 第 1 步：设计表

先在数据库建一张最小表（以 DemoTag 为例）：

字段建议：

1. id
2. name
3. sort_order
4. add_time
5. update_time
6. deleted

先保证字段够用，不要一上来设计很复杂。

---

## 第 2 步：生成或编写数据层代码

在 db 模块新增：

1. domain 实体
2. mapper 接口
3. mappers xml（如果你的模块使用 xml）

目录参考：

1. [shopflow-db/src/main/java/org/ysling/shopflow/db/domain](../shopflow-db/src/main/java/org/ysling/shopflow/db/domain)
2. [shopflow-db/src/main/java/org/ysling/shopflow/db/dao](../shopflow-db/src/main/java/org/ysling/shopflow/db/dao)
3. [shopflow-db/src/main/resources/mappers](../shopflow-db/src/main/resources/mappers)

你至少要有这几个查询：

1. 分页列表
2. 根据 id 查询
3. 新增
4. 更新
5. 逻辑删除

---

## 第 3 步：在 admin-api 加 Service

新增一个 DemoTagService（命名按现有规范）。

Service 负责：

1. 列表查询（带条件）
2. 新增参数校验
3. 编辑参数校验
4. 删除操作（逻辑删除）

建议把业务规则放在 Service，不要堆在 Controller。

---

## 第 4 步：在 admin-api 加 Controller

在 web 包新增 DemoTagController。

接口建议采用和现有管理端一致的风格：

1. 列表：GET 或 POST，返回分页结果
2. 新增：POST
3. 读取详情：GET
4. 更新：POST
5. 删除：POST

注意两点：

1. 统一使用 ResponseUtil 返回，结构保持 errno/errmsg/data
2. 按现有注解风格加登录校验（避免前端可访问但后端被拦）

参考：

1. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java)
2. [shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java](../shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java)

---

## 第 5 步：后端自测

后端先别急着接前端，先自测接口。

建议顺序：

1. 启动后端
2. 用 curl 或 apifox 测列表接口
3. 测新增
4. 用新增出来的 id 测详情
5. 测更新
6. 测删除

只有后端这 6 步都过，再接前端。

---

## 5. 前端部分（做一个标准管理页）

## 第 6 步：新增前端 API 文件

在 api 目录新增一个 demotag.js。

目录参考：

1. [shopflow-admin/src/api](../shopflow-admin/src/api)

这个文件里至少定义：

1. listDemoTag
2. createDemoTag
3. readDemoTag
4. updateDemoTag
5. deleteDemoTag

接口 URL 与后端 controller 对齐。

---

## 第 7 步：新增页面文件

在 views 下新增页面目录，例如 demo-tag，然后创建 index.vue。

目录参考：

1. [shopflow-admin/src/views](../shopflow-admin/src/views)

页面先做最小版本：

1. 查询条件（name）
2. 表格列（id/name/sortOrder/操作）
3. 新增按钮
4. 编辑按钮
5. 删除按钮
6. 分页组件

建议先实现列表和删除，再做新增和编辑弹窗。

---

## 第 8 步：接入路由与菜单

你需要把新页面挂到路由里，否则页面打不开。

目录参考：

1. [shopflow-admin/src/router](../shopflow-admin/src/router)

如果你项目菜单是后端权限驱动，还要在后端菜单权限里增加对应配置。

---

## 第 9 步：处理状态与交互细节

把这几件事做好，页面体验会明显稳定：

1. 列表加载态
2. 新增/编辑提交防重复
3. 删除确认弹窗
4. 操作成功后刷新列表
5. 错误信息展示

---

## 第 10 步：前端联调验收

按这个顺序测：

1. 打开页面，能看到列表
2. 搜索条件生效
3. 新增成功并回显
4. 编辑成功并回显
5. 删除后列表消失
6. 刷新页面后数据仍正确（证明后端持久化成功）

---

## 6. 启动与联调命令（当前仓库可直接用）

后端启动（管理端）：

1. 进入 shopflow-admin-api
2. 设置 JDK8
3. 执行 mvn spring-boot:run -DskipTests

前端启动：

1. 在 shopflow-admin 安装依赖
2. 执行 npm run dev
3. Node 18 如果报 OpenSSL 错误，增加 NODE_OPTIONS=--openssl-legacy-provider

---

## 7. 你最容易踩的 8 个坑

1. 路由已配但菜单没权限，页面看不到
2. 前端 URL 写对了但后端方法没放行或鉴权不通过
3. 列表接口返回不是分页结构，导致表格不更新
4. 更新接口漏传 id
5. 删除做成物理删，导致历史数据异常
6. 后端改了但没重启
7. 前端环境变量改了但没重新构建
8. 端口代理不一致

---

## 8. 你可以直接照着做的任务单

第 1 天：

1. 选模板模块
2. 建 DemoTag 表
3. 打通后端列表接口

第 2 天：

1. 打通后端新增/详情/更新/删除
2. 完成后端自测

第 3 天：

1. 前端 API 文件 + 列表页
2. 打通查询与删除

第 4 天：

1. 打通新增/编辑弹窗
2. 接路由菜单
3. 完成联调验收

---

## 9. 学习方式建议

你现在最有效的学习方法是：

1. 每次只做一个最小闭环
2. 每完成一步就验证
3. 不追求一次写完，追求每一步可运行

如果你愿意，我下一步可以继续给你：

1. DemoTag 的后端接口清单模板
2. index.vue 的页面骨架模板
3. 联调时每一步的检查清单

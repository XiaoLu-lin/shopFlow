# 前端学后端完整指南（基于 shopflow 项目）

## 写在最前面

你是前端，想学后端。你面前这个项目有 5 个后端模块、43 张数据库表、37 个 Controller。第一次看很容易迷路。

这份指南不会让你"从头到尾读一遍"，而是帮你按最短路径建立后端认知。

**核心原则：不要先学框架，先学请求怎么跑。**

你最熟的事是"页面调接口"。后端学习就从这个点反过来切入。

---

## 第一部分：你现在应该知道的全景

### 1.1 项目是什么

一个完整的电商系统，前后端分离，包含：

| 子系统 | 前端 | 后端 | 说明 |
|--------|------|------|------|
| 管理后台 | shopflow-admin (Vue 2) | shopflow-admin-api (Spring Boot) | 你最先接触的 |
| 微信小程序 | shopflow-wx (原生小程序) | shopflow-wx-api (Spring Boot) | 后面再看 |
| 公共基础 | — | shopflow-core | 权限、缓存、通知等 |
| 数据层 | — | shopflow-db | 43 张表的 ORM |
| 聚合部署 | — | shopflow-all | 一键打包 |

### 1.2 后端技术栈一句话说明

| 技术 | 它干什么 | 你理解成前端里的什么 |
|------|---------|---------------------|
| Spring Boot | 后端框架，管理所有模块的启动和配置 | 类似 Vue CLI + Webpack |
| Controller | 接收请求、返回响应 | 类似 Vue Router 的路由处理 |
| Service | 业务逻辑层 | 类似 Vuex actions |
| MyBatis Plus | 操作数据库的 ORM | 类似 axios，但是对数据库发请求 |
| Sa-Token | 登录鉴权 | 类似前端 token 拦截器 |
| Maven | 依赖管理和构建 | 类似 npm + package.json |
| MySQL | 数据库 | 类似 localStorage，但是持久化、结构化、多人共享 |
| Redis | 内存缓存 | 类似 sessionStorage，但在服务端 |

### 1.3 一个请求的完整旅程

```
你在浏览器点击"查询"
      │
      ▼
前端 api/demoCrud.js
      │  listDemoCrud({ page: 1, limit: 10, question: '测试' })
      │  GET /demo-crud/list?page=1&limit=10&question=测试
      ▼
vue.config.js 代理
      │  /admin → http://localhost:6914
      │  实际请求变成 GET /admin/demo-crud/list?page=1&limit=10&question=测试
      ▼
Spring Boot 路由匹配
      │  @RequestMapping("/admin/demo-crud") + @GetMapping("/list")
      │  匹配到 AdminDemoCrudController.list()
      ▼
Controller 层
      │  接收参数 DemoCrudListBody（包含 page、limit、question）
      │  调用 demoCrudService.querySelective(body)
      ▼
Service 层
      │  构造查询条件 QueryWrapper
      │  如果 question 有值 → wrapper.like("question", question)
      │  调用 queryAll(wrapper) 查数据库
      ▼
MyBatis Plus → MySQL
      │  SELECT * FROM shopflow_issue WHERE question LIKE '%测试%' LIMIT 0,10
      ▼
Service 返回 List<ShopflowIssue>
      ▼
Controller 调用 ResponseUtil.okList(list)
      │  { errno: "success", errmsg: "成功", data: { list: [...], total: 50, pages: 5 } }
      ▼
前端拿到响应
      │  res.data.list → 表格数据
      │  res.data.total → 分页总数
      ▼
页面渲染表格
```

**记住这条链路，后端学习的 80% 问题都能在这条链路上找到答案。**

---

## 第二部分：学习路线（5 个阶段）

### 阶段 1：跑起来 + 看懂一个接口

**目标**：你能在本地启动后端，调一个接口，看到返回数据。

**要做的事**：

1. 启动 MySQL，导入数据库
2. 启动 shopflow-admin-api（端口 6914）
3. 启动 shopflow-admin 前端（端口 9527）
4. 登录管理后台，打开一个列表页
5. 打开 Chrome DevTools → Network，找到这个页面发的请求
6. 在后端代码里找到对应的 Controller 方法

**要看的文件**（按顺序）：

| 顺序 | 文件 | 你要搞明白什么 |
|------|------|--------------|
| 1 | [shopflow-admin/src/api/login.js](../shopflow-admin/src/api/login.js) | 前端登录请求发到哪里 |
| 2 | [shopflow-admin/src/utils/request.js](../shopflow-admin/src/utils/request.js) | token 怎么自动带上的 |
| 3 | [AdminAuthController.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java) | 后端登录接口怎么处理的 |
| 4 | [ResponseUtil.java](../shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java) | 统一返回格式 errno/errmsg/data 怎么来的 |

**验收标准**：
- 你能说清楚：前端点击登录后，请求从哪发出、到哪个后端方法、返回了什么结构。

**参考文档**：[后端架构说明（前端视角）](backend-architecture-for-frontend.md)

---

### 阶段 2：读懂一个完整 CRUD

**目标**：你能追踪一个增删改查模块的全部代码，从前端到数据库。

**推荐你看的模块**：`demo-crud`（这是项目里专门为教学准备的模块）

**要看的文件**（按顺序）：

| 层 | 文件 | 你要搞明白什么 |
|----|------|--------------|
| 前端 API | [shopflow-admin/src/api/demoCrud.js](../shopflow-admin/src/api/demoCrud.js) | 5 个接口分别是什么请求方式 |
| 前端页面 | [shopflow-admin/src/views/mall/demoCrud.vue](../shopflow-admin/src/views/mall/demoCrud.vue) | 页面怎么调 API、怎么用返回数据 |
| 后端 Controller | [AdminDemoCrudController.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminDemoCrudController.java) | 5 个接口方法分别干什么 |
| 后端 Service | [AdminDemoCrudService.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service/AdminDemoCrudService.java) | 校验和查询逻辑怎么写 |
| 请求参数 | [DemoCrudListBody.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/model/democrud/body/DemoCrudListBody.java) | 列表查询参数怎么定义 |
| 数据库实体 | [ShopflowIssue.java](../shopflow-db/src/main/java/org/ysling/shopflow/db/domain/ShopflowIssue.java) | 实体类怎么对应数据库表 |
| 数据库 Service | [IssueServiceImpl.java](../shopflow-db/src/main/java/org/ysling/shopflow/db/service/impl/IssueServiceImpl.java) | 基础 CRUD 方法从哪来 |

**核心知识点**：

```
┌─────────────────────────────────────────────┐
│  后端分层 = 前端分层的镜像                      │
│                                             │
│  前端：页面  →  API文件  →  axios  →  服务器    │
│  后端：Controller → Service → Mapper → 数据库  │
│                                             │
│  Controller = "路由层"，只负责接参数和转发       │
│  Service = "业务层"，放校验和业务逻辑           │
│  Mapper = "数据层"，直接和数据库交互            │
└─────────────────────────────────────────────┘
```

**验收标准**：
- 你能说出 list / create / read / update / delete 各走哪个方法
- 你能解释 `startPage(body)` 为什么能让查询自动分页
- 你能解释 `wrapper.like(...)` 的作用

**参考文档**：[Demo CRUD 后端详解](demo-crud-backend-full-guide.md)

---

### 阶段 3：理解权限和公共能力

**目标**：你能解释"为什么没登录会被拦截"、"为什么所有接口返回格式都一样"。

**要看的文件**：

| 主题 | 文件 | 你要搞明白什么 |
|------|------|--------------|
| 权限拦截 | [SaTokenConfigure.java](../shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java) | 哪些路径不需要登录、拦截器怎么注册的 |
| 统一响应 | [ResponseUtil.java](../shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java) | ok()、okList()、badArgument() 都返回什么 |
| 请求封装 | [shopflow-admin/src/utils/request.js](../shopflow-admin/src/utils/request.js) | 前端怎么判断 errno 并做分支处理 |

**核心知识点**：

```
前端请求带 Header:
  X-ShopFlow-Admin-Token: xxxx

后端 Sa-Token 拦截器检查这个 token：
  ├── token 有效 → 继续执行 Controller
  ├── token 无效/过期 → 返回 { errno: "A0223", errmsg: "请登录" }
  └── 路径在白名单里 → 不检查 token，直接放行

前端 request.js 拦截响应：
  ├── errno === "success" → 正常处理
  ├── errno === "A0223" → 跳转登录页
  └── 其他 → 弹错误提示
```

**验收标准**：
- 你能解释 token 从登录到接口调用的完整生命周期
- 你能找到白名单配置，知道哪些接口不需要登录

**参考文档**：[后端架构说明（前端视角）](backend-architecture-for-frontend.md) 第 4 节

---

### 阶段 4：自己动手写一个模块

**目标**：你能独立新增一个后端 CRUD 模块，前后端联调通过。

**操作步骤**（以"标签管理 DemoTag"为例）：

```
第 1 步：数据库建表
  └── 建一张 demo_tag 表（id, name, sort_order, add_time, update_time, deleted）

第 2 步：shopflow-db 加数据层
  ├── domain/ShopflowDemoTag.java       （实体类）
  ├── mapper/DemoTagMapper.java         （Mapper 接口）
  └── service/impl/DemoTagServiceImpl.java （基础 CRUD）

第 3 步：shopflow-admin-api 加业务层
  ├── model/demotag/body/DemoTagListBody.java （查询参数）
  ├── service/AdminDemoTagService.java         （业务服务）
  └── web/AdminDemoTagController.java          （5 个接口）

第 4 步：shopflow-admin 加前端
  ├── src/api/demoTag.js               （API 定义）
  ├── src/views/mall/demoTag.vue       （列表页面）
  └── src/router/index.js             （加路由）

第 5 步：自测 → 联调 → 验收
```

**固定模板（背下来）**：

```java
// Controller 固定结构
@RestController
@RequestMapping("/admin/你的模块")
@Validated
public class AdminXxxController {

    @Autowired
    private AdminXxxService xxxService;

    @GetMapping("/list")    // 列表
    @GetMapping("/read")    // 详情
    @PostMapping("/create") // 新增
    @PostMapping("/update") // 修改
    @PostMapping("/delete") // 删除
}
```

```java
// Service 固定结构
@Service
public class AdminXxxService extends XxxServiceImpl {

    public Object validate(XxxEntity entity) { ... }          // 参数校验
    public List<XxxEntity> querySelective(XxxListBody body) { ... } // 条件查询
}
```

**验收标准**：
- 你独立写的模块，前端能正常增删改查
- 分页、搜索、校验、错误提示全部正常

**参考文档**：[从 0 到 1 做一个完整 CRUD](crud-full-walkthrough-for-frontend.md)

---

### 阶段 5：理解部署和多模块协作

**目标**：你能解释项目怎么打包、怎么部署、多个模块怎么协同。

**要看的文件**：

| 文件 | 你要搞明白什么 |
|------|--------------|
| [pom.xml](../pom.xml) | Maven 怎么管理多个子模块 |
| [shopflow-all/pom.xml](../shopflow-all/pom.xml) | 聚合模块怎么把所有模块打成一个 JAR |
| [application.yml](../shopflow-admin-api/src/main/resources/application.yml) | 端口和 profile 怎么配 |
| [application-db.yml](../shopflow-db/src/main/resources/application-db.yml) | 数据库连接怎么配 |
| [docker/Dockerfile](../docker/Dockerfile) | Docker 怎么打包部署 |
| [shopflow-admin/vue.config.js](../shopflow-admin/vue.config.js) | 前端代理和打包配置 |

**核心知识点**：

```
开发模式：
  前端 9527 ──代理──> 后端 6914（admin-api 单独启动）

生产模式（方案一，分离部署）：
  Nginx 托管前端静态文件
  ├── / → 前端 dist/
  └── /admin → 反向代理到后端 JAR

生产模式（方案二，聚合部署）：
  shopflow-all 把前端 + 后端打成一个 JAR
  java -jar shopflow-all-0.1.0-exec.jar
  一个进程同时提供前端页面和后端 API
```

**验收标准**：
- 你能说清 dev / dep / prod 三个阶段的配置差异
- 你能解释为什么改了 `.env` 要重新 build
- 你能解释为什么改了 `application.yml` 要重启后端

**参考文档**：[系统部署方案](project.md) 第 1.5 节

---

## 第三部分：Java 语法速查表（给前端的）

你不需要系统学 Java，先掌握这些就够在项目里读懂代码：

### 3.1 注解 = 装饰器

```java
// Java 注解                          // 类似前端的
@RestController                      // 标记这是一个 API 控制器
@RequestMapping("/admin/issue")      // 路由前缀，类似 Vue Router 的 path
@GetMapping("/list")                 // 处理 GET 请求，类似 router.get()
@PostMapping("/create")              // 处理 POST 请求，类似 router.post()
@Autowired                           // 自动注入依赖，类似 import + 全局注册
@Service                             // 标记业务逻辑类
@Valid                               // 自动校验参数，类似前端表单 rules
@RequestBody                         // 告诉框架：参数在请求体里（JSON）
```

### 3.2 类型 = TypeScript

```java
// Java                               // TypeScript 对照
String name;                          // name: string
Integer count;                        // count: number
Boolean active;                       // active: boolean
List<String> tags;                    // tags: string[]
Map<String, Object> data;             // data: Record<string, any>
ShopflowIssue issue;                  // issue: ShopflowIssue (自定义类型)
```

### 3.3 常见模式

```java
// if 判断和前端一样
if (name == null) {
    return ResponseUtil.badArgument();
}

// for 循环和前端一样
for (ShopflowIssue item : list) {
    // 处理每一项
}

// try-catch 和前端一样
try {
    service.doSomething();
} catch (Exception e) {
    return ResponseUtil.fail();
}
```

### 3.4 继承 = extends（和前端一样）

```java
// AdminDemoCrudService 继承了 IssueServiceImpl
// 所以自动拥有 findById、add、deleteById、updateVersionSelective 等方法
// 不需要自己写 SQL

public class AdminDemoCrudService extends IssueServiceImpl {
    // 只需要写业务逻辑
}
```

---

## 第四部分：常见问题快速排查

### 4.1 后端启动失败

| 现象 | 原因 | 解决 |
|------|------|------|
| 端口被占用 | 6914 端口有其他进程 | `lsof -i :6914` 找到并杀掉 |
| 数据库连接失败 | MySQL 没启动或密码错 | 检查 `application-db.yml` |
| Redis 连接失败 | Redis 没启动 | 启动 Redis 或临时注释掉 Redis 配置 |
| 类找不到 | Maven 依赖没装好 | `mvn clean install -DskipTests` |

### 4.2 接口返回异常

| errno | 含义 | 你该怎么查 |
|-------|------|----------|
| success | 成功 | 不用查 |
| A0223 | 未登录 | 检查请求头有没有带 token |
| badArgument | 参数错误 | 检查 Service 的 validate 方法 |
| 500 | 服务端异常 | 看后端控制台的报错堆栈 |

### 4.3 前后端联调不通

```
检查清单（按顺序排查）：

1. 后端是否启动？
   → 看终端有没有 "Started Application"

2. 端口对不对？
   → 后端 6914，前端代理指向 6914

3. 请求路径对不对？
   → 前端 /demo-crud/list → 代理后变成 /admin/demo-crud/list
   → 后端 @RequestMapping("/admin/demo-crud") + @GetMapping("/list")

4. token 带了没？
   → Chrome DevTools → Network → 看 Request Headers

5. 参数格式对不对？
   → GET 用 params，POST 用 data (JSON body)
```

---

## 第五部分：项目文档地图

这个项目已经有一套完整的文档体系，不同阶段看不同文档：

### 入门阶段

| 文档 | 内容 | 什么时候看 |
|------|------|----------|
| [后端架构说明（前端视角）](backend-architecture-for-frontend.md) | 后端模块分工、请求链路、鉴权模型 | 第 1 天 |
| [模块阅读顺序](frontend-backend-module-reading-order.md) | 应该先看哪个模块、后看哪个模块 | 第 1 天 |

### 实战阶段

| 文档 | 内容 | 什么时候看 |
|------|------|----------|
| [Demo CRUD 后端详解](demo-crud-backend-full-guide.md) | 完整拆解一个 CRUD 后端实现 | 阶段 2 |
| [从 0 到 1 做 CRUD](crud-full-walkthrough-for-frontend.md) | 自己动手写一个完整 CRUD 的步骤 | 阶段 4 |

### 深入阶段

| 文档 | 内容 | 什么时候看 |
|------|------|----------|
| [项目结构分析](project-structure-analysis.md) | 各模块包结构、数据库表、技术栈详情 | 想全面了解时 |
| [学习路线（8 周版）](frontend-to-backend-learning-roadmap.md) | 按周规划的详细学习任务 | 想系统学习时 |
| [系统部署方案](project.md) | 开发、部署、上线的完整方案 | 阶段 5 |
| [数据库设计](database.md) | 表结构设计说明 | 需要建表时 |
| [最佳管理后台实践](best-admin.md) | 管理后台的需求分析和设计思路 | 想理解设计思想时 |

---

## 第六部分：每日学习节奏建议

每天 1.5 小时，按这个节奏最高效：

```
前 20 分钟：读代码
  只读一条完整链路（前端 API → Controller → Service → Mapper）
  不要跳来跳去，一条链路追到底

中间 50 分钟：动手改
  改一个最小的点（比如加一个查询条件、加一个校验）
  改完立刻运行验证

后 20 分钟：验证 + 记录
  前端页面上操作一遍
  记下 3 句话：我改了什么、为什么这么改、结果是什么

周末 1 小时：回顾
  把这周看过的链路画一张流程图
  标注还没搞懂的点，下周重点攻
```

---

## 第七部分：学完之后你应该能回答的 10 个问题

用这个清单自测，全部能回答说明你已经入门后端：

1. 前端发的 `/demo-crud/list` 请求，后端哪个类的哪个方法在处理？
2. `@GetMapping` 和 `@PostMapping` 的区别是什么？
3. Controller 和 Service 为什么要分开？各自负责什么？
4. `ResponseUtil.okList()` 返回的 JSON 结构是什么？
5. 没有登录的请求为什么会返回 `A0223`？是哪个组件在拦截？
6. MyBatis Plus 的 `QueryWrapper` 是干什么用的？
7. `startPage(body)` 做了什么？为什么列表查询要调它？
8. 前端 `params` 和 `data` 的区别，对应后端接收参数的方式分别是什么？
9. 如果你要新增一个"标签管理"模块，需要建哪些文件？
10. `mvn clean package` 和 `mvn spring-boot:run` 的区别是什么？

---

## 一句话总结

**前端学后端的最快路径：先从你最熟悉的"调接口"反过来看后端怎么接，读懂一个 CRUD 的全链路，然后自己动手写一个。框架和理论放在最后学。**

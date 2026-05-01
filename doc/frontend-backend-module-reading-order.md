# 前端学习这个后端项目的模块阅读顺序

## 1. 这份文档解决什么问题

如果你是前端，第一次看这个后端项目，最容易犯的错不是看不懂代码，而是看代码顺序错了。

很多人一上来就去看：

1. 数据库层
2. 聚合启动模块
3. 各种公共组件
4. 一堆工具类

结果看了半天，知道文件很多，但不知道一个接口到底怎么跑。

正确做法不是“从底层开始”，而是“从你最熟悉的请求入口开始”。

所以这份文档只回答一个核心问题：

前端转后端，学习这个项目时，应该先看哪个模块，后看哪个模块。

---

## 2. 先给结论

如果你现在是前端，学习顺序建议固定为：

1. shopflow-admin
2. shopflow-admin-api
3. shopflow-core
4. shopflow-db
5. shopflow-all
6. shopflow-wx-api

这不是按“技术底层深度”排的，而是按“学习效率”排的。

原因很简单：

1. 你最熟的是页面发请求。
2. 所以最适合从前端 API 文件反向追后端 Controller。
3. 再从 Controller 追 Service。
4. 再追到 db 层。
5. 最后再理解聚合启动和多端复用。

也就是说，最适合你的学习路径不是：

数据库 -> 框架 -> 业务

而是：

页面请求 -> 接口入口 -> 业务逻辑 -> 数据查询 -> 启动装配

---

## 3. 为什么第一个看 shopflow-admin

很多人会问：

“我不是学后端吗，为什么先看前端模块？”

答案是：因为你本来就是前端，你应该从自己最熟悉的视角切进去。

前端模块的价值不是学 Vue，而是帮你快速回答这 3 个问题：

1. 页面请求发到哪里
2. 请求参数是什么
3. 页面期望后端返回什么结构

你先把这 3 件事搞清楚，再去看后端，会轻松很多。

### 3.1 在这个模块里重点看什么

第一优先级：

1. [shopflow-admin/src/api](shopflow-admin/src/api)
2. [shopflow-admin/src/utils/request.js](shopflow-admin/src/utils/request.js)
3. [shopflow-admin/src/views](shopflow-admin/src/views)
4. [shopflow-admin/src/router/index.js](shopflow-admin/src/router/index.js)

### 3.2 你要搞明白什么

1. 每个页面到底调了哪个接口。
2. 请求方式是 GET 还是 POST。
3. 参数是 params 还是 data。
4. 返回数据里页面实际依赖的是哪些字段。
5. token 是怎么自动带上的。

### 3.3 最推荐你先看的链路

先看登录链路：

1. [shopflow-admin/src/api/login.js](shopflow-admin/src/api/login.js)
2. [shopflow-admin/src/utils/request.js](shopflow-admin/src/utils/request.js)
3. [shopflow-admin/src/views/login/index.vue](shopflow-admin/src/views/login/index.vue)

然后再看一个最简单的 CRUD 列表页，比如：

1. [shopflow-admin/src/api/demoCrud.js](shopflow-admin/src/api/demoCrud.js)
2. [shopflow-admin/src/views/mall/demoCrud.vue](shopflow-admin/src/views/mall/demoCrud.vue)

你先从这里建立一个直觉：

前端调了什么，后端就一定有一个入口接住它。

---

## 4. 第二个看 shopflow-admin-api

这是你真正开始学后端的第一站。

原因：

1. 这个模块就是管理后台前端真正调用的后端接口。
2. 你刚才在前端看到的 URL，几乎都能在这里找到对应 Controller。
3. 这个模块最适合建立“一个请求在后端怎么跑”的主干认知。

### 4.1 这个模块你最该先看的目录

1. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web)
2. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service)
3. [shopflow-admin-api/src/main/resources/application.yml](shopflow-admin-api/src/main/resources/application.yml)

### 4.2 为什么先看 web 再看 service

因为前端最容易理解 Controller。

你可以把 Controller 理解成：

1. 接口入口
2. 参数接收层
3. 调 Service 的转发层
4. 响应返回层

而 Service 才是：

1. 业务判断
2. 参数校验
3. 组合查询
4. 调 db 层

### 4.3 建议你先看哪几个文件

第一组：登录与权限入口

1. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java)
2. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/model/auth/body/LoginBody.java](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/model/auth/body/LoginBody.java)

第二组：标准 CRUD 入口

1. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminIssueController.java](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminIssueController.java)
2. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service/AdminIssueService.java](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service/AdminIssueService.java)

第三组：你刚刚新增的教学 CRUD

1. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminDemoCrudController.java](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminDemoCrudController.java)
2. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service/AdminDemoCrudService.java](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service/AdminDemoCrudService.java)
3. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/model/democrud/body/DemoCrudListBody.java](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/model/democrud/body/DemoCrudListBody.java)

### 4.4 你要在这个模块里重点学会什么

1. URL 是怎么映射到 Controller 方法的。
2. 查询参数和请求体参数分别怎么接。
3. ResponseUtil 为什么能统一返回格式。
4. 为什么 Controller 里一般不直接写 SQL。
5. 为什么权限注解会影响接口是否可访问。

---

## 5. 第三个看 shopflow-core

当前端第一次开始学后端时，最容易忽略公共模块。

但你学到第二步之后，必须开始看 core，因为你会开始遇到这些问题：

1. 为什么没登录会返回 A0223。
2. 为什么所有接口返回结构都很像。
3. token 到底是谁校验的。
4. 为什么一些公共逻辑不写在 admin-api 里。

这些答案都在 core 模块里。

### 5.1 重点目录

1. [shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response](shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response)
2. [shopflow-core/src/main/java/org/ysling/shopflow/core/satoken](shopflow-core/src/main/java/org/ysling/shopflow/core/satoken)
3. [shopflow-core/src/main/java/org/ysling/shopflow/core/annotation](shopflow-core/src/main/java/org/ysling/shopflow/core/annotation)

### 5.2 最建议先看的文件

1. [shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java](shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java)
2. [shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java](shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java)
3. [shopflow-core/src/main/java/org/ysling/shopflow/core/annotation/JsonBody.java](shopflow-core/src/main/java/org/ysling/shopflow/core/annotation/JsonBody.java)

### 5.3 这个模块你不要一开始就全看

core 模块不是第一阶段全读的。

正确方式是：

1. 遇到统一返回问题，再看 ResponseUtil。
2. 遇到权限问题，再看 Sa-Token 配置。
3. 遇到注解不懂，再按需跳转看定义。

也就是说，core 应该“带着问题看”，而不是“扫目录式地看”。

---

## 6. 第四个看 shopflow-db

这是你真正理解“后端数据层”的地方。

但我不建议你一开始就先看它。

原因：

1. 前端看 db 层最容易迷路。
2. 实体、Mapper、ServiceImpl、分页、动态数据源会同时出现。
3. 如果你还没建立 Controller -> Service -> db 的主链路，容易直接看晕。

所以它应该放到第 4 步。

### 6.1 这个模块里看什么

1. domain 实体
2. mapper 接口
3. service impl
4. 数据源与数据库配置

### 6.2 重点路径

1. [shopflow-db/src/main/java/org/ysling/shopflow/db/domain](shopflow-db/src/main/java/org/ysling/shopflow/db/domain)
2. [shopflow-db/src/main/java/org/ysling/shopflow/db/mapper](shopflow-db/src/main/java/org/ysling/shopflow/db/mapper)
3. [shopflow-db/src/main/java/org/ysling/shopflow/db/service/impl](shopflow-db/src/main/java/org/ysling/shopflow/db/service/impl)
4. [shopflow-db/src/main/resources/application-db.yml](shopflow-db/src/main/resources/application-db.yml)

### 6.3 建议你先看哪几个文件

1. [shopflow-db/src/main/java/org/ysling/shopflow/db/domain/ShopflowIssue.java](shopflow-db/src/main/java/org/ysling/shopflow/db/domain/ShopflowIssue.java)
2. [shopflow-db/src/main/java/org/ysling/shopflow/db/mapper/IssueMapper.java](shopflow-db/src/main/java/org/ysling/shopflow/db/mapper/IssueMapper.java)
3. [shopflow-db/src/main/java/org/ysling/shopflow/db/service/impl/IssueServiceImpl.java](shopflow-db/src/main/java/org/ysling/shopflow/db/service/impl/IssueServiceImpl.java)

### 6.4 你在 db 模块要搞懂什么

1. 一个实体类是怎么对应数据库表的。
2. 一个 ServiceImpl 为什么能直接调用 add、deleteById、findById 这类方法。
3. 为什么前面 admin-api 的 Service 可以继承 db 模块里的实现类。
4. 分页是怎么进来的。

---

## 7. 第五个看 shopflow-all

这个模块不是前端转后端的第一学习重点。

因为它不是最能帮助你理解业务开发的模块，它更偏“聚合启动”和“统一部署”。

### 7.1 什么时候看它

等你已经理解：

1. 前端请求怎么到 admin-api
2. admin-api 怎么调 core
3. admin-api 怎么调 db

再来看 shopflow-all，就会很清楚它的意义：

它不是业务实现层，而是装配层。

### 7.2 重点看什么

1. [shopflow-all/pom.xml](shopflow-all/pom.xml)
2. [shopflow-all/src/main/resources/application.yml](shopflow-all/src/main/resources/application.yml)

### 7.3 你要明白什么

1. 为什么它能把多个模块一起启动。
2. 为什么有时候线上是跑它，而不是单独跑 admin-api。
3. 为什么改端口、改配置后会影响整个聚合服务。

---

## 8. 第六个看 shopflow-wx-api

它不是你入门后端的第一目标。

原因：

1. 你当前主要在做管理后台。
2. 先把 admin-api 吃透，收益最大。
3. wx-api 本质上是“另一端业务接口”，等你理解了一端，再看另一端会很快。

### 8.1 什么时候再看它

当你已经能独立看懂 admin-api 的一个 CRUD 模块后，再去看 wx-api。

这样你会发现：

1. 两边很多数据层是共用的。
2. 差异主要在 Controller 和业务规则。
3. 你会更容易理解“同一份 db/core，服务不同端”的架构方式。

---

## 9. 推荐你第一周就照着走的实际阅读顺序

下面这个顺序最适合你现在开始：

### 第 1 天：先从前端请求找后端入口

看：

1. [shopflow-admin/src/api/login.js](shopflow-admin/src/api/login.js)
2. [shopflow-admin/src/utils/request.js](shopflow-admin/src/utils/request.js)
3. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminAuthController.java)

目标：

1. 你要能说清登录请求从页面到后端怎么走。

### 第 2 天：看一个最简单 CRUD

看：

1. [shopflow-admin/src/api/demoCrud.js](shopflow-admin/src/api/demoCrud.js)
2. [shopflow-admin/src/views/mall/demoCrud.vue](shopflow-admin/src/views/mall/demoCrud.vue)
3. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminDemoCrudController.java](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminDemoCrudController.java)
4. [shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service/AdminDemoCrudService.java](shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service/AdminDemoCrudService.java)

目标：

1. 你要能说清列表、新增、修改、删除分别走哪个方法。

### 第 2 天补充：直接对照真实代码看一遍

如果你想最快理解“前端如何把一个 CRUD 请求送进后端”，就直接看我们现在已经落地的 `demoCrud` 示例。

建议你按下面顺序看。

#### 第一步：先看前端 API 怎么发请求

文件：`shopflow-admin/src/api/demoCrud.js`

```js
import request from '@/utils/request'

export function listDemoCrud(query) {
  return request({
	url: '/demo-crud/list',
	method: 'get',
	params: query
  })
}

export function createDemoCrud(data) {
  return request({
	url: '/demo-crud/create',
	method: 'post',
	data
  })
}
```

你这里要先看懂 3 件事：

1. 列表是 `GET`，新增是 `POST`。
2. 查询条件走 `params`，新增数据走 `data`。
3. 前端写的是 `/demo-crud/list`，但真正到后端时会带上 `/admin` 前缀。

也就是说，前端这一层的任务只有一句话：

把页面数据组装好，然后发给指定 URL。

#### 第二步：看后端 Controller 怎么接请求

文件：`shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminDemoCrudController.java`

```java
@RestController
@RequestMapping("/admin/demo-crud")
@Validated
public class AdminDemoCrudController {

	@Autowired
	private AdminDemoCrudService demoCrudService;

	@GetMapping("/list")
	public Object list(DemoCrudListBody body) {
		return ResponseUtil.okList(demoCrudService.querySelective(body));
	}

	@PostMapping("/create")
	public Object create(@Valid @RequestBody ShopflowIssue issue) {
		Object error = demoCrudService.validate(issue);
		if (error != null) {
			return error;
		}
		if (demoCrudService.add(issue) == 0) {
			return ResponseUtil.addDataFailed();
		}
		return ResponseUtil.ok();
	}
}
```

这一段你重点理解：

1. 类上的 `@RequestMapping("/admin/demo-crud")` 是总前缀。
2. `@GetMapping("/list")` 和 `@PostMapping("/create")` 是具体接口。
3. Controller 自己不写数据库逻辑，它只负责“接参数 -> 调 service -> 返回结果”。

前端出身的人在这里最容易进步，因为这层很像前端里的“事件分发层”。

#### 第三步：看 Service 怎么做业务处理

文件：`shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service/AdminDemoCrudService.java`

```java
@Service
@CacheConfig(cacheNames = "shopflow_demo_crud")
public class AdminDemoCrudService extends IssueServiceImpl {

	public Object validate(ShopflowIssue issue) {
		String question = issue.getQuestion();
		if (Objects.isNull(question)) {
			return ResponseUtil.badArgument();
		}
		String answer = issue.getAnswer();
		if (Objects.isNull(answer)) {
			return ResponseUtil.badArgument();
		}
		return null;
	}

	public List<ShopflowIssue> querySelective(DemoCrudListBody body) {
		QueryWrapper<ShopflowIssue> wrapper = startPage(body);
		if (StringUtils.hasText(body.getQuestion())) {
			wrapper.like(ShopflowIssue.QUESTION, body.getQuestion());
		}
		return queryAll(wrapper);
	}
}
```

这一层你要重点建立 4 个认知：

1. `validate` 是业务校验，不应该堆在 Controller。
2. `querySelective` 是列表查询逻辑的真正核心。
3. `startPage(body)` 说明分页不是前端自己分页，而是后端查分页数据。
4. `wrapper.like(...)` 说明模糊查询条件是在后端拼出来的。

#### 第四步：把一条“列表查询”链路完整串起来

你可以把这条链路背下来：

1. 页面点击“查询”。
2. 页面调用 `listDemoCrud(listQuery)`。
3. 前端发 `GET /demo-crud/list?page=1&limit=10&question=xx`。
4. 代理把请求转成 `/admin/demo-crud/list`。
5. `AdminDemoCrudController.list()` 接收 `DemoCrudListBody`。
6. `AdminDemoCrudService.querySelective()` 根据 `question` 拼装查询条件。
7. `ResponseUtil.okList(...)` 返回统一分页结构。
8. 前端表格拿 `res.data.list` 和 `res.data.total` 渲染页面。

如果你能把上面 8 步用自己的话讲出来，你就已经不是“只会调接口”，而是已经开始真正理解后端了。

### 第 3 天：顺着 CRUD 继续追到 db 层

看：

1. [shopflow-db/src/main/java/org/ysling/shopflow/db/domain/ShopflowIssue.java](shopflow-db/src/main/java/org/ysling/shopflow/db/domain/ShopflowIssue.java)
2. [shopflow-db/src/main/java/org/ysling/shopflow/db/service/impl/IssueServiceImpl.java](shopflow-db/src/main/java/org/ysling/shopflow/db/service/impl/IssueServiceImpl.java)
3. [shopflow-db/src/main/java/org/ysling/shopflow/db/mapper/IssueMapper.java](shopflow-db/src/main/java/org/ysling/shopflow/db/mapper/IssueMapper.java)

目标：

1. 你要能说清为什么 admin-api 的 Service 不需要自己写 SQL 也能跑 CRUD。

### 第 4 天：理解统一返回与权限

看：

1. [shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java](shopflow-core/src/main/java/org/ysling/shopflow/core/utils/response/ResponseUtil.java)
2. [shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java](shopflow-core/src/main/java/org/ysling/shopflow/core/satoken/config/SaTokenConfigure.java)

目标：

1. 你要能说清为什么接口要么成功返回统一结构，要么未登录直接拦下。

### 第 5 天：回头看配置与启动

看：

1. [shopflow-admin-api/src/main/resources/application.yml](shopflow-admin-api/src/main/resources/application.yml)
2. [shopflow-db/src/main/resources/application-db.yml](shopflow-db/src/main/resources/application-db.yml)
3. [shopflow-all/src/main/resources/application.yml](shopflow-all/src/main/resources/application.yml)

目标：

1. 你要能说清端口、数据库、Redis、聚合启动分别在哪管。

---

## 10. 前端转后端最容易看错的地方

### 10.1 一上来先读 db 模块

问题：

你会看到很多实体、Mapper、继承关系、框架封装，但不知道它们是给哪个接口服务的。

正确做法：

先从 Controller 找到具体调用链，再下钻到 db。

### 10.2 一上来就读 core 全家桶

问题：

core 里公共能力很多，第一次看容易信息过载。

正确做法：

遇到统一返回、鉴权、注解时再按问题回查。

### 10.3 一上来就看 shopflow-all

问题：

你会过早陷入配置和启动装配，反而没学到真正业务开发主链路。

正确做法：

先把 admin-api 看懂，再回头理解 all。

---

## 11. 你学这个项目时最值得优先建立的三个认知

### 11.1 第一个认知：后端不是从数据库开始学

对你来说，后端应该从“接口入口”开始学。

### 11.2 第二个认知：先看业务主链路，不要先看工具细节

主链路就是：

前端 API -> Controller -> Service -> db -> ResponseUtil

### 11.3 第三个认知：先吃透一个模块，比扫十个目录更有用

你现在最应该吃透的第一个模块，不是整个项目，而是：

1. shopflow-admin-api 里的一个完整 CRUD 模块
2. 配套前端页面
3. 对应 db 层实现

这个闭环跑通后，你再学别的模块会快很多。

---

## 12. 最终建议

如果你只能选一个模块先学，答案就是：

1. 先从 shopflow-admin 对应的调用入口开始看
2. 立刻切到 shopflow-admin-api
3. 然后按需进入 shopflow-core 和 shopflow-db

如果你只能选一个“后端模块”先学，答案就是：

1. shopflow-admin-api

因为它最接近你当前前端工作的真实联调场景，也最容易让你建立后端开发的核心直觉。

---

## 13. 一句话版本

前端学习这个项目的最佳起点不是 db，也不是 all，而是先从前端请求找到管理端接口，再重点吃透 shopflow-admin-api，然后带着问题去看 core 和 db。

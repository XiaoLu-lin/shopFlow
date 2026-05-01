# 阶段 2：读懂一个完整 CRUD — 从前端到数据库的逐行拆解

> 对应 [前端学后端完整指南](frontend-learn-backend-guide.md) → 阶段 2  
> 目标：你能追踪一个增删改查模块的全部代码，从前端到数据库

---

## 目录

- [0. 开始之前：你要看的 7 个文件](#0-开始之前你要看的-7-个文件)
- [1. 全景地图：一个 CRUD 请求的完整旅程](#1-全景地图一个-crud-请求的完整旅程)
- [2. 前端层：API 定义 + 页面组件](#2-前端层api-定义--页面组件)
  - [2.1 API 文件 — demoCrud.js](#21-api-文件--democrudjs)
  - [2.2 页面组件 — demoCrud.vue](#22-页面组件--democrudvue)
- [3. 后端 Controller 层：AdminDemoCrudController.java](#3-后端-controller-层admindemocrudcontrollerjava)
- [4. 后端 Service 层：AdminDemoCrudService.java](#4-后端-service-层admindemocrudservicejava)
- [5. 请求参数定义：DemoCrudListBody.java + PageBody.java](#5-请求参数定义democrudlistbodyjava--pagebodyjava)
- [6. 数据库实体：ShopflowIssue.java](#6-数据库实体shopflowissuejava)
- [7. 数据库服务层：IssueServiceImpl.java](#7-数据库服务层issueserviceimpljava)
- [8. 继承链全景：为什么你只写 30 行就拥有了完整 CRUD](#8-继承链全景为什么你只写-30-行就拥有了完整-crud)
- [9. 五大操作完整链路追踪](#9-五大操作完整链路追踪)
- [10. 缓存策略详解](#10-缓存策略详解)
- [11. 自测清单](#11-自测清单)

---

## 0. 开始之前：你要看的 7 个文件

按照请求从前端到数据库的顺序排列：

| 序号 | 层 | 文件 | 行数 | 你要搞明白什么 |
|------|-----|------|------|--------------|
| 1 | 前端 API | [demoCrud.js](../shopflow-admin/src/api/demoCrud.js) | 41 | 5 个接口分别用什么 HTTP 方法 |
| 2 | 前端页面 | [demoCrud.vue](../shopflow-admin/src/views/mall/demoCrud.vue) | 168 | 页面怎么调 API、怎么传参、怎么用返回数据 |
| 3 | 后端 Controller | [AdminDemoCrudController.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminDemoCrudController.java) | 99 | 5 个接口方法的路由、权限、参数怎么接 |
| 4 | 后端 Service | [AdminDemoCrudService.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service/AdminDemoCrudService.java) | 43 | 校验和查询的业务逻辑 |
| 5 | 请求参数 | [DemoCrudListBody.java](../shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/model/democrud/body/DemoCrudListBody.java) | 18 | 列表查询参数怎么定义 |
| 6 | 数据库实体 | [ShopflowIssue.java](../shopflow-db/src/main/java/org/ysling/shopflow/db/domain/ShopflowIssue.java) | 111 | Java 类怎么对应数据库表 |
| 7 | 数据库 Service | [IssueServiceImpl.java](../shopflow-db/src/main/java/org/ysling/shopflow/db/service/impl/IssueServiceImpl.java) | 240 | 基础 CRUD 和缓存策略 |

**另外 2 个辅助文件**（被继承/引用的）：

| 文件 | 作用 |
|------|------|
| [PageBody.java](../shopflow-db/src/main/java/org/ysling/shopflow/db/entity/PageBody.java) | 分页查询参数基类（DemoCrudListBody 的父类） |
| [IBaseServiceImpl.java](../shopflow-db/src/main/java/org/ysling/shopflow/db/mybatis/IBaseServiceImpl.java) | 自定义通用服务基类（IssueServiceImpl 的父类） |

---

## 1. 全景地图：一个 CRUD 请求的完整旅程

以"查询列表"为例，请求从你点击浏览器到数据库返回数据的完整链路：

```
┌──────────────────────────────────────────────────────────────────────────┐
│                           你在浏览器上点击"查询"                           │
└──────────────────────────────┬───────────────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  ① 前端 demoCrud.vue                                                     │
│     this.getList()                                                       │
│       → listDemoCrud({ page: 1, limit: 10, question: '测试' })           │
└──────────────────────────────┬───────────────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  ② 前端 demoCrud.js（API 层）                                            │
│     GET /demo-crud/list?page=1&limit=10&question=测试                    │
│     自动带上 Header: X-ShopFlow-Admin-Token: xxxx                        │
└──────────────────────────────┬───────────────────────────────────────────┘
                               │ vue.config.js 代理 → http://localhost:6914
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  ③ 后端 AdminDemoCrudController.list()                                   │
│     @GetMapping("/list")                                                 │
│     接收参数 → DemoCrudListBody body（Spring 自动把 URL 参数映射进去）       │
│     调用 → demoCrudService.querySelective(body)                          │
└──────────────────────────────┬───────────────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  ④ 后端 AdminDemoCrudService.querySelective()                            │
│     1. startPage(body)  → 设置分页（page=1, limit=10, sort=add_time）    │
│     2. wrapper.like("question", "测试")  → 模糊搜索条件                   │
│     3. queryAll(wrapper)  → 调父类 IssueServiceImpl 的方法               │
└──────────────────────────────┬───────────────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  ⑤ IssueServiceImpl.queryAll()                                           │
│     @Cacheable → 先查 Redis 缓存                                        │
│       ├── 缓存命中 → 直接返回                                             │
│       └── 缓存未命中 → getBaseMapper().selectList(wrapper)               │
│                           │                                              │
│                           ▼                                              │
│                    IssueMapper（MyBatis Plus）                             │
│                           │                                              │
│                           ▼                                              │
│                   ┌──────────────────────────────────────────┐            │
│                   │ MySQL 执行：                              │            │
│                   │ SELECT * FROM shopflow_issue              │            │
│                   │ WHERE deleted = 0                        │            │
│                   │   AND question LIKE '%测试%'              │            │
│                   │ ORDER BY add_time DESC                   │            │
│                   │ LIMIT 0, 10                              │            │
│                   └──────────────────────────────────────────┘            │
└──────────────────────────────┬───────────────────────────────────────────┘
                               │ 数据库返回 List<ShopflowIssue>
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  ⑥ Controller 调用 ResponseUtil.okList(list)                             │
│     封装成统一响应格式：                                                    │
│     {                                                                    │
│       "errno": "success",                                                │
│       "errmsg": "成功",                                                   │
│       "data": {                                                          │
│         "list": [ { id, question, answer, addTime, ... }, ... ],         │
│         "total": 50,                                                     │
│         "pages": 5                                                       │
│       }                                                                  │
│     }                                                                    │
└──────────────────────────────┬───────────────────────────────────────────┘
                               │ JSON 响应
                               ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  ⑦ 前端 demoCrud.vue 拿到响应                                            │
│     this.list = res.data.list    → 表格数据                              │
│     this.total = res.data.total  → 分页总数                              │
│     页面渲染 el-table                                                     │
└──────────────────────────────────────────────────────────────────────────┘
```

**记住这条链路。** 后端学习的 80% 问题都能在这条链路上找到答案。

---

## 2. 前端层：API 定义 + 页面组件

### 2.1 API 文件 — demoCrud.js

> 文件路径：`shopflow-admin/src/api/demoCrud.js`（41 行）

这个文件定义了 5 个函数，对应 CRUD 的 5 个操作：

```javascript
// ① 查列表 — GET 请求，参数在 URL 上（params）
export function listDemoCrud(query) {
  return request({
    url: '/demo-crud/list',
    method: 'get',
    params: query    // → ?page=1&limit=10&question=xxx
  })
}

// ② 查详情 — GET 请求，只传 id
export function readDemoCrud(id) {
  return request({
    url: '/demo-crud/read',
    method: 'get',
    params: { id }   // → ?id=xxx
  })
}

// ③ 新增 — POST 请求，参数在请求体里（data = JSON）
export function createDemoCrud(data) {
  return request({
    url: '/demo-crud/create',
    method: 'post',
    data              // → body: { question: "xxx", answer: "xxx" }
  })
}

// ④ 编辑 — POST 请求，参数在请求体里
export function updateDemoCrud(data) {
  return request({
    url: '/demo-crud/update',
    method: 'post',
    data              // → body: { id: "xxx", question: "xxx", answer: "xxx" }
  })
}

// ⑤ 删除 — POST 请求，id 在 URL 上
export function deleteDemoCrud(id) {
  return request({
    url: '/demo-crud/delete',
    method: 'post',
    params: { id }   // → ?id=xxx
  })
}
```

**你要注意的规律**：

| 操作 | HTTP 方法 | 参数传递方式 | 为什么 |
|------|----------|------------|--------|
| 查列表 | GET | `params`（URL 参数） | 查询是幂等的，GET 符合语义 |
| 查详情 | GET | `params`（URL 参数） | 同上 |
| 新增 | POST | `data`（JSON body） | 要传完整对象，body 更合适 |
| 编辑 | POST | `data`（JSON body） | 同上 |
| 删除 | POST | `params`（URL 参数） | 只需要传 id |

**前端 `params` vs `data`**：
- `params` → 拼到 URL 后面：`/list?page=1&limit=10`
- `data` → 放在请求体（body）里，JSON 格式：`{ "question": "xxx" }`

### 2.2 页面组件 — demoCrud.vue

> 文件路径：`shopflow-admin/src/views/mall/demoCrud.vue`（168 行）

这是你最熟悉的部分——一个标准的 Element UI 管理后台列表页。拆成几块看：

#### 模板部分（你一看就懂）

```html
<!-- 搜索栏：一个输入框 + 查询按钮 + 新增按钮 -->
<el-form :model="listQuery" size="small" :inline="true">
  <el-form-item label="问题关键字">
    <el-input v-model="listQuery.question" placeholder="输入问题关键字" />
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="handleFilter">查询</el-button>
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="handleCreate">新增</el-button>
  </el-form-item>
</el-form>

<!-- 表格：4 列（ID、问题、回复、操作） -->
<el-table :data="list">
  <el-table-column label="ID" prop="id" />
  <el-table-column label="问题" prop="question" />
  <el-table-column label="回复" prop="answer" />
  <el-table-column label="操作">
    <el-button @click="handleUpdate(scope.row)">编辑</el-button>
    <el-button @click="handleDelete(scope.row)">删除</el-button>
  </el-table-column>
</el-table>

<!-- 分页组件 -->
<pagination :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit"
            @pagination="getList" />
```

#### 数据部分（关键！这就是传给后端的参数）

```javascript
data() {
  return {
    list: [],        // 表格数据（从后端拿回来的）
    total: 0,        // 总记录数（分页用）
    listQuery: {
      page: 1,       // ← 对应后端 PageBody.page
      limit: 10,     // ← 对应后端 PageBody.limit
      question: undefined,  // ← 对应后端 DemoCrudListBody.question
      sort: 'add_time',     // ← 对应后端 PageBody.sort
      order: 'desc'         // ← 对应后端 PageBody.order
    },
    form: {
      id: undefined,
      question: '',
      answer: ''
    },
    rules: {
      question: [{ required: true, message: '请输入问题', trigger: 'blur' }],
      answer: [{ required: true, message: '请输入回复', trigger: 'blur' }]
    }
  }
}
```

**前后端参数对应关系**：

```
前端 listQuery                    后端 DemoCrudListBody（extends PageBody）
──────────────                   ──────────────────────────────────────────
listQuery.page       ──────→     body.page        (Integer, 默认 1)
listQuery.limit      ──────→     body.limit       (Integer, 默认 10)
listQuery.question   ──────→     body.question    (String)
listQuery.sort       ──────→     body.sort        (String, 默认 "add_time")
listQuery.order      ──────→     body.order       (String, 默认 "desc")
```

#### 方法部分（核心逻辑）

```javascript
methods: {
  // 查列表 → 调后端 /list 接口
  async getList() {
    this.listLoading = true
    const res = await listDemoCrud(this.listQuery)  // 传 listQuery 整个对象
    this.list = res.data.list     // 后端返回的 data.list
    this.total = res.data.total   // 后端返回的 data.total
    this.listLoading = false
  },

  // 点击"查询"按钮 → 重置页码到第 1 页再查
  handleFilter() {
    this.listQuery.page = 1
    this.getList()
  },

  // 点击"新增"按钮 → 打开弹窗，mode = create
  handleCreate() {
    this.mode = 'create'
    this.form = { id: undefined, question: '', answer: '' }
    this.dialogVisible = true
  },

  // 点击"编辑"按钮 → 打开弹窗，mode = update，填入当前行数据
  handleUpdate(row) {
    this.mode = 'update'
    this.form = { id: row.id, question: row.question, answer: row.answer }
    this.dialogVisible = true
  },

  // 弹窗确认 → 根据 mode 决定调 create 还是 update 接口
  submitForm() {
    this.$refs.formRef.validate(async valid => {
      if (!valid) return
      if (this.mode === 'create') {
        await createDemoCrud(this.form)     // → POST /create  body: { question, answer }
      } else {
        await updateDemoCrud(this.form)     // → POST /update  body: { id, question, answer }
      }
      this.dialogVisible = false
      this.getList()  // 操作完成后刷新列表
    })
  },

  // 点击"删除" → 确认后调 delete 接口
  handleDelete(row) {
    this.$confirm('确定删除这条数据吗？')
      .then(async () => {
        await deleteDemoCrud(row.id)        // → POST /delete?id=xxx
        this.getList()  // 删除后刷新列表
      })
  }
}
```

**重点**：每次写操作（新增/编辑/删除）成功后，都会调 `this.getList()` 重新刷新列表，保证页面数据和数据库一致。

---

## 3. 后端 Controller 层：AdminDemoCrudController.java

> 文件路径：`shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/web/AdminDemoCrudController.java`（99 行）

### 类定义（第 24-28 行）

```java
@Slf4j                                    // 自动生成日志对象 log
@RestController                           // 标记为 REST 控制器（返回 JSON，不是页面）
@RequestMapping("/admin/demo-crud")       // 路由前缀：所有接口都以 /admin/demo-crud 开头
@Validated                                // 开启参数校验
public class AdminDemoCrudController {

    @Autowired                            // 自动注入服务（你理解成 import + 全局注册）
    private AdminDemoCrudService demoCrudService;
```

**前端类比**：

| Java | 前端类比 |
|------|---------|
| `@RestController` | 相当于一个专门处理 API 请求的模块 |
| `@RequestMapping("/admin/demo-crud")` | 类似 Vue Router 的 `path: '/admin/demo-crud'` |
| `@Autowired private AdminDemoCrudService` | 类似 `import { xxxService } from '...'` |

### 5 个接口方法逐个拆解

#### 接口 1：查列表 — list()

```java
@SaCheckPermission("admin:issue:list")                              // 权限检查
@RequiresPermissionsDesc(menu = {"商城管理", "CRUD练习"}, button = "查询")  // 权限描述（给管理界面用）
@GetMapping("/list")                                                // 处理 GET /admin/demo-crud/list
public Object list(DemoCrudListBody body) {                         // ← Spring 自动把 URL 参数映射到 body
    return ResponseUtil.okList(demoCrudService.querySelective(body));
}
```

**逐行解读**：
1. `@SaCheckPermission("admin:issue:list")` — 检查当前登录用户是否有"查询"权限，没有则直接返回 403
2. `@GetMapping("/list")` — 只处理 GET 请求，完整路径 = 类上的 `/admin/demo-crud` + 方法上的 `/list`
3. `DemoCrudListBody body` — **没有** `@RequestBody`，说明参数从 URL 查询参数来（`?page=1&limit=10&question=xxx`）
4. `demoCrudService.querySelective(body)` — 调 Service 层做带条件的分页查询
5. `ResponseUtil.okList(...)` — 把列表数据包装成 `{ errno: "success", data: { list, total, pages } }`

**前端请求对应**：

```javascript
// 前端
listDemoCrud({ page: 1, limit: 10, question: '测试' })
// 实际发出：GET /admin/demo-crud/list?page=1&limit=10&question=测试
```

#### 接口 2：查详情 — read()

```java
@SaCheckPermission("admin:issue:read")
@RequiresPermissionsDesc(menu = {"商城管理", "CRUD练习"}, button = "详情")
@GetMapping("/read")
public Object read(@JsonBody String id) {           // @JsonBody 从参数中提取 id
    return ResponseUtil.ok(demoCrudService.findById(id));
}
```

**逐行解读**：
1. `@JsonBody String id` — 从请求参数中提取 `id`（`@JsonBody` 是项目自定义注解）
2. `demoCrudService.findById(id)` — 按 ID 查一条记录
3. `ResponseUtil.ok(...)` — 包装成 `{ errno: "success", data: { 单条记录 } }`

#### 接口 3：新增 — create()

```java
@SaCheckPermission("admin:issue:create")
@RequiresPermissionsDesc(menu = {"商城管理", "CRUD练习"}, button = "添加")
@PostMapping("/create")
public Object create(@Valid @RequestBody ShopflowIssue issue) {     // ← 从 JSON body 接收
    Object error = demoCrudService.validate(issue);                 // 1. 校验
    if (error != null) {
        return error;                                               // 校验失败，返回错误
    }
    if (demoCrudService.add(issue) == 0) {                          // 2. 插入数据库
        return ResponseUtil.addDataFailed();                        // 插入失败
    }
    return ResponseUtil.ok();                                       // 3. 返回成功
}
```

**逐行解读**：
1. `@PostMapping("/create")` — 处理 POST 请求
2. `@Valid` — 触发 Java Bean Validation（实体类上的校验注解）
3. `@RequestBody ShopflowIssue issue` — 从请求体的 JSON 自动转成 Java 对象
4. **三步流程**：校验 → 插入 → 返回

**前端请求对应**：

```javascript
// 前端
createDemoCrud({ question: '如何退款？', answer: '请联系客服' })
// 实际发出：POST /admin/demo-crud/create
// body: { "question": "如何退款？", "answer": "请联系客服" }
```

#### 接口 4：编辑 — update()

```java
@SaCheckPermission("admin:issue:update")
@RequiresPermissionsDesc(menu = {"商城管理", "CRUD练习"}, button = "编辑")
@PostMapping("/update")
public Object update(@Valid @RequestBody ShopflowIssue issue) {
    Object error = demoCrudService.validate(issue);
    if (error != null) {
        return error;
    }
    if (demoCrudService.updateVersionSelective(issue) == 0) {       // 带版本号更新
        return ResponseUtil.updatedDataFailed();
    }
    return ResponseUtil.ok();
}
```

和 create 结构完全一样，区别在于：
- 调的是 `updateVersionSelective` 而不是 `add`
- 前端传的数据会多一个 `id` 字段（标识要更新哪条记录）

#### 接口 5：删除 — delete()

```java
@SaCheckPermission("admin:issue:delete")
@RequiresPermissionsDesc(menu = {"商城管理", "CRUD练习"}, button = "删除")
@PostMapping("/delete")
public Object delete(@JsonBody String id) {
    if (demoCrudService.deleteById(id) == 0) {
        return ResponseUtil.deletedDataFailed();
    }
    return ResponseUtil.ok();
}
```

最简单的接口——只需要接收一个 `id`，调 `deleteById` 删除。

### Controller 总结

5 个接口的固定模式：

```
Controller 方法 = 权限检查 + 接参数 + 调 Service + 包装响应

它不做：
  ✗ 不写业务逻辑（交给 Service）
  ✗ 不操作数据库（交给 ServiceImpl）
  ✗ 不处理缓存（交给注解）

它只做：
  ✓ 定义 URL 路径
  ✓ 定义 HTTP 方法
  ✓ 接收参数
  ✓ 调用 Service
  ✓ 返回统一响应
```

---

## 4. 后端 Service 层：AdminDemoCrudService.java

> 文件路径：`shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/service/AdminDemoCrudService.java`（43 行）

这是整个 CRUD 最核心的文件——但只有 43 行。

### 类定义（第 19-21 行）

```java
@Service                                         // 注册为 Spring 服务
@CacheConfig(cacheNames = "shopflow_demo_crud")  // 缓存命名空间
public class AdminDemoCrudService extends IssueServiceImpl {
```

关键：`extends IssueServiceImpl`。通过继承，它直接拥有了父类的所有 30+ 个方法（findById、add、deleteById、updateVersionSelective...），自己只需要写特有的逻辑。

**前端类比**：

```javascript
// 类似于：
class AdminDemoCrudService extends IssueService {
  // 父类已经有了 findById、add、deleteById、updateVersionSelective...
  // 我只需要写我特有的 validate 和 querySelective
}
```

### 方法 1：validate() — 参数校验

```java
public Object validate(ShopflowIssue issue) {
    String question = issue.getQuestion();        // 取出 question
    if (Objects.isNull(question)) {               // 如果 question 为空
        return ResponseUtil.badArgument();         //   返回"参数不对"
    }
    String answer = issue.getAnswer();             // 取出 answer
    if (Objects.isNull(answer)) {                  // 如果 answer 为空
        return ResponseUtil.badArgument();          //   返回"参数不对"
    }
    return null;                                   // 校验通过，返回 null
}
```

**设计逻辑**：
- 返回 `null` = 校验通过
- 返回非 `null` = 校验失败（返回的是错误响应对象）
- Controller 里用 `if (error != null) return error;` 来判断

**前端类比**：类似 Element UI 表单校验 rules 的后端版本。前端校验防用户误操作，后端校验防非法请求。

### 方法 2：querySelective() — 带条件的分页查询

```java
@Cacheable(sync = true)                                     // 结果会被缓存
public List<ShopflowIssue> querySelective(DemoCrudListBody body) {
    QueryWrapper<ShopflowIssue> wrapper = startPage(body);   // 1. 设置分页
    if (StringUtils.hasText(body.getQuestion())) {           // 2. 如果 question 有值
        wrapper.like(ShopflowIssue.QUESTION, body.getQuestion());  // 加模糊搜索
    }
    return queryAll(wrapper);                                // 3. 执行查询
}
```

这三行代码做了三件事：

**第 1 行：`startPage(body)`**

```java
QueryWrapper<ShopflowIssue> wrapper = startPage(body);
```

`startPage` 是从父类继承来的方法，它做了：
1. 从 `body` 里取出 `page`、`limit`、`sort`、`order`
2. 创建一个 `QueryWrapper`，并设置好分页和排序
3. 相当于 SQL 里的 `ORDER BY add_time DESC LIMIT 0, 10`

**第 2 行：`wrapper.like(...)`**

```java
if (StringUtils.hasText(body.getQuestion())) {
    wrapper.like(ShopflowIssue.QUESTION, body.getQuestion());
}
```

`wrapper.like("question", "测试")` 生成的 SQL：`WHERE question LIKE '%测试%'`

注意 `if` 判断：如果前端没传 `question` 或传了空字符串，就不加这个条件——这就是"动态查询"。

**前端类比**：

```javascript
// 前端动态拼接请求参数的逻辑
const params = { page: 1, limit: 10 }
if (this.listQuery.question) {
  params.question = this.listQuery.question  // 有搜索词才加这个参数
}
```

后端也在做一样的事，只不过是在拼 SQL 条件。

**第 3 行：`queryAll(wrapper)`**

调用父类 `IssueServiceImpl` 的 `queryAll` 方法，执行最终的数据库查询。

### Service 的调用关系图

```
Controller 调用                  Service 方法          实际执行者
──────────────                  ──────────            ──────────
list()    → querySelective()    → queryAll()          → IssueServiceImpl
read()    → findById()          (继承来的)             → IssueServiceImpl
create()  → validate() + add() (validate 自己写，add 继承来的) → IssueServiceImpl
update()  → validate() + updateVersionSelective()     → IssueServiceImpl
delete()  → deleteById()       (继承来的)              → IssueServiceImpl
```

---

## 5. 请求参数定义：DemoCrudListBody.java + PageBody.java

### DemoCrudListBody — 列表查询的参数

> 文件路径：`shopflow-admin-api/src/main/java/org/ysling/shopflow/admin/model/democrud/body/DemoCrudListBody.java`（18 行）

```java
@Data                                   // 自动生成 getter/setter/toString/equals/hashCode
@EqualsAndHashCode(callSuper = true)    // equals/hashCode 计算时包含父类字段
public class DemoCrudListBody extends PageBody {

    /**
     * 问题关键字
     */
    private String question;            // 自己只定义了一个搜索字段
}
```

只有 18 行，但因为 `extends PageBody`，它实际包含 6 个字段。

### PageBody — 分页查询公共基类

> 文件路径：`shopflow-db/src/main/java/org/ysling/shopflow/db/entity/PageBody.java`（101 行）

```java
@Data
@NoArgsConstructor
public class PageBody implements Serializable {

    public static final String ID = "`id`";

    private String id;                           // 通用 ID

    /**
     * 分页页码  (默认：1)
     */
    private Integer page = 1;

    /**
     * 查询数量  (默认：10) (0:查询全部)
     */
    private Integer limit = 10;

    /**
     * 排序字段  (默认：add_time)
     */
    @Sort                                        // 自定义校验：只允许合法的排序字段
    private String sort = "add_time";

    /**
     * 正序或倒叙排序，desc -- asc （默认：desc）
     */
    @Order                                       // 自定义校验：只允许 desc 或 asc
    private String order = "desc";
}
```

### DemoCrudListBody 的完整字段

合并后，`DemoCrudListBody` 拥有的全部字段：

```
DemoCrudListBody 的完整字段：
  ├── question   ← 自己定义的（搜索关键字）
  ├── page       ← 从 PageBody 继承（第几页，默认 1）
  ├── limit      ← 从 PageBody 继承（每页几条，默认 10）
  ├── sort       ← 从 PageBody 继承（排序字段，默认 add_time）
  ├── order      ← 从 PageBody 继承（排序方向，默认 desc）
  └── id         ← 从 PageBody 继承（通用 ID）
```

### 前端请求 → 参数自动映射

当前端发起请求：

```
GET /admin/demo-crud/list?page=2&limit=20&question=测试&sort=add_time&order=desc
```

Spring Boot 自动映射（不需要写任何解析代码）：

```
URL 参数               Java 字段
─────────             ──────────
page=2          →     body.page = 2
limit=20        →     body.limit = 20
question=测试    →     body.question = "测试"
sort=add_time   →     body.sort = "add_time"
order=desc      →     body.order = "desc"
```

### 这个设计模式的价值

项目里每个列表接口都有自己的 `XxxListBody`，但全部继承 `PageBody`：

```
PageBody（分页基类 — 5 个通用字段）
  ├── DemoCrudListBody    → 加了 question
  ├── AdListBody          → 加了 name、content
  ├── UserListBody        → 加了 username、mobile
  ├── GoodsListBody       → 加了 goodsName、categoryId
  └── ...更多模块
```

**好处**：分页、排序逻辑只写一次（在 PageBody + startPage 里），每个模块只需要定义自己特有的搜索字段。跟前端的 mixin 一个思路。

---

## 6. 数据库实体：ShopflowIssue.java

> 文件路径：`shopflow-db/src/main/java/org/ysling/shopflow/db/domain/ShopflowIssue.java`（111 行）

这个类 = 一张数据库表的 Java 映射。

### 类定义

```java
@Data                              // getter/setter/toString
@Builder                           // 提供建造者模式：ShopflowIssue.builder().question("xx").build()
@AllArgsConstructor                // 全参数构造函数
@NoArgsConstructor                 // 无参数构造函数
@TableName("shopflow_issue")       // ← 关键：对应数据库中的 shopflow_issue 表
public class ShopflowIssue implements Serializable {
```

### 字段 ↔ 数据库列对应关系

```java
// Java 字段                        // 数据库列                 // 说明
@TableId("`id`")
private String id;                 // id                      → 主键

@TableField("`question`")
private String question;           // question                → 问题标题

@TableField("`answer`")
private String answer;             // answer                  → 问题答案

@TableField(value = "`add_time`", fill = FieldFill.INSERT)
private LocalDateTime addTime;     // add_time                → 创建时间（自动填充）

@TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updateTime;  // update_time             → 更新时间（自动填充）

@TableField("`deleted`")
@TableLogic                        // ← 逻辑删除标记
private Boolean deleted;           // deleted                 → 是否删除（0=未删，1=已删）

@TableField("`tenant_id`")
private String tenantId;           // tenant_id               → 租户ID（多租户隔离）

@TableField("`version`")
@Version                           // ← 乐观锁标记
private Integer version;           // version                 → 版本号（并发控制）
```

### 重要注解详解

#### `@TableLogic` — 逻辑删除

```java
@TableLogic
private Boolean deleted;
```

加了这个注解后，MyBatis Plus 的行为会变：

| 操作 | 没有 @TableLogic 时的 SQL | 有 @TableLogic 时的 SQL |
|------|--------------------------|------------------------|
| 删除 | `DELETE FROM shopflow_issue WHERE id = ?` | `UPDATE shopflow_issue SET deleted = 1 WHERE id = ?` |
| 查询 | `SELECT * FROM shopflow_issue` | `SELECT * FROM shopflow_issue WHERE deleted = 0` |

**一句话**：删除不是真删，只是标记为"已删除"。查询时自动过滤掉已删除的记录。

#### `@Version` — 乐观锁

```java
@Version
private Integer version;
```

防止两个人同时编辑同一条数据导致覆盖。更新时 MyBatis Plus 自动加版本号判断：

```sql
-- 实际执行的 SQL：
UPDATE shopflow_issue
SET question = '新值', version = version + 1
WHERE id = '123' AND version = 2     -- 只有版本号匹配才能更新成功

-- 如果另一个人已经先改过（version 变成了 3），这条 SQL 会更新 0 行
-- Controller 里的 if (... == 0) return updatedDataFailed() 就会触发
```

#### `@TableField(fill = FieldFill.INSERT)` — 自动填充时间

```java
@TableField(value = "`add_time`", fill = FieldFill.INSERT)
private LocalDateTime addTime;

@TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updateTime;
```

| fill 值 | 行为 |
|---------|------|
| `FieldFill.INSERT` | 新增时自动填充当前时间（后续更新不动） |
| `FieldFill.INSERT_UPDATE` | 新增和更新时都自动填充当前时间 |

你不需要在代码里手动 `issue.setAddTime(LocalDateTime.now())`，框架自动处理。

### 字段名常量

```java
public static final String QUESTION = "`question`";
public static final String ANSWER = "`answer`";
// ...
```

这些常量在 Service 里构建查询条件时使用：

```java
wrapper.like(ShopflowIssue.QUESTION, body.getQuestion());
// 等价于
wrapper.like("`question`", body.getQuestion());
```

用常量的好处：避免写死字符串，IDE 可以自动补全和重命名。

---

## 7. 数据库服务层：IssueServiceImpl.java

> 文件路径：`shopflow-db/src/main/java/org/ysling/shopflow/db/service/impl/IssueServiceImpl.java`（240 行）

### 它的角色

这是 **db 模块**（数据层）的一部分，专门负责和 `shopflow_issue` 数据库表打交道。它是 `AdminDemoCrudService` 的**父类**。

### 类定义

```java
@Service                                        // 注册为 Spring 服务
@Primary                                        // 优先注入（多个同类型 Bean 时）
@CacheConfig(cacheNames = "shopflow_issue")     // 缓存命名空间
public class IssueServiceImpl
    extends IBaseServiceImpl<IssueMapper, ShopflowIssue>   // 泛型：Mapper 和 实体类
    implements IIssueService {                              // 实现接口
```

| 注解/关键字 | 作用 |
|------------|------|
| `@Service` | 注册为 Spring 服务组件 |
| `@Primary` | 当有多个同类型的 Bean 时，优先使用这个。因为 `AdminDemoCrudService` 也继承了它，加 `@Primary` 避免注入歧义 |
| `@CacheConfig(cacheNames = "shopflow_issue")` | 这个类的缓存命名空间是 `shopflow_issue` |
| `IBaseServiceImpl<IssueMapper, ShopflowIssue>` | 泛型参数：`IssueMapper` 是 Mapper 接口，`ShopflowIssue` 是实体类 |
| `implements IIssueService` | 实现 `IIssueService` 接口（方法签名的契约） |

### 方法分类

240 行看起来多，但**逻辑只有两类**：

#### 第一类：读操作 — `@Cacheable`（查之前先看缓存）

```java
@Cacheable(sync = true)
public ShopflowIssue findById(Serializable id) {       // 按 ID 查一条
    return getBaseMapper().selectById(id);
}

@Cacheable(sync = true)
public List<ShopflowIssue> queryAll(Wrapper wrapper) {  // 按条件查列表
    return getBaseMapper().selectList(wrapper);
}

@Cacheable(sync = true)
public ShopflowIssue getById(Serializable id) { ... }   // 按 ID 查

@Cacheable(sync = true)
public long count() { ... }                              // 总记录数

@Cacheable(sync = true)
public List<ShopflowIssue> list() { ... }                // 查全部

@Cacheable(sync = true)
public List<ShopflowIssue> listByIds(...) { ... }        // 按 ID 批量查
```

`@Cacheable(sync = true)` 的执行流程：

```
请求来了
  │
  ├── Redis 中有缓存？
  │     ├── YES → 直接返回缓存（不查数据库）
  │     └── NO  → 查数据库 → 把结果存到 Redis → 返回
  │
  └── sync = true 的意思：
        多个请求同时来查同一个数据时，
        只让一个线程去查数据库，其他线程等待。
        防止缓存失效瞬间大量请求同时打到数据库（缓存击穿）
```

#### 第二类：写操作 — `@CacheEvict`（写完之后清缓存）

```java
@CacheEvict(allEntries = true)
public int add(ShopflowIssue record) {                  // 新增
    return getBaseMapper().insert(record);
}

@CacheEvict(allEntries = true)
public int updateVersionSelective(ShopflowIssue record) { // 更新
    return getBaseMapper().updateById(record);
}

@CacheEvict(allEntries = true)
public int deleteById(Serializable id) {                // 删除（逻辑删除）
    return getBaseMapper().deleteById(id);
}

@CacheEvict(allEntries = true)
public int actualDeleteById(Serializable id) {          // 物理删除（真删）
    return getBaseMapper().actualDeleteById(id);
}

@CacheEvict(allEntries = true)
public boolean batchAdd(List<ShopflowIssue> list) { ... }  // 批量新增

// ... 更多写操作（save、saveOrUpdate、remove、update 等）
```

`@CacheEvict(allEntries = true)` 的执行流程：

```
写操作执行
  │
  ├── 执行数据库操作（INSERT / UPDATE / DELETE）
  │
  └── 执行完毕后 → 清空 shopflow_issue 命名空间下的所有缓存
        │
        └── 下次读操作发现缓存为空 → 重新查数据库 → 存入缓存
```

### `getBaseMapper()` 是什么？

你会发现所有方法都在调 `getBaseMapper().xxx()`：

```java
getBaseMapper().selectById(id);     // 查询
getBaseMapper().insert(record);     // 新增
getBaseMapper().updateById(record); // 更新
getBaseMapper().deleteById(id);     // 删除
getBaseMapper().selectList(wrapper); // 条件查询
```

`getBaseMapper()` 返回 `IssueMapper`（Mapper 接口），它由 MyBatis Plus 框架自动实现，能把方法调用翻译成 SQL：

```
getBaseMapper().selectById("123")
  → SELECT * FROM shopflow_issue WHERE id = '123' AND deleted = 0

getBaseMapper().insert(record)
  → INSERT INTO shopflow_issue (id, question, answer, ...) VALUES (...)

getBaseMapper().deleteById("123")
  → UPDATE shopflow_issue SET deleted = 1 WHERE id = '123'  (逻辑删除)
```

**前端类比**：`getBaseMapper()` 就像拿到了一个预配置好的 `axios` 实例，它知道要操作哪张表、表有哪些字段，你只需要调 `selectById` / `insert` 这些方法就行。

---

## 8. 继承链全景：为什么你只写 30 行就拥有了完整 CRUD

### 后端继承链

```
MyBatis Plus 框架层
  └── ServiceImpl<M, T>              ← 框架提供 saveBatch、updateBatchById、removeById 等方法
        │
        └── IBaseServiceImpl<M, T>   ← 项目自定义基类（目前为空壳，预留项目级公共逻辑）
              │
              └── IssueServiceImpl   ← shopflow_issue 表专用，30+ 个方法 + 缓存策略
                    │
                    └── AdminDemoCrudService  ← 只写了 validate + querySelective（30 行）
```

### 前端类比

```
axios（底层 HTTP 库）
  └── request.js（封装了 baseURL、拦截器、token）
        └── demoCrud.js（封装了 /demo-crud 的 5 个具体请求）
              └── demoCrud.vue（调 API + 处理页面逻辑）
```

### 每一层负责什么

| 层 | 文件 | 职责 | 你需要写代码吗 |
|-----|------|------|-------------|
| 框架层 | ServiceImpl（MyBatis Plus 提供） | 通用增删改查能力 | 不用，框架自带 |
| 项目基类 | IBaseServiceImpl | 项目级公共逻辑扩展点 | 暂时不用 |
| 表服务层 | IssueServiceImpl | 特定表的 CRUD + 缓存注解 | 代码生成器生成 |
| 业务层 | AdminDemoCrudService | 校验规则 + 业务查询 | **你要写的** |

**结论**：你实际需要手写的只有 `validate()` 和 `querySelective()` 这两个方法，其他全靠继承。

---

## 9. 五大操作完整链路追踪

### 9.1 查列表（List）

```
前端                                     后端
──────                                  ──────
demoCrud.vue                            AdminDemoCrudController
  │ handleFilter() 或 created()             │
  │ → getList()                             │
  │ → listDemoCrud(this.listQuery)          │
  │                                         │
  │ GET /demo-crud/list                     │
  │ ?page=1&limit=10&question=测试    ─────→ list(DemoCrudListBody body)
  │                                         │
  │                                     AdminDemoCrudService
  │                                         │ querySelective(body)
  │                                         │   1. startPage(body) → 分页
  │                                         │   2. wrapper.like() → 搜索条件
  │                                         │   3. queryAll(wrapper)
  │                                         │
  │                                     IssueServiceImpl
  │                                         │ @Cacheable → 查缓存
  │                                         │ → getBaseMapper().selectList()
  │                                         │
  │                                     MySQL
  │                                         │ SELECT * FROM shopflow_issue
  │                                         │ WHERE deleted=0 AND question LIKE '%测试%'
  │                                         │ ORDER BY add_time DESC LIMIT 0,10
  │                                         │
  │  ←─────────────────────────────────────  │
  │ { errno:"success", data:{ list, total }} │
  │                                         │
  │ this.list = res.data.list               │
  │ this.total = res.data.total             │
```

### 9.2 查详情（Read）

```
readDemoCrud(id)
  GET /demo-crud/read?id=xxx  ───→  read(@JsonBody String id)
                                      → findById(id)
                                      → @Cacheable → selectById(id)
                                      → SELECT * FROM shopflow_issue WHERE id = ?
  ←──────────────────────────────  { errno:"success", data: { 单条记录 } }
```

### 9.3 新增（Create）

```
createDemoCrud({ question, answer })
  POST /demo-crud/create      ───→  create(@RequestBody ShopflowIssue issue)
  body: { question, answer }          │
                                      ├── validate(issue) → question 和 answer 非空检查
                                      ├── add(issue)
                                      │     → @CacheEvict → 清空缓存
                                      │     → getBaseMapper().insert(issue)
                                      │     → INSERT INTO shopflow_issue (...) VALUES (...)
                                      │     → 自动填充 add_time、update_time
                                      │
  ←──────────────────────────────  { errno:"success" }

  submitForm() → this.getList()  // 新增后刷新列表
```

### 9.4 编辑（Update）

```
updateDemoCrud({ id, question, answer })
  POST /demo-crud/update      ───→  update(@RequestBody ShopflowIssue issue)
  body: { id, question, answer }      │
                                      ├── validate(issue) → 同上
                                      ├── updateVersionSelective(issue)
                                      │     → @CacheEvict → 清空缓存
                                      │     → getBaseMapper().updateById(issue)
                                      │     → UPDATE shopflow_issue
                                      │       SET question=?, answer=?, version=version+1
                                      │       WHERE id=? AND version=?  (乐观锁)
                                      │     → 自动更新 update_time
                                      │
  ←──────────────────────────────  { errno:"success" }
                                  或  { errno:"xxx" }  (版本号不匹配则更新失败)
```

### 9.5 删除（Delete）

```
deleteDemoCrud(id)
  POST /demo-crud/delete?id=xxx ──→  delete(@JsonBody String id)
                                       │
                                       ├── deleteById(id)
                                       │     → @CacheEvict → 清空缓存
                                       │     → getBaseMapper().deleteById(id)
                                       │     → UPDATE shopflow_issue SET deleted=1
                                       │       WHERE id=?  (逻辑删除，不是真删)
                                       │
  ←──────────────────────────────  { errno:"success" }

  handleDelete() → this.getList()  // 删除后刷新列表
```

---

## 10. 缓存策略详解

### 为什么要缓存？

```
没有缓存：每次查列表 → 都查数据库 → 数据库压力大
有了缓存：第一次查 → 查数据库 + 存 Redis → 后续相同查询直接从 Redis 拿（快 10-100 倍）
```

### 两个注解的分工

```
┌─────────────────────────────────────────────────────────────┐
│                  @Cacheable — 读操作                          │
│                                                             │
│  请求来了 → 生成缓存 key（方法名 + 参数哈希）                    │
│     ├── Redis 中有这个 key？                                  │
│     │     ├── YES → 直接返回缓存值（不查数据库，快！）            │
│     │     └── NO  → 查数据库 → 结果存入 Redis → 返回            │
│     │                                                        │
│     └── sync = true                                          │
│           → 并发时只让 1 个线程查数据库，其他等待                  │
│           → 防止缓存击穿（100 个请求同时查同一个数据）             │
│                                                              │
├──────────────────────────────────────────────────────────────┤
│                  @CacheEvict — 写操作                         │
│                                                              │
│  新增/修改/删除 → 执行数据库操作                                │
│     └── 执行完毕 → allEntries = true                          │
│           → 清空整个命名空间的缓存                              │
│           → 下次读操作会重新查数据库（保证数据新鲜）              │
└──────────────────────────────────────────────────────────────┘
```

### 缓存命名空间

```java
// IssueServiceImpl
@CacheConfig(cacheNames = "shopflow_issue")
// 缓存 key 格式：shopflow_issue::方法名::参数哈希

// AdminDemoCrudService
@CacheConfig(cacheNames = "shopflow_demo_crud")
// 缓存 key 格式：shopflow_demo_crud::方法名::参数哈希
```

子类覆盖了父类的缓存命名空间，所以 `AdminDemoCrudService.querySelective()` 的缓存和 `IssueServiceImpl.findById()` 的缓存是隔离的。

### 完整的缓存生命周期

```
时刻 1：用户第一次查列表
  → querySelective() → @Cacheable → 缓存未命中
  → 查数据库 → 结果存入 Redis（key = shopflow_demo_crud::querySelective::xxhash）
  → 返回结果

时刻 2：用户第二次查列表（相同参数）
  → querySelective() → @Cacheable → 缓存命中！
  → 直接返回 Redis 中的数据（没查数据库，快）

时刻 3：用户新增了一条数据
  → add() → @CacheEvict(allEntries = true)
  → 执行 INSERT → 清空 shopflow_issue 下所有缓存

时刻 4：用户再次查列表
  → querySelective() → @Cacheable → 缓存未命中（刚被清空）
  → 重新查数据库 → 能查到新增的数据 → 存入缓存 → 返回
```

---

## 11. 自测清单

学完这份文档，你应该能回答以下所有问题：

### 前端层

- [ ] `demoCrud.js` 里 5 个函数分别对应哪个 HTTP 方法（GET/POST）？
- [ ] `params` 和 `data` 传参有什么区别？分别用在什么场景？
- [ ] `demoCrud.vue` 的 `listQuery` 包含哪些字段？和后端哪个类对应？
- [ ] 为什么每次新增/编辑/删除成功后都要调 `this.getList()`？

### Controller 层

- [ ] `@GetMapping("/list")` 的完整请求路径是什么？
- [ ] `list()` 方法的参数 `DemoCrudListBody body` 为什么没有 `@RequestBody`？
- [ ] `create()` 方法的参数为什么要加 `@Valid @RequestBody`？
- [ ] `@SaCheckPermission("admin:issue:list")` 做了什么？

### Service 层

- [ ] `AdminDemoCrudService` 为什么只有 2 个方法，但 Controller 调了 5 个不同的方法？
- [ ] `validate()` 返回 `null` 代表什么？返回非 `null` 代表什么？
- [ ] `querySelective()` 里的 `startPage(body)` 做了什么？
- [ ] `wrapper.like(ShopflowIssue.QUESTION, body.getQuestion())` 对应什么 SQL？

### 数据库层

- [ ] `@TableLogic` 注解的效果是什么？`deleteById` 实际执行的是 DELETE 还是 UPDATE？
- [ ] `@Version` 注解的作用是什么？什么场景下会更新失败？
- [ ] `@TableField(fill = FieldFill.INSERT)` 是什么意思？
- [ ] `@Cacheable(sync = true)` 和 `@CacheEvict(allEntries = true)` 分别用在什么操作上？
- [ ] `getBaseMapper()` 返回的是什么？

### 综合理解

- [ ] 从前端点击"查询"到页面显示数据，经过了哪些层、哪些文件、哪些方法？
- [ ] 如果要给列表查询加一个"按答案搜索"的功能，需要改哪些文件？
  - （答案：改 3 个文件 — `DemoCrudListBody` 加字段、`AdminDemoCrudService.querySelective()` 加条件、`demoCrud.vue` 加输入框和 listQuery 字段）

---

## 相关文档索引

| 文档 | 你应该什么时候看 |
|------|--------------|
| [前端学后端完整指南](frontend-learn-backend-guide.md) | 你现在正在的位置（阶段 2） |
| [Demo CRUD 后端详解](demo-crud-backend-full-guide.md) | 这份文档的另一个视角版本 |
| [从 0 到 1 做 CRUD](crud-full-walkthrough-for-frontend.md) | 看完这份文档后，准备自己动手写的时候看 |
| [后端架构说明（前端视角）](backend-architecture-for-frontend.md) | 想了解更全面的后端架构时看 |
| [项目结构分析](project-structure-analysis.md) | 想了解其他模块的结构时看 |

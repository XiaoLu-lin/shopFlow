# Demo CRUD 后端实现详解（基于当前仓库实战版）

## 1. 目标说明

这份文档专门讲清楚一件事：

1. 你要的 CRUD 页面，不只是前端。
2. 我们已经在项目里补齐了独立后端接口 `/admin/demo-crud/*`。
3. 你可以按本文一步步理解并复刻同类后端模块。

本次实现为了教学效率，复用了现有数据表 `litemall_issue`（字段简单、链路稳定），但接口路径是独立的 `demo-crud`，所以从前端视角看，它已经是一套完整的“你自己写的后端 CRUD 模块”。

---

## 2. 本次新增/修改文件清单

### 2.1 新增后端文件

1. `litemall-admin-api/src/main/java/org/ysling/litemall/admin/model/democrud/body/DemoCrudListBody.java`
2. `litemall-admin-api/src/main/java/org/ysling/litemall/admin/service/AdminDemoCrudService.java`
3. `litemall-admin-api/src/main/java/org/ysling/litemall/admin/web/AdminDemoCrudController.java`

### 2.2 修改前端对接文件

1. `litemall-admin/src/api/demoCrud.js`
2. `litemall-admin/src/router/index.js`

---

## 3. 最关键的设计思路

### 3.1 为什么要独立 Controller/Service

如果你一直复用 `AdminIssueController`，你只是“调旧接口”；
如果你新建 `AdminDemoCrudController + AdminDemoCrudService`，你才真正完成了后端模块搭建。

所以本次的核心是：

1. 路由前缀独立：`/admin/demo-crud`
2. 业务类独立：`AdminDemoCrudService`
3. 列表查询参数独立：`DemoCrudListBody`

### 3.2 为什么暂时复用 `litemall_issue` 表

你当前目标是“先学后端 CRUD 方法论”，不是先做复杂建模。

复用 `IssueServiceImpl + LitemallIssue` 的好处：

1. 少踩数据库/Mapper 层坑。
2. 快速聚焦 Controller 与 Service 的写法。
3. 能把完整联调链路先跑通。

后续你熟练后，再升级到“新建 `demo_crud` 表 + 新 domain/mapper/service`”会更稳。

---

## 4. 后端分层是怎么串起来的

请求从浏览器到数据库，实际链路如下：

1. 前端调用 `/demo-crud/list`
2. devServer 代理到后端 `/admin/demo-crud/list`
3. `AdminDemoCrudController.list()` 接收参数
4. 调用 `AdminDemoCrudService.querySelective()`
5. Service 用 MyBatis-Plus 构造 `QueryWrapper`
6. `IssueServiceImpl` 执行查询
7. `ResponseUtil.okList(...)` 返回统一分页结构

返回结构仍是项目统一协议：

1. `errno`
2. `errmsg`
3. `data`（里面含 `list`、`total`、`pages`、`limit` 等）

---

## 5. 三个后端文件逐个拆解

## 5.1 DemoCrudListBody（列表查询入参）

文件：`DemoCrudListBody.java`

作用：定义“列表查询”允许前端传的字段。

本次保留最小字段：

1. `question`

它继承 `PageBody`，自动具备：

1. `page`
2. `limit`
3. `sort`
4. `order`

也就是说，前端分页参数不需要你再手写解析。

---

## 5.2 AdminDemoCrudService（业务逻辑层）

文件：`AdminDemoCrudService.java`

类定义：

1. `@Service`
2. `extends IssueServiceImpl`

这表示你直接复用了 db 模块里已实现的基础 CRUD 能力（例如 `findById`、`add`、`deleteById`、`updateVersionSelective`、`queryAll`）。

### 5.2.1 validate 方法

```java
public Object validate(LitemallIssue issue) {
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
```

为什么要写：

1. 防止空参数直接落库。
2. 统一返回项目错误码格式。
3. Controller 更薄，只负责路由编排。

### 5.2.2 querySelective 方法

```java
@Cacheable(sync = true)
public List<LitemallIssue> querySelective(DemoCrudListBody body) {
    QueryWrapper<LitemallIssue> wrapper = startPage(body);
    if (StringUtils.hasText(body.getQuestion())) {
        wrapper.like(LitemallIssue.QUESTION, body.getQuestion());
    }
    return queryAll(wrapper);
}
```

关键点：

1. `startPage(body)` 负责把分页和排序参数装进查询上下文。
2. `question` 有值才加 `like` 条件，避免无效过滤。
3. `queryAll(wrapper)` 执行实际查询。
4. `@Cacheable(sync = true)` 跟随项目现有风格（教学环境可保留，真实高写入场景可评估是否关闭）。

---

## 5.3 AdminDemoCrudController（接口层）

文件：`AdminDemoCrudController.java`

类级别定义：

1. `@RestController`
2. `@RequestMapping("/admin/demo-crud")`
3. `@Validated`

对应的 5 个接口：

1. `GET /admin/demo-crud/list`
2. `GET /admin/demo-crud/read`
3. `POST /admin/demo-crud/create`
4. `POST /admin/demo-crud/update`
5. `POST /admin/demo-crud/delete`

### 5.3.1 list

```java
@GetMapping("/list")
public Object list(DemoCrudListBody body) {
    return ResponseUtil.okList(demoCrudService.querySelective(body));
}
```

说明：

1. 入参是查询对象，不需要 `@RequestBody`。
2. 最终使用 `okList` 返回分页标准结构。

### 5.3.2 read

```java
@GetMapping("/read")
public Object read(@JsonBody String id) {
    return ResponseUtil.ok(demoCrudService.findById(id));
}
```

说明：

1. 按 id 读详情。
2. 保持和项目其他模块一致的写法。

### 5.3.3 create

```java
@PostMapping("/create")
public Object create(@Valid @RequestBody LitemallIssue issue) {
    Object error = demoCrudService.validate(issue);
    if (error != null) {
        return error;
    }
    if (demoCrudService.add(issue) == 0) {
        return ResponseUtil.addDataFailed();
    }
    return ResponseUtil.ok();
}
```

说明：

1. 先校验，再落库。
2. 影响行数为 0 走失败分支。
3. 成功统一 `ok()`。

### 5.3.4 update

```java
@PostMapping("/update")
public Object update(@Valid @RequestBody LitemallIssue issue) {
    Object error = demoCrudService.validate(issue);
    if (error != null) {
        return error;
    }
    if (demoCrudService.updateVersionSelective(issue) == 0) {
        return ResponseUtil.updatedDataFailed();
    }
    return ResponseUtil.ok();
}
```

说明：

1. 和 create 一样先校验。
2. 使用带版本控制的方法更新，符合项目并发控制习惯。

### 5.3.5 delete

```java
@PostMapping("/delete")
public Object delete(@JsonBody String id) {
    if (demoCrudService.deleteById(id) == 0) {
        return ResponseUtil.deletedDataFailed();
    }
    return ResponseUtil.ok();
}
```

说明：

1. 按 id 删除。
2. 表实体启用了逻辑删除字段，行为符合项目主流设计。

---

## 6. 权限注解怎么理解

每个接口都写了 `@SaCheckPermission(...)`，本次复用的是 `issue` 权限点，例如：

1. `admin:issue:list`
2. `admin:issue:create`
3. `admin:issue:update`
4. `admin:issue:delete`

这么做的目的：

1. 你现有管理员账号无需额外配新权限即可访问。
2. 快速避免“接口写好了但 403”的学习阻塞。

后续你要正式上线时，建议拆成独立权限点：

1. `admin:demo-crud:list`
2. `admin:demo-crud:create`
3. `admin:demo-crud:update`
4. `admin:demo-crud:delete`

并同步菜单权限配置。

---

## 7. 前端如何对接新后端

文件：`litemall-admin/src/api/demoCrud.js`

你现在调用的已经是独立后端：

1. `url: '/demo-crud/list'`
2. `url: '/demo-crud/read'`
3. `url: '/demo-crud/create'`
4. `url: '/demo-crud/update'`
5. `url: '/demo-crud/delete'`

配合前端代理后，真实到达后端是 `/admin/demo-crud/*`。

文件：`litemall-admin/src/router/index.js`

`demo-crud` 路由权限元数据已改为：

1. `GET /admin/demo-crud/list`

---

## 8. 我实际做过的验证

### 8.1 代码层检查

IDE 问题检查：新增和修改文件无报错。

### 8.2 后端编译验证

执行命令（JDK8 环境）：

```bash
cd /Users/user/Desktop/demo/test/react/litemall-plus-master
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH="$JAVA_HOME/bin:$PATH"
mvn -pl litemall-admin-api -am -DskipTests compile
```

结果：

1. `litemall-admin-api` 编译成功。
2. 整体 `BUILD SUCCESS`。

---

## 9. 接口自测清单（你可直接照做）

先确保后端已启动，并已登录拿到管理端 token。

### 9.1 列表

```bash
curl -G 'http://127.0.0.1:6914/admin/demo-crud/list' \
  --data-urlencode 'page=1' \
  --data-urlencode 'limit=10' \
  --data-urlencode 'question=测试' \
  -H 'X-Litemall-Admin-Token: 你的token'
```

### 9.2 新增

```bash
curl -X POST 'http://127.0.0.1:6914/admin/demo-crud/create' \
  -H 'Content-Type: application/json' \
  -H 'X-Litemall-Admin-Token: 你的token' \
  -d '{
    "question": "演示问题",
    "answer": "演示答案"
  }'
```

### 9.3 详情

```bash
curl -G 'http://127.0.0.1:6914/admin/demo-crud/read' \
  --data-urlencode 'id=刚创建返回的id' \
  -H 'X-Litemall-Admin-Token: 你的token'
```

### 9.4 更新

```bash
curl -X POST 'http://127.0.0.1:6914/admin/demo-crud/update' \
  -H 'Content-Type: application/json' \
  -H 'X-Litemall-Admin-Token: 你的token' \
  -d '{
    "id": "要更新的id",
    "question": "演示问题-已更新",
    "answer": "演示答案-已更新"
  }'
```

### 9.5 删除

```bash
curl -X POST 'http://127.0.0.1:6914/admin/demo-crud/delete' \
  -H 'Content-Type: application/json' \
  -H 'X-Litemall-Admin-Token: 你的token' \
  -d '{"id":"要删除的id"}'
```

---

## 10. 你以后自己写后端 CRUD 的固定模板

你可以把这套模板背下来：

1. 建 `xxxListBody`（继承 `PageBody`）
2. 建 `AdminXxxService`
3. 在 Service 写 `validate`
4. 在 Service 写 `querySelective`
5. 建 `AdminXxxController`（list/read/create/update/delete）
6. 统一 `ResponseUtil`
7. 给每个接口加权限注解
8. 前端 `src/api/xxx.js` 对齐路径
9. 路由权限 `perms` 对齐
10. 先后端自测，再前端联调

---

## 11. 常见错误与排查

### 11.1 新增/更新返回参数错误

通常是：

1. `question` 或 `answer` 传空。
2. `validate` 返回了 `badArgument`。

### 11.2 页面能打开但请求 403

通常是：

1. token 失效。
2. 权限点不匹配。
3. 路由 `meta.perms` 与后端接口不一致。

### 11.3 列表没有分页

通常是：

1. 没继承 `PageBody`。
2. Service 没调用 `startPage(body)`。

### 11.4 修改无效

通常是：

1. update 请求没带 `id`。
2. 版本号并发控制导致更新行数为 0。

---

## 12. 下一步升级建议（从教学版到生产版）

当前版本是教学友好的“独立接口 + 复用现有表”。

如果你准备进入真实业务开发，建议按这个顺序升级：

1. 新建业务表 `demo_crud`。
2. 在 `litemall-db` 新建 `LitemallDemoCrud`、`DemoCrudMapper`、`DemoCrudServiceImpl`。
3. 把 `AdminDemoCrudService` 从 `IssueServiceImpl` 切到 `DemoCrudServiceImpl`。
4. 新建独立权限点 `admin:demo-crud:*`。
5. 补充单元测试和接口测试。

---

## 13. 一句话总结

你现在这套代码已经满足“我写了前端，也把后端独立 CRUD 写出来了”的要求；
重点不是记住每行代码，而是记住这条固定链路：

`ListBody -> Service(validate/query) -> Controller(CRUD) -> ResponseUtil -> 前端 API 对齐`。

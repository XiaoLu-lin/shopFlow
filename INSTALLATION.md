# litemall-plus 本地环境安装指南

## 📋 环境检查

### 当前本地环境状态
| 组件 | 已安装 | 需求版本 | 状态 |
|------|--------|--------|------|
| Node.js | v18.20.3 | 12+ | ✅ |
| NPM | 10.7.0 | 6+ | ✅ |
| Git | 2.39.2 | 2.0+ | ✅ |
| Java JDK | ❌ | 1.8 LTS | ⚠️ 缺失 |
| Maven | ❌ | 3.6+ | ⚠️ 缺失 |
| MySQL | ❌ | 5.7 / 8.0 | ⚠️ 缺失 |
| Redis | ❌ | 6.0+ | ⭕ 可选 |

---

## 🚀 必需组件安装步骤
---

## 📚 各组件功能说明（为前端开发者）

作为前端开发者，了解这些工具的作用很重要，下面用简单的语言解释：

### 🔵 **Java JDK 1.8**
**是什么？** Java 开发工具包，用于运行 Java 程序

**为什么需要？** 这个项目的后端是用 Java 的 Spring Boot 框架开发的，需要 Java 环境才能运行

**类比** 就像前端需要 Node.js 一样，后端也需要 Java 运行环境

**你需要了解吗？** 不需要写代码，只需要安装，确保后端能运行

---

### 🟢 **Maven**
**是什么？** Java 项目的包管理和构建工具

**为什么需要？** 用来下载后端项目所需的依赖库，类似 npm/yarn

**作用对比：**
```
前端开发：npm install → 下载 node_modules
后端开发：mvn clean install → 下载 Maven 依赖库
```

**你需要了解吗？** 不需要深入了解，知道这是"Java 的 npm"就行

---

### 🟡 **MySQL 数据库**
**是什么？** 关系型数据库，存储商城的所有数据

**为什么需要？** 存储用户、商品、订单、支付等所有业务数据

**存储的数据包括：**
- 👤 用户账号密码、个人信息
- 🛍️ 商品信息、价格、库存
- 📦 订单、支付记录、发货信息
- 💬 评论、用户动态、聊天记录
- 💰 优惠券、积分、分销提成
- 👥 角色权限、店铺信息

**简单理解：** 你的前端通过 API 从 MySQL 读写数据，类似这样：
```
前端请求 → API(后端) → MySQL数据库 → API返回数据 → 前端展示
```

**你需要了解吗？** 不需要写 SQL，但要知道没有数据库，API 无法工作

---

### 🔴 **Node.js + NPM**
**是什么？** 前端开发运行环境和包管理工具

**现在你已经安装了吗？** ✅ 已安装（v18.20.3）

**用途：**
- ✅ 运行前端开发服务器（`npm run dev`）
- ✅ 管理前端依赖（`npm install`）
- ✅ 打包前端代码（`npm run build`）

**你需要了解吗？** ✅ 必然！这是你主要的开发工具

---

### ⚫ **Git**
**是什么？** 版本控制工具

**现在你已经安装了吗？** ✅ 已安装（2.39.2）

**用途：**
- 克隆项目代码到本地
- 管理代码版本历史
- 多人协作开发

**你需要了解吗？** ✅ 必然！用来下载项目和管理代码

---

### 💜 **Redis**（可选，但推荐）
**是什么？** 内存型缓存数据库

**为什么需要？** 用于缓存、会话管理、消息队列等

**简单理解：**
- MySQL 是磁盘数据库，读写速度慢
- Redis 在内存中，读写速度快，用来缓存热数据

**常见场景：**
- 🔐 缓存用户登录信息（token）
- 🏷️ 缓存商品热词、分类数据
- ⏱️ 临时存储订单支付超时数据
- 📨 消息队列处理异步任务

**你需要了解吗？** ❌ 不必须，但有的话项目性能更好

---

### 🔧 **微信开发者工具**（可选）
**是什么？** 微信小程序的官方开发工具

**为什么需要？** 开发和测试微信小程序 (`litemall-wx` 项目)

**用途：**
- 📱 在电脑上预览小程序效果
- 🐛 调试小程序代码
- 📤 上传代码到微信审核

**你需要了解吗？** ❌ 只有开发小程序前端时才需要

---

## 🎯 前端开发者必需清单

| 工具 | 现状 | 必须? | 用途 |
|-----|------|------|------|
| Node.js + NPM | ✅ 已装 | ✅ 必须 | 运行和开发前端 |
| Git | ✅ 已装 | ✅ 必须 | 下载项目、版本管理 |
| Java JDK 1.8 | ❌ 缺失 | ✅ 必须 | 让后端服务运行 |
| Maven | ❌ 缺失 | ✅ 必须 | 下载后端依赖 |
| MySQL | ❌ 缺失 | ✅ 必须 | 存储业务数据 |
| Redis | ❌ 缺失 | ⭕ 推荐 | 提升性能（可选） |
| 微信开发者工具 | ❌ 缺失 | ⭕ 可选 | 开发小程序（可选） |

**🔥 必须先安装：Java JDK + Maven + MySQL**

---

## 🚀 必需组件安装步骤

### 1️⃣ 安装 Java JDK 1.8

#### 方式一：使用 Homebrew（推荐）
```bash
brew install java@8
```

#### 方式二：手动安装
1. 访问 Oracle Java 官网：https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
2. 下载 JDK 1.8 macOS 版本
3. 按照安装向导完成安装

#### 设置环境变量
编辑 `~/.zshrc` 或 `~/.bash_profile`：
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH=$JAVA_HOME/bin:$PATH
```

然后执行：
```bash
source ~/.zshrc
```

#### 验证安装
```bash
java -version
# 输出应为: java version "1.8.0_xxx"
```

---

### 2️⃣ 安装 Maven 3.6+

#### 方式一：使用 Homebrew（推荐）
```bash
brew install maven
```

#### 方式二：手动安装
1. 访问 Maven 官网：https://maven.apache.org/download.cgi
2. 下载 Binary zip archive
3. 解压到任意目录，例如 `/opt/maven`
4. 设置环境变量

#### 设置环境变量（如果手动安装）
编辑 `~/.zshrc`：
```bash
export M2_HOME=/opt/maven
export PATH=$M2_HOME/bin:$PATH
```

执行：
```bash
source ~/.zshrc
```

#### 验证安装
```bash
mvn -version
# 输出应为: Apache Maven 3.6.x
```

---

### 3️⃣ 安装 MySQL 5.7 或 8.0

#### 方式一：使用 Homebrew（推荐）
```bash
# 安装最新版本 MySQL 8.0
brew install mysql

# 或安装 MySQL 5.7
brew install mysql@5.7
```

#### 方式二：DMG 安装包
1. 访问 MySQL 官网：https://dev.mysql.com/downloads/mysql/
2. 选择 macOS 版本下载
3. 按照安装向导完成

#### 启动 MySQL 服务
```bash
# 使用 Homebrew
brew services start mysql

# 或手动启动
mysql.server start
```

#### 初始化数据库和用户
首次启动后，运行以下命令设置密码：
```bash
mysql -u root

# 在 MySQL 命令行中执行
ALTER USER 'root'@'localhost' IDENTIFIED BY 'litemall123';
```

#### 验证安装
```bash
mysql --version
mysql -u root -p
# 输入密码: litemall123
```

---

## 📦 可选组件安装

### Redis（用于缓存和消息队列）
```bash
# 安装 Redis
brew install redis

# 启动 Redis 服务
brew services start redis

# 验证
redis-cli --version
redis-cli ping
# 输出应为: PONG
```

### 微信开发者工具（开发小程序时需要）
1. 访问微信开发者官网：https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html
2. 下载 macOS 版本
3. 按照安装向导完成

---

## 🗄️ 数据库初始化

### 1. 创建数据库和用户

使用 MySQL 命令行创建数据库：
```bash
mysql -u root -p

# 输入密码后，在 MySQL 命令行执行以下 SQL：
CREATE DATABASE litemall_plus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'litemall'@'localhost' IDENTIFIED BY 'litemall123';
GRANT ALL PRIVILEGES ON litemall_plus.* TO 'litemall'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 2. 导入数据库脚本

项目中的 SQL 脚本位置：
- [litemall-db/sql/litemall_plus.sql](litemall-db/sql/litemall_plus.sql)

导入数据库：
```bash
cd litemall-db/sql

# 导入完整数据库（包括表结构和测试数据）
mysql -u litemall -p litemall_plus < litemall_plus.sql

# 输入密码: litemall123
```

验证导入成功：
```bash
mysql -u litemall -p litemall_plus
SHOW TABLES;
EXIT;
```

---

## 🔧 配置文件修改

### 后端服务配置

修改后端配置文件，根据本地数据库设置调整连接字符串。

主要配置文件位置：
- `litemall-admin-api/src/main/resources/application.yml`
- `litemall-wx-api/src/main/resources/application.yml`

检查以下配置项是否匹配本地环境：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/litemall_plus?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: litemall
    password: litemall123
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    host: 127.0.0.1
    port: 6379
    password: 
    database: 0
```

---

## 🚀 项目启动步骤

### 1. 启动后端服务

#### 方式一：使用 Maven 启动

```bash
# 进入项目根目录
cd /Users/user/Desktop/demo/test/react/litemall-plus-master

# 编译整个项目
mvn clean install -DskipTests

# 启动管理后台 API 服务
cd litemall-admin-api
mvn spring-boot:run

# 在另一个终端窗口启动小程序 API 服务
cd litemall-wx-api
mvn spring-boot:run
```

#### 方式二：使用 IDE 启动（推荐）
1. 在 IDE 中打开项目（IntelliJ IDEA 或 Eclipse）
2. 识别为 Maven 项目
3. 右键选择 `Run` 启动应用

#### 验证后端启动成功
- 管理后台 API：http://localhost:8080
- 小程序 API：http://localhost:8001

---

### 2. 启动管理后台前端

```bash
# 进入管理后台项目目录
cd litemall-admin

# 安装依赖
npm install
# 或使用 yarn
yarn install

# 启动开发服务器
npm run dev
# 或用 yarn
yarn dev
```

访问地址：http://localhost:8081

默认登录账号：
- 用户名：`admin123`
- 密码：`admin123`

**前端工程师通常只需要这一步！** 上面的后端启动步骤可以由后端团队完成。

---

### 理解项目启动流程（架构简图）

```
┌─────────────────────────────────────────────────────────────┐
│                    litemall-plus 项目                        │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  📱 前端部分（你的工作）                                      │
│  ───────────────────────────────────────────                 │
│  ├─ litemall-admin (管理后台 Vue 项目)                        │
│  │  └─ npm run dev → http://localhost:8081                   │
│  │     (用 Vue + Element UI 开发管理系统)                     │
│  │                                                            │
│  └─ litemall-wx (微信小程序项目)                              │
│     └─ 用微信开发者工具 → 预览小程序                           │
│        (用小程序开发，ColorUI 组件库)                         │
│                                                              │
│  🖥️ 后端部分（后端工程师的工作）                               │
│  ─────────────────────────────                               │
│  ├─ litemall-admin-api (Maven 项目)                           │
│  │  └─ mvn spring-boot:run → http://localhost:8080          │
│  │     (提供管理后台的 API 接口)                              │
│  │                                                            │
│  └─ litemall-wx-api (Maven 项目)                              │
│     └─ mvn spring-boot:run → http://localhost:8001          │
│        (提供小程序的 API 接口)                               │
│                                                              │
│  💾 数据存储                                                  │
│  ─────────────                                               │
│  ├─ MySQL (localhost:3306)                                   │
│  │  └─ 存储用户、商品、订单等业务数据                          │
│  │                                                            │
│  └─ Redis (localhost:6379) [可选]                             │
│     └─ 缓存热数据，提升性能                                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**流程说明：**
1. 你在浏览器打开 http://localhost:8081 （前端）
2. 你输入用户名密码登录
3. 前端发送 HTTP 请求到后端 API → http://localhost:8080
4. 后端从 MySQL 查询数据，返回给前端
5. 前端收到数据后展示在页面上


---

### 3. 启动微信小程序（开发环境）

1. 打开微信开发者工具
2. 选择 `litemall-wx` 目录
3. 设置 AppID（如果需要）
4. 在开发者工具中启动预览

---

## ✅ 验证各模块运行

| 模块 | 访问地址 | 验证方式 |
|------|--------|--------|
| 管理后台 API | http://localhost:8080 | swagger 文档或 API 请求 |
| 小程序 API | http://localhost:8001 | 小程序调试工具 |
| 管理后台前端 | http://localhost:8081 | 浏览器访问 |
| MySQL | localhost:3306 | 命令行连接 |
| Redis | localhost:6379 | `redis-cli ping` |

---

## 🐛 常见问题

### Q1: Java 版本不对
**问题**：显示 Java 11 或其他版本，需要 1.8
**解决**：
```bash
# 查看所有已安装的 Java 版本
/usr/libexec/java_home -V

# 重新设置 JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
```

### Q2: Maven 编译失败
**问题**：`mvn clean install` 失败
**解决**：
```bash
# 清除 Maven 缓存
rm -rf ~/.m2/repository

# 重新下载依赖
mvn clean install -DskipTests
```

### Q3: 数据库连接失败
**问题**：应用启动时数据库连接错误
**解决**：
1. 检查 MySQL 服务是否运行：`brew services list`
2. 检查数据库用户和密码
3. 检查 `application.yml` 中的连接字符串

### Q4: 前端 npm 安装失败
**问题**：`npm install` 超时或失败
**解决**：
```bash
# 清除 npm 缓存
npm cache clean --force

# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com

# 重新安装
npm install
```

### Q5: 端口被占用
**问题**：启动服务时提示端口已被占用
**解决**：
```bash
# 查看占用端口的进程
lsof -i :8080
lsof -i :8081

# 杀死进程
kill -9 <PID>

---

### Q6: 前端请求后端 API 时报错 (前端特定)
**问题**：浏览器控制台出现 `CORS` 错误或连接被拒绝

**错误示例：**
```
Access to XMLHttpRequest at 'http://localhost:8080/...' 
from origin 'http://localhost:8081' 
has been blocked by CORS policy
```

**原因**：前端 (8081) 尝试访问后端 (8080)，浏览器因为跨域而阻止了请求

**解决方案：**
1. 确保后端 API 服务正在运行（`http://localhost:8080`）
2. 检查项目中 CORS 跨域配置是否启用
3. 检查前端的 API 请求地址是否正确
4. 后端通常会在 `application.yml` 配置 CORS：
```yaml
# 允许所有来源的跨域请求
cors:
  allowed-origins: "*"
```

---

### Q7: 修改前端代码后热加载不生效 (前端特定)
**问题**：改了 Vue 代码，刷新浏览器还是看不到变化

**解决方案：**
```bash
# 1. 检查开发服务器是否还在运行
#    查看终端是否有输出

# 2. 强制刷新浏览器
#    Mac: Command + Shift + R
#    Windows: Ctrl + Shift + F5

# 3. 清除浏览器缓存
#    打开 DevTools (F12) → 右键刷新按钮 → 清空缓存并硬性加载

# 4. 重新启动开发服务器
npm run dev
```

---

### Q8: 我是前端工程师，如何快速跳过后端部分? (前端特定)
**场景**：你只想开发前端，不需要本地启动后端

**方案：**
1. 询问后端团队要测试环境 API 地址（通常是 `http://某个服务器:8080`）
2. 修改前端 API 配置文件指向测试环境
3. 这样你就可以只跑前端，连接到线上后端进行开发

**前端配置文件位置：**
```
litemall-admin/src/api/config.js  # API 基础地址配置
litemall-admin/src/utils/request.js   # HTTP 请求配置
```

修改这些文件中的 baseURL：
```javascript
// 本地开发
const BASE_URL = 'http://localhost:8080'

// 连接测试服务器
const BASE_URL = 'http://test-api.example.com:8080'
```

---

## ⚡ 前端工程师快速开始指南

如果你**只做前端开发**，这是最快的起步方式：

### 第 1 步：安装依赖（一次性）
```bash
# 1. 安装 Node.js（如果没装）- 参考上面的安装步骤

# 2. 进入项目目录
cd /Users/user/Desktop/demo/test/react/litemall-plus-master/litemall-admin

# 3. 安装 npm 依赖
npm install
```

### 第 2 步：启动开发服务器（每次开发）
```bash
# 在 litemall-admin 目录中
npm run dev

# 浏览器自动打开 http://localhost:8081
```

### 第 3 步：开始编码
```
src/
├─ views/          # 页面组件 (这里改 UI)
├─ components/     # 通用组件
├─ api/            # API 接口调用
├─ router/         # 路由配置
└─ store/          # Vuex 状态管理
```

修改代码 → 保存 → 浏览器自动热更新 → 看到效果 ✨

### 第 4 步：构建生产版本
```bash
npm run build

# 生成的文件在 dist/ 目录，部署到服务器
```

---

## 🔗 前端开发相关资源

### 使用的技术栈
- **Vue.js** - 前端框架（版本 2）
- **Element UI** - UI 组件库
- **Axios** - HTTP 请求库
- **Vuex** - 状态管理
- **Vue Router** - 页面路由
- **Webpack** - 打包工具（Vue CLI 内置）

### 项目文件结构
```
litemall-admin/                # 管理后台前端项目
├─ public/                     # 静态文件
├─ src/
│  ├─ App.vue                 # 根组件
│  ├─ main.js                 # 入口文件
│  ├─ permission.js           # 权限控制
│  ├─ api/                    # API 接口（在这里调用后端 API）
│  ├─ views/                  # 页面（用户管理、商品管理等）
│  ├─ components/             # 公共组件
│  ├─ router/                 # 路由配置
│  ├─ store/                  # Vuex 数据存储
│  ├─ utils/                  # 工具函数
│  └─ assets/                 # 图片、样式等资源
├─ package.json              # npm 依赖配置
└─ vue.config.js              # Vue 配置文件
```

### 学习资源
- Vue.js 官方文档：https://cn.vuejs.org/
- Element UI 文档：https://element.eleme.cn/
- Axios 文档：https://axios-http.com/
- Vue Router 文档：https://router.vuejs.org/zh/

```

---

## 📞 获取帮助

- 项目文档：[doc/README.md](doc/README.md)
- API 文档：[doc/api.md](doc/api.md)
- 常见问题：[doc/FAQ.md](doc/FAQ.md)
- 数据库文档：[doc/database.md](doc/database.md)
- GitHub 问题：https://github.com/linlinjava/litemall/issues

---

## 📝 更新日志

- **2026-04-19**: 初始创建，基于 litemall-plus v0.1.0

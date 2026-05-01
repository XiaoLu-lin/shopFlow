# shopflow 分布式session实现

>  在每次编写完之后，记得在修改模块，clear，install，import一下

## 实现

### 模块

**shopflow-admin-api 模块**

### 依赖

pom文件添加 redis依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<!-- redis Lettuce 模式 连接池 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

### 配置文件

在application-admin.yml添加redis配置文件

```yml
spring:
  redis:
    host: 192.168.79.131
    port: 6379
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 20
        max-wait: 3000ms
        max-idle: 8
        min-idle: 0
```

### 编写RedisSessionDAO

```java
@Component
public class RedisSessionDAO extends AbstractSessionDAO {

    // session 过期时间，可以在配置文件 @Value("${expire_time}")
    private final long EXPIRE_TIME = 3600000L * 6;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    protected Serializable doCreate(Session session) {
        // TODO 使用分布式id生成，实现 SessionIdGenerator接口 重写setSessionIdGenerator方法
        // 生成sessionId
        Serializable sessionId = this.generateSessionId(session);
        // 绑定sessionId
        this.assignSessionId(session, sessionId);
        // store session
        redisTemplate.opsForValue().set(sessionId, session, EXPIRE_TIME, TimeUnit.MILLISECONDS);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = (Session)redisTemplate.opsForValue().get(sessionId);
        if (session == null) {
            throw new UnknownSessionException("can not find this session, sessionId = " + sessionId);
        }
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) throw new UnknownSessionException("session or sessionId is null");
        redisTemplate.opsForValue().set(session.getId(), session, EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) throw new UnknownSessionException("session or sessionId is null");
        redisTemplate.opsForValue().getOperations().delete(session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {

        // TODO 这里改为redis_session 前缀

        return redisTemplate.keys("*");
    }
}
```

### 配置RedisSessionDAO

在admin/confgi/ShiroConfig.java里面

```java
@Autowired
RedisSessionDAO redisSessionDAO;

@Bean
public SessionManager sessionManager() {
    AdminWebSessionManager adminWebSessionManager = new AdminWebSessionManager();
    adminWebSessionManager.setSessionDAO(redisSessionDAO);
    return adminWebSessionManager;
}
```

### ShopflowAdmin添加序列化

shopflow-db 模块，db/domain/ShopflowAdmin.java

```java
public class ShopflowAdmin implements Serializable{

}
```

## 参考文档

[Shiro session-management 官方文档](http://shiro.apache.org/session-management.html#session-clustering)

[Shiro SessionDAO 官方文档](http://shiro.apache.org/static/1.7.1/apidocs/org/apache/shiro/session/mgt/eis/SessionDAO.html)
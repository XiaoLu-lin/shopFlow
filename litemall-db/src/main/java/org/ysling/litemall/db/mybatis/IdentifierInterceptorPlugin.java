package org.ysling.litemall.db.mybatis;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 自定义ID生成器
 */
@Slf4j
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class IdentifierInterceptorPlugin implements Interceptor {

    /**
     * 雪花算法生成ID
     */
    public static String getNextId() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        return String.valueOf(snowflake.nextId());
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        for (Object arg : invocation.getArgs()) {
            if (arg instanceof MappedStatement) {
                MappedStatement statement = (MappedStatement) arg;
                //判断是否是insert类型不是直接跳过
                if (!SqlCommandType.INSERT.equals(statement.getSqlCommandType())) {
                    return invocation.proceed();
                }
            } else {
                //遍历插入对象参数
                for (Field field : arg.getClass().getDeclaredFields()) {
                    if (field.getName().equals("id")) {
                        // 关闭安全检查,可以达到提升反射速度
                        field.setAccessible(true);
                        // 生成并设置主键
                        field.set(arg, getNextId());
                        //log.info("set primary key: {}, value: {}", name, primaryKey);
                        return invocation.proceed();
                    }
                }
            }
        }
        return invocation.proceed();
    }


}

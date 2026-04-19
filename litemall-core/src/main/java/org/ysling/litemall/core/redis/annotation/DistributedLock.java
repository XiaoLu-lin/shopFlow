
package org.ysling.litemall.core.redis.annotation;

import org.ysling.litemall.core.redis.util.LockUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/** 
 * @author Ysling
 * @ClassName: DistributedLock
 * @Description: 分布式锁
 *  
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface DistributedLock {
	
	/**
	 * redis锁 名字
	 */
	String lockName() default "";

	/**
	 * redis锁 key 支持spel表达式
	 */
	String key() default "";
	
	/**
	 * 获取锁等待毫秒数，默认1毫秒，轮询等待的时间
	 */
	int waitTime() default 1;

	/**
	 * 锁过期秒数,默认为30分钟，程序出现异常的情况锁未被及时释放，该时间结束后会自动释放锁
	 */
	int expire() default LockUtil.DEFAULT_LEASE_MILLISECONDS;

	/**
	 * 超时时间单位
	 */
	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

}

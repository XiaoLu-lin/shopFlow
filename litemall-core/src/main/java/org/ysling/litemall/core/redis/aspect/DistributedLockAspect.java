package org.ysling.litemall.core.redis.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.stereotype.Component;
import org.ysling.litemall.core.redis.annotation.DistributedLock;
import org.ysling.litemall.core.redis.util.LockUtil;
import org.ysling.litemall.core.redis.util.SpelUtil;

/** 
 * @author Ysling
 * @ClassName: DistributedLockInterceptor
 * @Description: 分布式锁拦截器
 *  
 */
@Aspect
@Component
public class DistributedLockAspect {
	
	private static final String DISTRIBUTED_LOCK_PREFIX = "DISTRIBUTED_LOCK:";

	/**
	 * 注解切面
	 */
	@Around(value ="@annotation(distributedLock)")
	public Object distributedLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
		String key = distributedLock.key();
		String lockName = distributedLock.lockName();
		String lockKey = getKey(joinPoint, lockName, key);
		Object result = null;
		if(LockUtil.tryLock(lockKey, distributedLock.waitTime(), distributedLock.expire(), distributedLock.timeUnit())){
			try {
				result = joinPoint.proceed();
			} finally{
				LockUtil.unlock(lockKey);
			}
		}
		return result;
	}

	/**
	 * 获取锁
	 */
	private String getKey(ProceedingJoinPoint joinPoint, String lockName, String key) {
		// 采用SPEL 解析
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method targetMethod = methodSignature.getMethod();
		Object parse = SpelUtil.parse(joinPoint.getTarget(), key, targetMethod, joinPoint.getArgs());
		if (parse == null){
			parse = key;
		}
		return DISTRIBUTED_LOCK_PREFIX + lockName + parse;
	}

}

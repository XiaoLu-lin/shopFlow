package org.ysling.litemall.core.redis.util;

import org.ysling.litemall.core.redis.lock.Locker;

import java.util.concurrent.TimeUnit;


/**
 * @ClassName: LockUtil
 * @Description: redis分布式锁工具类
 * 
 */
public class LockUtil {

	private static Locker locker;

	/** 默认获取锁的等到毫秒数 */
	public static final int DEFAULT_WAIT_MILLISECONDS = 10;
	/** 默认获取锁后超时释放锁的持有时间:30分钟 (这个时间只在锁定后程序缺陷忘记解锁或解锁出异常时会触发自动释放锁)*/
	public static final int DEFAULT_LEASE_MILLISECONDS = 30 * 60 * 1000;

	/**
	 * 设置工具类使用的locker
	 */
	public static void setLocker(Locker locker) {
		LockUtil.locker = locker;
	}

	/**
	 * 获取锁，如果锁不可用，则当前线程处于休眠状态，直到获得锁为止。
	 * @param lockKey 锁的名称
	 */
	public static void lock(String lockKey) {
		locker.lock(lockKey);
	}

	/**
	 * 释放锁  重入的方式，所以同一个线程加了几次锁就要释放几次锁
	 * @param lockKey 锁的名称
	 */
	public static void unlock(String lockKey) {
		locker.unlock(lockKey);
	}

	/**
	 * 获取锁,如果锁不可用，则当前线程处于休眠状态，直到获得锁为止。如果获取到锁后，执行结束后解锁或达到超时时间后会自动释放锁
	 * @param lockKey 锁的名称
	 * @param timeout 超时时间
	 */
	public static void lock(String lockKey, int timeout) {
		locker.lock(lockKey, timeout);
	}

	/**
	 * 获取锁,如果锁不可用，则当前线程处于休眠状态，直到获得锁为止。如果获取到锁后，执行结束后解锁或达到超时时间后会自动释放锁
	 * @param lockKey 锁的名称
	 * @param unit 	  时间的单位
	 * @param timeout 超时时间
	 */
	public static void lock(String lockKey, TimeUnit unit, int timeout) {
		locker.lock(lockKey, unit, timeout);
	}

	/**
	 * 尝试获取锁，获取到立即返回true,未获取到立即返回false
	 * @param lockKey 锁的名称
	 * @return 获取到立即返回true,未获取到立即返回false
	 */
	public static boolean tryLock(String lockKey) {
		return locker.tryLock(lockKey);
	}

	/**
	 * 尝试获取锁，如果是锁定状态，立即返回，否则获取锁 最长持有锁的时间{@link cn.trstech.ecommerce.cache.util.LockUtil#DEFAULT_LEASE_MILLISECONDS},
	 * 默认的不满足要求，则请调用{@link tryLock(String, long, long, TimeUnit)}
	 * <pre>
	 * 分布式锁使用方法如下：
     * 
     * String lockKey ...
	 * if (LockUtil.tryLockDefault(lockKey)) {
	 *  	try {
	 *    		// 获取到锁的操作流程
	 *  		} finally {
	 *    		LockUtil.unlock(lockKey);
	 *  	}
	 * } else {
	 *  	// 未获取到锁的操作流程
	 * }}
     * </pre>
	 * 
	 * @param lockKey 锁的名称
	 * @return 是否获取到锁
	 */
	public static boolean tryLockDefault(String lockKey) {
		try {
			if (locker.isLocked(lockKey)) {
				return false;
			}
			return locker.tryLock(lockKey, DEFAULT_WAIT_MILLISECONDS, DEFAULT_LEASE_MILLISECONDS, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			return false;
		}
	}


	/**
	 * 获取锁-同一个线程可重入
	 * 尝试获取锁，在等待时间内获取到锁则返回true,否则返回false,如果获取到锁，则要么执行完后程序释放锁，
	 * 要么在给定的超时时间leaseTime后释放锁
	 * @param lockKey  锁的名称
	 * @param waitTime 获取锁的等待时间
	 * @param leaseTime 锁的持续时间
	 * @param unit  时间的单位
	 * @return 获取锁的结果
	 */
	public static boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
		return locker.tryLock(lockKey, waitTime, leaseTime, unit);
	}

	/**
	 * 锁是否被任意一个线程锁持有
	 * @param lockKey 锁的名称
	 * @return 获取到立即返回true,未获取到立即返回false
	 */
	public static boolean isLocked(String lockKey) {
		return locker.isLocked(lockKey);
	}

}

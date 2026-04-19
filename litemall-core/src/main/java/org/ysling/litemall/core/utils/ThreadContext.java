
package org.ysling.litemall.core.utils;

import java.util.HashMap;
import java.util.Map;

public class ThreadContext {
	
	private ThreadContext() {

	}

	/**
	 * 线程容器
	 */
	private static final ThreadLocal<Map<Object, Object>> local = new ThreadLocal$Map<Map<Object, Object>>();

	/**
	 * 设置线程中的对象
	 * 
	 * @param key
	 * @param value
	 */
	public static void put(Object key, Object value) {
		Map<Object, Object> m = getThreadMap();
		m.put(key, value);
	}

	/**
	 * 取得线程中的对象
	 * 
	 * @param key
	 * @return
	 */
	public static Object get(Object key) {
		Map<Object, Object> m = getThreadMap();
		return m.get(key);
	}

	/**
	 * 删除线程中的对象
	 * 
	 * @param key
	 * @return
	 */
	public static void remove(Object key) {
		Map<Object, Object> m = getThreadMap();
		m.remove(key);
	}

	/**
	 * 清空线程Map
	 * 
	 * @return
	 */
	public static void clear() {
		Map<Object, Object> m = getThreadMap();
		m.clear();
	}

	/**
	 * 是否包含Key
	 * 
	 * @param key
	 * @return
	 */
	public static boolean containsKey(Object key) {
		Map<Object, Object> m = getThreadMap();
		return m.containsKey(key);
	}

	/**
	 * 批量添加
	 * 
	 * @param p
	 * @return
	 */
	public static void putAll(Map<Object, Object> p) {
		Map<Object, Object> m = getThreadMap();
		m.putAll(p);
	}

	/**
	 * 取得线程中的Map
	 * 
	 * @return
	 */
	private static Map<Object, Object> getThreadMap() {
		return local.get();
	}

	/**
	 * 销毁线程变量Map
	 */
	public static void destory() {
		local.remove();
	}

	/**
	 * 内部类实现
	 * 
	 * @param <T>
	 */
	private static class ThreadLocal$Map<T> extends ThreadLocal<Map<Object, Object>> {
		@Override
		protected Map<Object, Object> initialValue() {
			return new HashMap<>();
		}
	}

}

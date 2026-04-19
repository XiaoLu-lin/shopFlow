package org.ysling.litemall.core.redis.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/** 
 * @ClassName: BeanUtil 
 * @Description: Bean 工具类
 *  
 */
public final class BeanUtil {
	
	/** Bean方法名中属性名开始的下标 */
	private static final int BEAN_METHOD_PROP_INDEX = 3;

	/** * 匹配getter方法的正则表达式 */
	private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

	/** * 匹配setter方法的正则表达式 */
	private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

	
	
	/**
	 * 数据拷贝
	 * 
	 * @param src
	 *            源
	 * @param dest
	 *            目标
	 */
	public static void copy(Object src, Object dest) {
		if (src == null || dest == null) {
			return;
		}
		if (src instanceof Map) {
			copyMap2Bean(null, (Map<?, ?>) src, dest);
		} else {
			copyBean2Bean(src, dest);
		}
	}
	
	
	/**
	 * 对象转map,浅拷贝
	 */
    public static Map<String, Object> bean2Map(Object obj){
        Map<String, Object> map = new HashMap<>();
        if(obj==null){
            return null;
        }
        Field[] fields = obj.getClass().getDeclaredFields();//获取类的各个属性值
        for(Field field : fields){
        	if(Modifier.isStatic(field.getModifiers())){
				continue;
			}
            String fieldName =  field.getName();//获取类的属性名称
            try {
            	Object v = getPropertyValue(obj, fieldName);
            	if( v != null)//获取类的属性名称对应的值
                {
                    map.put(fieldName,  v);
                }
			} catch (Exception ignored) {
				
			}
        }
        return map;
    }
	
	/**
	 * 拷贝Bean->Bean
	 * 
	 * @param src
	 *            源
	 * @param dest
	 *            目标
	 */
	private static void copyBean2Bean(Object src, Object dest) {
		Class<?> srcClass = src.getClass();
		Class<?> destClass = dest.getClass();
		try {
			if (srcClass.equals(destClass)) {// 源类型与目标类型相同
				Field[] fields = getBeanFields(src.getClass());
				for (Field field : fields) {
					if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
						field.setAccessible(true);
						field.set(dest, field.get(src));
					}
				}
			} else {// 源类型与目标类型不同
				Field[] srcFields = getBeanFields(src.getClass());
				Field[] destFields = getBeanFields(dest.getClass());
				for (Field srcField : srcFields) {
					for (Field destField : destFields) {
						if (!Modifier.isStatic(srcField.getModifiers()) && !Modifier.isFinal(srcField.getModifiers())) {
							if (!Modifier.isStatic(destField.getModifiers())
									&& !Modifier.isFinal(destField.getModifiers())) {
								if ( srcField.getName().equals(destField.getName()) 
										&& destField.getType().isAssignableFrom(srcField.getType()) ) {
									srcField.setAccessible(true);
									destField.setAccessible(true);
									Object srcValue = srcField.get(src);
									destField.set(dest, srcValue);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 拷贝Map->Bean
	 *
	 * @param src
	 *            源
	 * @param dest
	 *            目标
	 */
	private static void copyMap2Bean(String prefix, Map<?, ?> src, Object dest) {
		Field[] fields = getBeanFields(dest.getClass());
		for (Field field : fields) {
			if (isAccessField(field)) {
				field.setAccessible(true);
				if (isSetValueEnable(prefix, src, field)) {// 是否需要设置值,Map里面有的才映射过去
					setFieldValue(prefix, src, dest, field);
				}
			}
		}
	}
	
	/**
	 * 设置字段值
	 * @param prefix 前缀
	 * @param src 源对象
	 * @param dest 目标对象
	 * @param field 目标字段
	 */
	private static void setFieldValue(String prefix, Map<?, ?> src, Object dest, Field field) {
		Class<?> clazz = field.getType();
		String value = null;
		try {
			value = getValue(prefix, src, field);
			if (value == null) {
				if( clazz.isPrimitive() ){//基本类型
					field.set(dest, 0);
				}else{
					field.set(dest, null);
				}
			} else {
				boolean isNumber = NumberUtil.isNumber(value);
				if (String.class.isAssignableFrom(clazz)) {
					field.set(dest, value);
				}else if (Integer.class.isAssignableFrom(clazz)) {
					field.set(dest, isNumber ? Integer.valueOf(value) : null);
				}else if (Long.class.isAssignableFrom(clazz)) {
					field.set(dest, isNumber ? Long.valueOf(value) : null);
				}else if(Double.class.isAssignableFrom(clazz)){
					field.set(dest, isNumber ? Double.valueOf(value) : null);
				}else if (BigDecimal.class.isAssignableFrom(clazz)) {
					field.set(dest, isNumber ? new BigDecimal(value) : null);
				}else if (Byte.class.isAssignableFrom(clazz)) {
					field.set(dest, isNumber ? Byte.valueOf(value) : null);
				}else if (java.sql.Date.class.isAssignableFrom(clazz)) {// java.sql.Date
					Timestamp date = DateUtil.parse(value);
					field.set(dest, new java.sql.Date(date.getTime()));
				} else if (Date.class.isAssignableFrom(clazz)) {
					Timestamp date = DateUtil.parse(value);
					field.set(dest, date);
				} else if (clazz.isPrimitive()) {//基本类型
					if( int.class.equals(clazz) ){
						field.set(dest, new Integer(value));
					}else if( long.class.equals(clazz) ){
						field.set(dest, new Long(value));
					}else if( double.class.equals(clazz) ){
						field.set(dest, new Double(value));
					}else if( float.class.equals(clazz) ){
						field.set(dest, new Float(value));
					}else if( short.class.equals(clazz) ){
						field.set(dest, new Short(value));
					}else if( boolean.class.equals(clazz) ){
						field.set(dest, Boolean.valueOf(value));
					} else if (byte.class.equals(clazz)) {
						field.set(dest, new Byte(value));
					}
				}else {
					Constructor<?> c = clazz.getConstructor(String.class);
					Object object = c.newInstance(value);
					field.set(dest, object);
				}
			}
		} catch (Exception e) {
			//log.error(field + ".setValue(" + value + ")");
			e.printStackTrace();
		}
	}
	
	/**
	 * 取得属性值
	 */
	private static String getValue(String prefix, Map<?, ?> src, Field field) {
		Object value;
		if (StringUtil.isValid(prefix)) {
			value = src.get(merge(prefix, field.getName()));
		} else {
			value = src.get(field.getName());
		}
		if( value instanceof String ){
			return (String)value;
		}else if( value instanceof Date ){
			return DateUtil.formatFull((Date)value);
		}
		return StringUtil.isEmpty(value) ? null : value.toString();
	}
	
	/**
	 * 取得属性
	 */
	private static String merge(String prefix, String name) {
		return prefix + "-" + name;
	}
	
	/**
	 * 字段是否需要设置值
	 */
	private static boolean isSetValueEnable(String prefix, Map<?, ?> src, Field field) {
		if (StringUtil.isValid(prefix)) {
			return src.containsKey(merge(prefix, field.getName()));
		}
		return src.containsKey(field.getName());
	}
	
	
	/**
	 * 取得Bean字段
	 */
	public static Field[] getBeanFields(Class<?> clazz){
		List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
		while( (clazz = clazz.getSuperclass()) != null ){
			 Field[] supperFields = clazz.getDeclaredFields();
			 fields.addAll(Arrays.asList(supperFields));
		}
		List<Field> accessFields = new ArrayList<>();
		for (Field field : fields) {
			 if ( isAccessField(field) ){
				 accessFields.add(field);
			 }
		 }
        return accessFields.toArray(new Field[]{});
	}
	
	/**
	 * 是否是需要设置的字段
	 */
	private static boolean isAccessField(Field field) {
		Class<?> clazz = field.getType();
		int m = field.getModifiers();
		if ((Modifier.isPrivate(m) || Modifier.isProtected(m)) && !Modifier.isStatic(m)) {
			// 是private非static
			return Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
					|| String.class.isAssignableFrom(clazz) || clazz.isPrimitive();
		}
		return false;
	}
	
	/**
	 * Bean属性复制工具方法。
	 * @param dest 目标对象
	 * @param src  源对象
	 */
	public static void copyBeanProp(Object dest, Object src) {
		List<Method> destSetters = getSetterMethods(dest);
		List<Method> srcGetters = getGetterMethods(src);
		try {
			for (Method setter : destSetters) {
				for (Method getter : srcGetters) {
					if (isMethodPropEquals(setter.getName(), getter.getName())
							&& setter.getParameterTypes()[0].equals(getter.getReturnType())) {
						setter.invoke(dest, getter.invoke(src));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取对象的setter方法。
	 * @param obj 对象
	 * @return 对象的setter方法列表
	 */
	public static List<Method> getSetterMethods(Object obj) {
		// setter方法列表
		List<Method> setterMethods = new ArrayList<>();
		// 获取所有方法
		Method[] methods = obj.getClass().getMethods();
		// 查找setter方法
		for (Method method : methods) {
			Matcher m = SET_PATTERN.matcher(method.getName());
			if (m.matches() && (method.getParameterTypes().length == 1)) {
				setterMethods.add(method);
			}
		}
		// 返回setter方法列表
		return setterMethods;
	}

	/**
	 * 获取对象的getter方法。
	 * 
	 * @param obj 对象
	 * @return 对象的getter方法列表
	 */

	public static List<Method> getGetterMethods(Object obj) {
		// getter方法列表
		List<Method> getterMethods = new ArrayList<Method>();
		// 获取所有方法
		Method[] methods = obj.getClass().getMethods();
		// 查找getter方法
		for (Method method : methods) {
			Matcher m = GET_PATTERN.matcher(method.getName());
			if (m.matches() && (method.getParameterTypes().length == 0)) {
				getterMethods.add(method);
			}
		}
		// 返回getter方法列表
		return getterMethods;
	}

	/**
	 * 检查Bean方法名中的属性名是否相等。<br>
	 * 如getName()和setName()属性名一样，getName()和setAge()属性名不一样。
	 * 
	 * @param m1 方法名1
	 * @param m2 方法名2
	 * @return 属性名一样返回true，否则返回false
	 */

	public static boolean isMethodPropEquals(String m1, String m2) {
		return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
	}
	
	
	/**
	 * 取得属性值
	 * 
	 * @param bean Bean对象
	 * @param propertyName 属性名
	 * @return 属性值
	 */
	@SuppressWarnings("unchecked")
	public static <K, T> K getPropertyValue(T bean, String propertyName) {
		if (bean instanceof Map) {
			return (K) ((Map) bean).get(propertyName);
		} else {
			Class<?> clazz = bean.getClass();
			String methodName = "get" + StringUtil.firstCharUpperCase(propertyName);
			try {
				Method method = clazz.getMethod(methodName);
				return (K) method.invoke(bean);
			} catch (Exception e) {
				try{
					methodName = "is" + StringUtil.firstCharUpperCase(propertyName);
					Method method = clazz.getMethod(methodName);
					return (K) method.invoke(bean);
				}catch(Exception ee){
					e.printStackTrace();
					throw new RuntimeException("无法找到属性对应的方法名(先getXXX，后isXXX):" + methodName, e);
				}
			}
		}
	}

	/**
	 * 取得Key对应的所有值
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getPropertyValues(List<?> list, String propertyName) {
		List<T> values = new ArrayList<T>(list.size());
		if (!list.isEmpty()) {
			Object bean0 = list.get(0);
			if (bean0 instanceof Map) {
				for (Object bean : list) {
					T value = (T) ((Map) bean).get(propertyName);
					values.add(value);
				}
			} else {
				try {
					Class<?> clazz = list.get(0).getClass();
					Method method = clazz.getMethod("get" + StringUtil.firstCharUpperCase(propertyName));
					for (Object bean : list) {
						T value = (T) method.invoke(bean, new Object[] {});
						values.add(value);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return values;
	}
	
	/**
	 * 根据字段名分组,字段名相同的分为一个组
	 */
	public static <K, V> Map<K, List<V>> groupByFiled(Collection<V> collection, String fieldName){
		if(CollectionUtils.isEmpty(collection)){
			return Maps.newHashMapWithExpectedSize(0);
		}
		Map<K, List<V>> map = Maps.newHashMap();
		for(V obj : collection){
			if(obj == null){
				continue;
			}
			K k = getPropertyValue(obj, fieldName);
			if(k == null){
				continue;
			}
			List<V> list = map.get(k);
			if(CollectionUtils.isEmpty(list)){
				list = Lists.newArrayList();
				list.add(obj);
				map.put(k, list);
			}else{
				list.add(obj);
			}
		}
		return map;
	}
	
	/**
	 * 设置集合对象元素的属性值
	 */
	public static <T> void setCollectionFieldValue(Collection<T> objs, String fieldName, Object value){
		if(CollectionUtils.isEmpty(objs)){
			return;
		}
		for(Object obj : objs){
			setFieldValue(obj, fieldName, value);
		}
	}

	/**
	 * 设置字段值
	 * 
	 * @param obj 目标对象
	 * @param fieldName 字段名
	 * @param value 字段值
	 */
	public static <T> void setFieldValue(T obj, String fieldName, Object value) {
		if (obj == null) {
			return;
		}
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals(fieldName)) {
					field.setAccessible(true);
					field.set(obj, value);
				}
			}
		} catch (Exception e) {
			// do nothing
		}
	}
	
	/**
	 * 获取对象的值，如果当前类没有定义该域，则尝试检测父类
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("all")
	public static <T> T getFieldValueDeep(Object obj, String fieldName){
		if( obj == null ){
			return null;
		}
		Object value = null;
		try {
			Class<?> clazz = obj.getClass();
	        for(; clazz != Object.class; clazz = clazz.getSuperclass()){
	        	boolean fund = false;
	            Field[] fields = clazz.getDeclaredFields();
	            for (Field field : fields) {// 获取bean的属性和值
	            	if(field.getName().equals(fieldName) ){
						field.setAccessible(true);
						value = field.get(obj);
						fund = true;
						break;
					}
	            }
	            if(fund)break;
	        }
		} catch (Exception e) {
			//do nothing
		}
		return (T)value;
	}
	
	/**
	 * 数据求和
	 * 
	 * @param list 数据集
	 * @param properties 求和的属性
	 * @return 求和结果
	 */
	public static BigDecimal[] sum(List<?> list, Object... properties) {
		BigDecimal[] numbers = new BigDecimal[properties.length];
		for (int i = 0; i < properties.length; i++) {
			numbers[i] = BigDecimal.valueOf(0);
		}
		List<Object[]> objectList = getPropertiesValues(list, properties);
		for (Object[] objects : objectList) {
			for (int i = 0; i < objects.length; i++) {
				if( numbers[i] == null ){
					numbers[i] = toNumber(objects[i]);
				}else{
					numbers[i] = numbers[i].add(toNumber(objects[i]));
				}
			}
		}
		return numbers;
	}
	
	/**
	 * 转换为数字
	 * @param value 对象
	 * @return 数字
	 */
	private static BigDecimal toNumber(Object value) {
		if (StringUtil.isEmpty(value)) {
			return new BigDecimal(0);
		}else if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}else if (value instanceof Number) {
			return new BigDecimal(value.toString());
		}else if (value instanceof String) {
			return new BigDecimal((String) value);
		}
		throw new RuntimeException("[" + value.getClass() + ";value=" + value + "]无效的数据类型...");
	}
	
	/**
	 * 取得一些属性的值
	 * 
	 * @param list 数据集
	 * @param properties 属性名列表
	 * @return 数据集
	 */
	public static List<Object[]> getPropertiesValues(List<?> list, Object... properties) {
		if (list.isEmpty()) {
			return new ArrayList<>(0);
		}
		Object bean0 = list.get(0);
		List<Object[]> objectList = new ArrayList<>();
		if (bean0 instanceof Map) {// Map
			for (Object bean : list) {
				Map<?, ?> map = (Map<?, ?>) bean;
				Object[] objects = new Object[properties.length];
				for (int i = 0; i < properties.length; i++) {
					Object o = map.get(properties[i]);
					objects[i] = o;
				}
				objectList.add(objects);
			}
		} else if (bean0 instanceof Object[]) {// 对象数据
			for (Object bean : list) {
				Object[] source = (Object[]) bean;
				Object[] objects = new Object[properties.length];
				for (int i = 0; i < properties.length; i++) {
					Object o = source[((Number) properties[i]).intValue()];
					objects[i] = o;
				}
				objectList.add(objects);
			}
		} else if (bean0 != null) {// Object
			Class<?> clazz = list.get(0).getClass();
			try {
				for (Object bean : list) {
					Object[] objects = new Object[properties.length];
					for (int i = 0; i < properties.length; i++) {
						String methodName = "get" + StringUtil.firstCharUpperCase(String.valueOf(properties[i]));
						Method method = clazz.getMethod(methodName);
						Object o = method.invoke(bean);
						objects[i] = o;
					}
					objectList.add(objects);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return objectList;
	}

}

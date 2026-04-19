package org.ysling.litemall.core.redis.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** 
 * @ClassName: NumberUtil 
 * @Description: 数字工具类
 */
public final class NumberUtil {
	
	/**
	 * 格式 9,876,543,2.123
	 */
	private static final DecimalFormat COMMA_FORMAT = new DecimalFormat("#,###.######");

	/**
	 * 格式 0.8211 - > 82.11%
	 */
	private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.######%");

	/**
	 * 是否是数字
	 */
	private static final Pattern NUMBER_PATTERN = Pattern.compile("^(\\-)?[0-9]*\\.?[0-9]*");
	
	/**
	 * 是否是数字
	 */
	private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");

	private NumberUtil() {

	}

	/**
	 * 格式化数字： 9,876,543,2.123
	 * 
	 * @param value
	 * @return
	 */
	public static final String getCommaNumber(double value) {
		return COMMA_FORMAT.format(value);
	}

	/**
	 * 格式化数字： 9,876,543,2.123
	 * 
	 * @param value
	 * @return
	 */
	public static final String getCommaNumber(String value) {
		if (!StringUtil.isValid(value))
			return value;

		return getCommaNumber(Double.parseDouble(value));
	}

	/**
	 * 格式化数字：20%
	 * 
	 * @param value
	 * @return
	 */
	public static final String getPercentNumber(double value) {
		return PERCENT_FORMAT.format(value);
	}

	/**
	 * 格式化数字： 20%
	 * 
	 * @param value
	 * @return
	 */
	public static final String getPercentNumber(String value) {
		if (!StringUtil.isValid(value))
			return value;

		return getPercentNumber(Double.parseDouble(value));
	}

	/**
	 * 是否是数字
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNumber(String value) {
		return StringUtil.isValid(value) && NUMBER_PATTERN.matcher(value).matches();
	}
	
	/**
	 * 是否是数字
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isInteger(String value) {
		return StringUtil.isValid(value) && INTEGER_PATTERN.matcher(value).matches();
	}
	
	/**
	 * 计算
	 * @param expression 表达式
	 * @param args 参数
	 * @return 计算结果
	 */
	public static BigDecimal cal(String expression, Object... args){
		if( args.length % 2 != 0 ){
			throw new RuntimeException("参数个数错误！args=" + Arrays.asList(args));
		}
		Map<String,Number> map = new LinkedHashMap<String,Number>();
		for (int i = 0; i < args.length; i = i + 2) {
			Object key = args[i];
			Object value = args[i + 1];
			if( !(key instanceof String) ){
				throw new RuntimeException("参数名称不是字符串类型! name=" + key.getClass());
			}
			String name = (String)key;
			if( value instanceof Number ){
				map.put(name, (Number)value);
			}else if( value instanceof String ){
				map.put(name, new BigDecimal((String)value));
			}else {
				throw new RuntimeException("参数值格式错误! value=" + value);
			}
		}
		return null;//TODO
	}
	
	/**
	 * 格式化数字
	 * @param number 数字 12.123
	 * @param pattern 格式化 ##.00
	 * @return 格式化数字
	 */
	public static String format(Number number, String pattern){
		DecimalFormat format = new DecimalFormat(pattern);
		return format.format(number);
	}
	
	/**
	 * 补零
	 * @param value 值
	 * @param num 位数
	 * @return
	 */
	public static String fillZero(int value, int num) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumIntegerDigits(num);
        nf.setMinimumIntegerDigits(num);
        return nf.format(value);
	}
	
	/**
	 * 减法运算
	 * @param a
	 * @param b
	 * @return
	 */
	public static Number sub(Number a , Number b){
		return a.longValue() - b.longValue();
	}
	
	/**
	 * 将map中的参数Number强制转换为整型
	 * @param map
	 * @param param
	 */
	public static void constToInt(Map<String, Object> map, String param){
		Object obj = map.get(param);
		if(obj == null){
			return;
		}
		if(obj instanceof Number){
			map.put(param, ((Number)obj).intValue());
		}
	}
	
	public static int constToInt(Integer num){
		if(num == null){
			return 0;
		}
		return num.intValue();
	}
	
	public static <T> boolean equal(T t1, T t2){
		if(t1 == t2){
			return true;
		}
		if(t1 == null){
			return false;
		}
		return t1.equals(t2);
	}
	
	/**
	 * 将obj转换为泛型的具体类型
	 * @param obj 值
	 * @param type 泛型的class
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object obj, Class<T> type){
		if(obj == null){
			return null;
		}
		Object result = null;
		String str = obj.toString();
		if(type == Short.class){
			result = new Short(str);
		}else if(type == Integer.class){
			result = new Integer(str);
		}else if(type == Long.class){
			result = new Long(str);
		}else if(type == Float.class){
			result = new Float(str);
		}else if(type == Double.class){
			result = new Double(str);
		}else if(type == Byte.class){
			result = new Byte(str);
		}else if(type == Boolean.class){
			result = new Boolean(str);
		}else if(type == BigDecimal.class){
			result = new BigDecimal(str);
		}else{
			result = str;
		}
		return (T)result;
	}

	public static boolean isPositive(Number number) {
		return number.shortValue() >=0;
	}
	
	public static void main(String[] args) {
		
		System.out.println(isPositive(3L));
		System.out.println(isPositive(-3L));
		System.out.println(isPositive(0.0));
		System.out.println(isPositive(-33.3));		
		System.out.println(getCommaNumber("54321.12"));
		System.out.println(getCommaNumber("0.1211"));
		System.out.println(getCommaNumber("987654321"));
		System.out.println(getCommaNumber(99987654321.0000));
		System.out.println(getCommaNumber("99987654321.0000"));
		System.out.println(getCommaNumber(99987654321.00012212));
		System.out.println(isNumber("0.000167839249641237"));
		//计算表达式 第一种方式
		Map<String, Number> map = new HashMap<String, Number>();
		map.put("单价", 50); 
		map.put("数量", 10); 
		map.put("运费", 20); 
		//BigDecimal result = NumberUtil.cal("(单价*数量)+运费", map); 
		//System.out.println(result);
		//计算表达式 第二种方式
		//result = NumberUtil.cal("(单价*数量)+运费", "单价", 50, "数量", 10, "运费", 20); 
		//System.out.println(result);
		System.out.println("isNumber=" + isNumber(""));
	}

}

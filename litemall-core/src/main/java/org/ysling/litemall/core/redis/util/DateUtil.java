package org.ysling.litemall.core.redis.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期工具类
* @ClassName: DateUtil 
* @Description: 日期工具类
*
 */
public final class DateUtil {
	
	private static Logger log = LoggerFactory.getLogger(DateUtil.class);

	public static final int HOUR_OF_DAY = 24;
	public static final int MINUTE_OF_HOUR = 60;
	public static final int SECOND_OF_MINUTE = 60;
	public static final int MILLISECOND_OF_SECOND = 1000;
	
	public static final String CHINA_FORMAT = "yyyy年MM月dd日";
	
	/** 年月日时分秒毫秒(无下划线) yyyyMMddHHmmssSSS */
    public static final String DT_LONG_FULL = "yyyyMMddHHmmssSSS";
	
	/** 年月日时分秒(无下划线) yyyyMMddHHmmss */
    public static final String DT_LONG = "yyyyMMddHHmmss";
    
    /** 完整时间 yyyy-MM-dd HH:mm:ss */
    public static final String SIMPLE = "yyyy-MM-dd HH:mm:ss";
    
    /** 年月日(无下划线) yyyyMMdd */
    public static final String DT_SHORT = "yyyyMMdd";

	/** 在指定日期上增加或减少天数 * */
	public static Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}

	/** 在指定日期上增加或减少月数 * */
	public static Date addMonth(Date date, int month) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.MONTH, month);
		return startDT.getTime();
	}
	
	/** 在指定日期上增加或减少年数 * */
	public static Date addYear(Date date, int year) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.YEAR, year);
//		startDT.add(Calendar.DAY_OF_MONTH, -1);
		return startDT.getTime();
	}
	
	/** 在指定日期上增加或减少秒数 * */
	public static Date addSeconds(Date date, int seconds) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.SECOND, seconds);
		return startDT.getTime();
	}
	
	/** 将指定日期增加一天然后减去一毫秒*/
	public static Date addDayAndMinusSecond(Date date){
		if(date == null){
			return null;
		}
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, 1);
		startDT.add(Calendar.MILLISECOND, -1);
		return startDT.getTime();
	}

	/**
	 * @param offsetSecond
	 * @return 当前时间 + offsetSecond
	 */
	public static Date getNowExpiredSecond(int offsetSecond) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, offsetSecond);
		return now.getTime();
	}

	/**
	 * @param offsetSecond
	 * @return 当前时间 + offsetSecond
	 */
	public static long getNowExpiredSecondToMIllis(int offsetSecond) {
		Date date = getNowExpiredSecond(offsetSecond);
		return date.getTime();
	}
	
	/**
	 * @param offset
	 * @return 当前时间 + offsetHour
	 */
	public static Date getNowExpiredHour(int offset) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR, offset);
		return now.getTime();
	}
	
	/**
	 * @param offset
	 * @return 当前时间 + offsetMonth
	 */
	public static Date getNowExpiredMonth(int offset) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MONTH, offset);
		return now.getTime();
	}
	

	/**
	 * 当前时间开始 offset 天
	 * @param offset
	 * @return 当前时间 + offsetDay
	 */
	public static Date getNowExpiredDay(int offset) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, offset);
		return now.getTime();
	}

	/**
	 * between是否在startDate与endDate之间
	 */
	public static boolean between(Date between, Date startDate, Date endDate) {
		if (between == null || (startDate == null && endDate == null)) {
			return false;
		}
		if (startDate != null && between.getTime() < startDate.getTime()) {
			return false;
		}
		if (endDate != null && between.getTime() > endDate.getTime()) {
			return false;
		}
		return true;
	}

	/**
	 * 获取两个日期间隔天数，满24小时才算1天，结果有正负之分
	 */
	public static int getIntervalDays(Date start, Date end) {
		return (int) Math.floor((end.getTime() - start.getTime()) / (double) (MILLISECOND_OF_SECOND * SECOND_OF_MINUTE * MINUTE_OF_HOUR * HOUR_OF_DAY));
	}
	
	/**
	 * 获取两个时间相差天数
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getIntervalDays_currt(Date start, Date end) {
		
		return getDays(end)-getDays(start);
	}

	public static int getDays(Date date) {
		if (date == null) {
			return -1;
		}	
		
		return  (int) (getFirstSecOfDate(date).getTime()/(1L*MILLISECOND_OF_SECOND * SECOND_OF_MINUTE * MINUTE_OF_HOUR * HOUR_OF_DAY));
	}
	/**
	 * 获取当前日期 精确到天 时间全为0时0分0秒
	 * 
	 * @return
	 */
	public static Date getCurrenctDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 转换当前日期的格式
	 * @param pattern 格式模板
	 * @return
	 */
	public static String convertCurrentDateFormat(String pattern){
		if(pattern == null){
			pattern = CHINA_FORMAT;
		}
		return convertDateToString(new Date(), pattern);
	}

	/**
	 * 将日期按照对应格式转换成字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String convertDateToString(Date date, String pattern) {
		if (pattern == null) {
			throw new NullPointerException("pattern is  null");
		}
		SimpleDateFormat dt = new SimpleDateFormat(pattern);
		String sdate = "";
		if(date != null){
			sdate = dt.format(date);
		}
		return sdate;
	}

	public static Date strToDate(String str, String pattern) {
		if(StringUtil.isEmpty(str)){
			return null ;
		}
		if (pattern == null) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat dt = new SimpleDateFormat(pattern);
		try {
			return dt.parse(str);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getDayOfWeek(Date date) {
		if (date == null) {
			return -1;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	// 1 奇数, 2 偶数
	public static int isOddDay(Date date) {
		if (date == null) {
			return -1;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int num = cal.get(Calendar.DAY_OF_MONTH);
		return num % 2 == 0 ? 2 : 1;
	}
	
	public static long getTime(Date date, String pattern) {
		if(date == null) {
			return 0;
		}
		return DateUtil.strToDate(DateUtil.convertDateToString(date, pattern), pattern).getTime();
	}
	
	/**
	 * 格式化日期字符串
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static String format(Date date) {
		if (date == null) {
			return null;
		}
		DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
		return YYYY_MM_DD.format(date);
	}
	
	/**
	 * 格式化日期字符串
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            日期格式
	 * @return
	 */
	public static String format(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		DateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static String formatFull(Date date) {
		DateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return YYYY_MM_DD_HH_MM_SS.format(date);
	}
	
	/**
	 * 解析日期 yyyy-MM-dd
	 * 
	 * @param date
	 *            日期字符串
	 * @return
	 */
	public static Timestamp parse(Object object) {
		if( object instanceof Date){
			return new Timestamp( ( (Date)object).getTime() );
		}
		if (StringUtil.isEmpty(object))
			return null;
		String date = StringUtil.asString(object);
		try {
			if (date.length() == 10) {
				return parseSimple(date);
			} else if (date.length() == 8) {
				DateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
				Date d = yyyyMMdd.parse(date);
				return new Timestamp(d.getTime());
			} else if (date.length() == 9) {
				if( date.matches("\\d{4}-\\d-\\d{2}") ){//yyyy-M-dd
					DateFormat yyyyMdd = new SimpleDateFormat("yyyy-M-dd");
					Date d = yyyyMdd.parse(date);
					return new Timestamp(d.getTime());
				}else if( date.matches("\\d{4}-\\d{2}-\\d") ){//yyyy-MM-d
					DateFormat yyyyMdd = new SimpleDateFormat("yyyy-MM-d");
					Date d = yyyyMdd.parse(date);
					return new Timestamp(d.getTime());
				}
			}else if (date.length() == 16) {//yyyy-MM-dd HH:mm
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date d = format.parse(date);
				return new Timestamp(d.getTime());
			}else if (date.length() == 19) {//yyyy-MM-dd HH:mm:ss
				return parseFull(date);
			}else if (date.length() >= 20 && date.length() <= 23 ) {//yyyy-MM-dd HH:mm:ss.SSS
				if( date.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{1,3}") ){
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					Date d = format.parse(date);
					return new Timestamp(d.getTime());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 解析日期 yyyy-MM-dd
	 * 
	 * @param date
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return
	 */
	public static Timestamp parseSimple(String date) {
		if(date == null) {
			return null;
		}
		Date result = null;
		try {
			DateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
			result = yyyyMMdd.parse(date);
		} catch (ParseException e) {
			log.error(e.toString());
		}
		return result != null ? new Timestamp(result.getTime()) : null;
	}

	/**
	 * 解析日期字符串
	 * 
	 * @param date
	 * @return
	 */
	public static Timestamp parseFull(String date) {
		Date result = null;
		try {
			DateFormat yyyyMMddHHmmss = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			result = yyyyMMddHHmmss.parse(date);
		} catch (ParseException e) {
			log.error(e.toString());
		}
		return result != null ? new Timestamp(result.getTime()) : null;
	}
	
	/**
	 * 取得当前日期
	 * 
	 * @return
	 */
	public static Timestamp getNow() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * 比较日期 1大于  0相等  -1小于 ,d1和d2 都是null 返回0, d1==null 返回 -1, d2==null 返回 1
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int compareDate(Date d1, Date d2){
		if(d1 == null && d2 == null){
			return 0;
		}else if (d1 ==  null) {
			return -1;
		}else if(d2 == null){
			return 1;
		}
		
		
		if(d1 == d2){
			return 0;
		}
		if(d1.getTime() - d2.getTime() > 0){
			return 1;
		}else if(d1.getTime() - d2.getTime() == 0){
			return 0;
		}else{
			return -1;
		}
	}
	
	/**
	 * 获取当天日期的下一天日期，只有年月日
	 * @return
	 */
	public static Date getNextDate(){
		Calendar calendar = Calendar.getInstance();
		Date currentDate = getCurrenctDate() ;
		calendar.setTime(currentDate);
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime() ;
	}
	/**
	 * 指定日期的第一秒 如2008-08-08 00:00:00
	 * @param date
	 * @return
	 */
	public static Date getFirstSecOfDate(Date date) {
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    String formatDate = format.format(date);
	    formatDate += " 00:00:00";
	    Date result = null;
        try {
            result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(formatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
	    return result;
	}
	/**
     * 指定日期的最后一秒 如2008-08-08 23:59:59
	 * @param date
	 * @return
	 */
	public static Date getLastSecOfDate(Date date) {
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = format.format(date);
        formatDate += " 23:59:59";
        Date result = null;
        try {
            result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(formatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
	
	/**
	 * 指定日期的下一天
	 * @param date 指定的日期
	 * @return     指定日期的下一天
	 */
    public static Date getNextOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 查询两个时间的间隔天数      返回date1距date2已过去多少天了  ep. date1=today   date2=tomrrow  返回结果：1
     * @param date1 
     * @param date2
     * @return  两个时间的间隔天数
     */
    public static int getGapDays(Date date1, Date date2) {
        date1 = getFirstSecOfDate(date1);
        date2 = getFirstSecOfDate(date2);
        Calendar calendar = Calendar.getInstance();    
        calendar.setTime(date1);
        long time1 = calendar.getTimeInMillis();
        calendar.setTime(date2);
        long time2 = calendar.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }
    
    
    /**
     * 返回指定日期一年中的天数
     * @param date
     * @return
     */
    public static int getYYYYMMOfDate(Date date) {
    	
    	if(date == null) {
    		return -1;
    	}
    	
    	String format = format(date, "yyyyMM");
        
        return Integer.parseInt(format);
    }
    
    /**
     * 返回指定日期一年中的月数 
     * @param date 不为空
     * @return
     */
    public static int getYYYYMMDDOfDate(Date date) {
    	
    	if(date == null) {
    		return -1;
    	}
    	
    	String format = format(date, "yyyyMMdd");
        
        return Integer.parseInt(format);
    }
    
    /**
	 * 格式化日期字符串
	 * 
	 * @param date  日期
	 * @param pattern 日期格式
	 * @param local 区域信息
	 * @return
	 */
	public static String format(Date date, String pattern, Locale local) {
		if (date == null) {
			return null;
		}
		DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
		return format.format(date);
	}

	public static Date getDateForYYYYMM(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
    /**
     * 批量格式化日期
     * @param dates     需要格式化的日期集合
     * @param pattern   格式化的样式
     * @return
     */
	public static List<String> format(List<Date> dates, String pattern) {
	    List<String> result = new ArrayList<String>();
	    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
	    for (Date date : dates) {
	        result.add(dateFormat.format(date));
	    }
	    return result;
	}
    
    /**
     * 取指定间隔天数的日期列表
     * @param sDate     开始时间
     * @param eDate     结束时间
     * @param gapDay    间隔天数
     * @return
     */
    public static List<Date> queryEveryday(Date sDate, Date eDate, int gapDay) {
        gapDay = gapDay <= 0 ? 1 : gapDay;
        Date startDate = getFirstSecOfDate(sDate);
        Date endDate = getLastSecOfDate(eDate);
        int dayPoint = (getGapDays(startDate, endDate) + 1) / gapDay;
        List<Date> result = new ArrayList<Date>(dayPoint);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        do {
            result.add(calendar.getTime());
            calendar.add(Calendar.DATE, gapDay);
        } while (compareDate(endDate, calendar.getTime()) >= 0);
        return result;
    }
    
    /**
     * 取指定日期内的间隔几周的指定星期日期
     * @param sDate         开始日期
     * @param eDate         结束日期
     * @param dateOfWeek    星期几
     * @param gapWeek       间隔的周数
     * @return
     */
    public static List<Date> queryDayOfWeek(Date sDate, Date eDate, int dateOfWeek, int gapWeek) {
        gapWeek = gapWeek <= 0 ? 1 : gapWeek;
        Date startDate = getFirstSecOfDate(sDate);
        Date endDate = getLastSecOfDate(eDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        List<Date> result = new ArrayList<Date>();
        do {
            calendar.set(Calendar.DAY_OF_WEEK, dateOfWeek);
            result.add(calendar.getTime());
            calendar.add(Calendar.DATE, 7 * gapWeek);
        } while (DateUtil.compareDate(calendar.getTime(), endDate) <= 0
                || calendar.get(Calendar.WEEK_OF_YEAR) <= endCalendar.get(Calendar.WEEK_OF_YEAR));
        return result;
    }
    /**
     * 返回指定日期在一周中的指定星期几的日期
     * @param date
     * @param dateOfWeek
     * @return
     */
    public static Date getDayOfWeek(Date date, int dateOfWeek) {
        date = getFirstSecOfDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, dateOfWeek);
        return calendar.getTime();
    }
    /**
     * 返回指定日期内指定间隔个月的每月的号数日期
     * @param sDate     开始日期
     * @param eDate     结束日期
     * @param whichDay  指定的号数
     * @param gapMonth  间隔的个月数
     * @return
     */
    public static List<Date> queryDayOfMonth(Date sDate, Date eDate, int whichDay, int gapMonth) {
        gapMonth = gapMonth <= 0 ? 1 : gapMonth;
        Date startDate = getDayOfMonth(sDate, whichDay);
        Date endDate = getDayOfMonth(eDate, whichDay);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        List<Date> result = new ArrayList<Date>();
        do {
            result.add(getDayOfMonth(calendar.getTime(), whichDay));
            calendar.add(Calendar.MONTH, gapMonth);
        } while (compareDate(calendar.getTime(), endDate) <= 0);
        return result;
    }

    /**
     * 取日期所在的那个月的多少号
     * @param date  指定的日期
     * @param whichDay  指定的号数
     * @return
     */
    public static Date getDayOfMonth(Date date, int whichDay) {
        if (whichDay < 1) {
            whichDay = 1;
        }
        if (whichDay > 28) {
            whichDay = 28;
        }
        String dayOfMonth;
        if (whichDay < 10) {
            dayOfMonth = "-0" + whichDay;
        } else {
            dayOfMonth = "-" + whichDay;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(format(date, "yyyy-MM") + dayOfMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     * 取指定日期内指定年限间隔的每年的第一天 
     * e.g.传入 2015-09-01,2017-05-02,1则返回2015-01-01、2016-01-01、2017-01-01
     * @param sDate
     * @param eDate
     * @param gapYear
     * @return
     */
    public static List<Date> queryFirstDayOfYear(Date sDate, Date eDate, int gapYear) {
        Date startDate = getFirstDayOfYear(sDate);
        Date endDate = getFirstDayOfYear(eDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getFirstDayOfYear(startDate));
        List<Date> result = new ArrayList<Date>();
        do {
            result.add(calendar.getTime());
            calendar.add(Calendar.YEAR, gapYear);
        } while (compareDate(endDate, calendar.getTime()) >= 0);
        return result;
    }
    /**
     * 返回指定日期所在年的第一天
     * e.g.input:2015-06-01,output:2015-01-01
     * @param date
     * @return
     */
    public static Date getFirstDayOfYear(Date date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(format(date, "yyyy") + "-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
	 * 获取当前日期 精确到月
	 * 
	 * @return
	 */
	public static String getCurrenctDateMoth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) +1);
	}
	
	/**
	 * 
	 * @Description 取日期所在的月的多少号
	 * @param date  指定日期
	 * @param whichDay 那一号
	 * @return
	 * @author owen.he
	 * @date 2016年1月29日 下午5:01:47
	 */
    public static Date getDayOfCurrentMonth(Date date, int whichDay) {
        if (whichDay < 1) {
            whichDay = 1;
        }
        if(whichDay > 31){
        	whichDay = 30;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
		calendar.set(Calendar.DATE, whichDay);
        return calendar.getTime();
    }

    /**
     * 两个日期间隔的个月数
     * @param date1
     * @param date2
     * @return
     */
    public static int getMonthSpace(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return -1;
        }
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        int year1 = calendar1.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int month2 = calendar2.get(Calendar.MONTH);
        return (year1 - year2) * 12 + month1 - month2;
    }
    
    /**
     * 获取指定日期下的当月第一天
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		if (null != date) {
			calendar.setTime(date);
		}
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
    
    /**
     * 
     * <p>Title: getLastDayOfMonth</p>  
     * <p>Description:获取指定日期下的当月最后一天 </p>  
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
    	Calendar calendar = Calendar.getInstance();
    	if (null != date) {
			calendar.setTime(date);
		}
    	calendar.add(Calendar.MONTH, 1);  
    	calendar.set(Calendar.DAY_OF_MONTH, 0); 
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	String lastDay = format.format(calendar.getTime()); 
    	System.out.println(lastDay);
    	return calendar.getTime();
    }
    
    /**
     * 
     * <p>Title: compTime</p>  
     * <p>Description:比较两个时间的时、分、秒的大小 </p>  
     * @param date1
     * @param date2
     * @return date1的时分秒>date1的时分秒返回true；反之
     */
    public static boolean compTime(Date date1,Date date2) {
    	Calendar calendar1 = Calendar.getInstance();
    	Calendar calendar2 = Calendar.getInstance();
		if (null != date1) {
			calendar1.setTime(date1);
		}
		if (null != date1) {
			calendar2.setTime(date2);
		}
		int totalSecond1 = calendar1.get(Calendar.HOUR_OF_DAY) * 3600 + calendar1.get(Calendar.MINUTE) * 60 + calendar1.get(Calendar.SECOND);
		int totalSecond2 = calendar2.get(Calendar.HOUR_OF_DAY) * 3600 + calendar2.get(Calendar.MINUTE) * 60 + calendar2.get(Calendar.SECOND);
		return totalSecond1 - totalSecond2 > 0 ? true : false;
    }
    
    /**
     * 
     * <p>Title: differDays</p>  
     * <p>Description: 计算两个日期相差天数</p>  
     * @param date1
     * @param date2
     * @return
     */
    public static Double differDays(Date date1,Date date2) {
    	if(date1 == null || date2 == null) {
    		return 0D;
    	}
    	Long differ = date1.getTime() - date2.getTime();
    	Double days = differ.doubleValue() / (24 * 60 * 60 * 1000);
    	BigDecimal daysBigDecimal = BigDecimal.valueOf(days).setScale(BigDecimal.ROUND_HALF_UP,4);
    	return daysBigDecimal.doubleValue();
    }
    
    /**
     * 
     * <p>Title: getThisYear_12Month_12Day</p>  
     * <p>Description: 获取当年12月12日的时间</p>  
     * @return
     */
    public static Date getThisYear_12Month_12Day() {
    	Date nowDate = new Date();
    	Calendar calendar1 = Calendar.getInstance();
    	calendar1.setTime(nowDate);
    	int year = calendar1.get(Calendar.YEAR);
//    	System.out.println(year);
    	String this_year_12Month_12Day_Str = year+"-12-12 00:00:00";
    	Date this_year_12Month_12Day_date = strToDate(this_year_12Month_12Day_Str, SIMPLE);
		return this_year_12Month_12Day_date;
    }
    
    /**  
	 * <p>Title: getNextYear_12Month_12Day</p>  
	 * <p>Description: 获取次年12月12日的时间</p>  
	 * @return  
	 */  
	public static Date getNextYear_12Month_12Day() {
		Date nowDate = new Date();
    	Calendar calendar1 = Calendar.getInstance();
    	calendar1.setTime(nowDate);
    	int year = calendar1.get(Calendar.YEAR);
    	year = year + 1;
    	String next_year_12Month_12Day_Str = year+"-12-12 00:00:00";
    	Date next_year_12Month_12Day_date = strToDate(next_year_12Month_12Day_Str, SIMPLE);
		return next_year_12Month_12Day_date;
	}
	
	/**
     * LocalDateTime转为日期
     *
     * @param localDateTime LocalDateTime
     * @return 日期
     */
    public static Date localDateTimeToDate(final LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        final ZoneId zoneId = ZoneId.systemDefault();
        final ZonedDateTime zdt = localDateTime.atZone(zoneId);
        final Date date = Date.from(zdt.toInstant());
        return date;
    }
    
	/**
	 * Date 转为 LocalDate
	 * 
	 * @param  date
	 * @return LocalDate;
	 */
	public static LocalDateTime dateToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
    
    
    public static void main(String[] args) throws InterruptedException {
    	getLastDayOfMonth(null);
    	/*Date date = getNextYear_12Month_12Day();
    	System.out.println(formatFull(date));*/
    	/*Date date1 = new Date();
		Thread.sleep(2000);
		Date date2 = new Date();
    	System.out.println(DateUtil.compTime(date2,date1));*/
    	/*Date strToDate = DateUtil.strToDate("2019-07-22 07:57:29", null);
    	Date addYear = DateUtil.addYear(strToDate, 1);
    	String formatFull = DateUtil.formatFull(addYear);
    	System.out.println(formatFull);*/
    	
//    	System.out.println(DateUtil.getIntervalDays_currt(strToDate, new Date()));
    	/*System.out.println(DateUtil.differDays(new Date(),strToDate));
    	Double differDays = DateUtil.differDays(new Date(),strToDate);
    	System.out.println("Integer: "+differDays.intValue());
    	Integer converCycle = 2;
    	if(differDays.compareTo(converCycle.doubleValue()) <= 0) {
    		System.out.println("true");
    	}else {
    		System.out.println("false");
    	}*/
    	
    	/*Date newDate = addDay(new Date(), 30);
    	String dateStr = formatFull(newDate);
    	System.out.println(dateStr);*/
    	
	}

	
}
/**
 * 海风在线学习平台
 * @Title: DateUtils.java 
 * @Package: com.hyphen.util
 * @author: cloud
 * @date: 2014年5月28日-下午4:18:12
 * @version: V1.0
 * @copyright: 2014上海风创信息咨询有限公司-版权所有
 * 
 */
package com.hfjy.framework.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.hfjy.framework.common.entity.DateType;

/**
 * @ClassName: DateUtils
 * @Description: TODO(日期帮助工具类)
 * @author cloud
 * @date 2014年5月28日 下午4:18:12
 * 
 */
public class DateUtils {

	public static final long DAY_MILLIS = 24 * 60 * 60 * 1000;

	public static final String[] WEEK_TEXT = { "(周一)", "(周二)", "(周三)", "(周四)", "(周五)", "(周六)", "(周日)" };

	public static final String[] WEEK_DAYS = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };

	/**
	 * @Title: getWeeks(获取当前一周的时间 月/日)
	 * @return Date[]
	 * @throws ParseException
	 * @exception
	 * @since 1.0
	 */
	public static Date[] getWeeks(int next) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		Date day = stringToDate(formatDate(calendar.getTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
		calendar.setTime(day);
		long millis = calendar.getTimeInMillis() + next * 7 * DAY_MILLIS;
		Date[] weeks = new Date[7];// 返回的这周的日期
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		week = week == 1 ? 8 : week;

		for (int i = 2; i <= 8; i++) {
			long timeMillis = (week >= i) ? millis - (week - i) * DAY_MILLIS : millis + (i - week) * DAY_MILLIS;
			weeks[i - 2] = new Date(timeMillis);
		}
		return weeks;
	}

	/**
	 * @Title: getWeeks(获取当前一周的时间 月/日)
	 * @return Date[]
	 * @exception
	 * @since 1.0
	 */
	public static Date[] getWeeks(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		long millis = calendar.getTimeInMillis();
		Date[] weeks = new Date[7];// 返回的这周的日期
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		week = week == 1 ? 8 : week;

		for (int i = 2; i <= 8; i++) {
			long timeMillis = (week >= i) ? millis - (week - i) * DAY_MILLIS : millis + (i - week) * DAY_MILLIS;
			weeks[i - 2] = new Date(timeMillis);
		}
		return weeks;
	}

	/**
	 * @Title: formatDate(格式化日期)
	 * @param date
	 *            日期
	 * @param format
	 *            日期格式
	 * @return String 格式化后的字符串
	 * @exception
	 * @since 1.0
	 */
	public static String formatDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * @Title: StringToDate(字符串转日期)
	 * @param dateText
	 *            日期字符串
	 * @param format
	 *            日期格式
	 * @return date 转换后的日期
	 * @throws ParseException
	 * @exception
	 * @since 1.0
	 */
	public static Date stringToDate(String dateText, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(dateText);
	}

	/**
	 * 获取当前日期是星期几<br>
	 * 
	 * @param dt
	 * @return 当前日期是星期几
	 */
	public static int getWeekOfDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK);
		return (w == 1) ? 7 : w - 1;
	}

	/**
	 * 
	 * @Title: getDayTime(获取当前日期，不带时分秒 00:00:00)
	 * @return Date
	 * @exception
	 * @since 1.0
	 */
	public static Date getDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 
	 * @Title: getDayTime(获取时间戳，不带时分秒 00:00:00)
	 * @return long
	 * @exception
	 * @since 1.0
	 */
	public static long getDayTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 
	 * @Title: getNextWeekDay(获取N周后的这一天日期)
	 * @param day
	 *            当前日期
	 * @param next
	 *            第几周后
	 * @return Date
	 * @exception
	 * @since 1.0
	 */
	public static Date getNextWeekDay(Date day, int next) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(day.getTime() + next * 7 * DAY_MILLIS);
		return calendar.getTime();
	}

	/**
	 * 返回系统当前日期
	 * 
	 * @return 以yyyyMMdd为格式的当前系统时间
	 */
	public static String getDayText() {
		Calendar cal = Calendar.getInstance();
		StringBuffer text = new StringBuffer();
		text.append(cal.get(Calendar.YEAR));
		text.append(cal.get(Calendar.MONTH) + 1);
		if (text.length() == 5) {
			text.insert(4, "0");
		}
		text.append(cal.get(Calendar.DATE));
		if (text.length() == 7) {
			text.insert(6, "0");
		}
		return text.toString();
	}

	/**
	 * 返回系统当前年月
	 * 
	 * @return
	 */
	public static String toMonthText() {
		Calendar cal = Calendar.getInstance();
		StringBuffer text = new StringBuffer();
		text.append(cal.get(Calendar.YEAR));
		text.append(cal.get(Calendar.MONTH) + 1);
		if (text.length() == 5) {
			text.insert(4, "0");
		}
		return text.toString();
	}

	/**
	 * 
	 * @Title: exeHoursTime(计算两个时间间隔多少分钟)
	 * @exception
	 * @since 1.0
	 */
	public static String exeHoursTime(Date end, Date start) {
		long t = (end.getTime() - start.getTime()) / (1000 * 60);
		long h = t / 60;
		long m = t % 60;
		return (h > 0 ? h + "小时" : "") + (m > 0 ? m + "分" : "");
	}

	public static String getOrderNum() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(date);
	}

	/**
	 * 计算下一个时间
	 */
	public static Date nextDate(Date date, DateType value, int no) {
		Calendar c = Calendar.getInstance();
		String[] tmp = formatDate(date, "yyyy-MM-dd-HH-mm-ss").split("-");
		c.set(Calendar.YEAR, Integer.valueOf(tmp[0]));
		c.set(Calendar.MARCH, Integer.valueOf(tmp[1]) - 1);
		c.set(Calendar.DATE, Integer.valueOf(tmp[2]));
		c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tmp[3]));
		c.set(Calendar.MINUTE, Integer.valueOf(tmp[4]));
		c.set(Calendar.SECOND, Integer.valueOf(tmp[5]));
		switch (value) {
		case YEAR:
			c.add(Calendar.YEAR, no);
			break;
		case MARCH:
			c.add(Calendar.MARCH, no);
			break;
		case DAY:
			c.add(Calendar.DATE, no);
			break;
		case HOUR:
			c.add(Calendar.HOUR_OF_DAY, no);
			break;
		case MINUTE:
			c.add(Calendar.MINUTE, no);
			break;
		case SECOND:
			c.add(Calendar.SECOND, no);
			break;
		case WEEK:
			c.add(Calendar.DATE, no * 7);
			break;
		}
		return c.getTime();
	}

	public static String getLocalDateString() {
		Calendar calendar = Calendar.getInstance();
		String str = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String str2 = sdf.format(new Date()).toString();
		return str;
	}

	/**
	 * 根据 年月 获取上个月份的天数
	 * 
	 * @param dyear
	 *            年份 例如：2014
	 * @param dmouth
	 *            月份 例如：12
	 * @param num
	 *            例如 -n：表示向前推n个月 1：表示向后推一个月
	 * @return
	 */
	public static int calDayByYearAndMonth(String dyear, String dmouth, int num) {
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
		Calendar rightNow = Calendar.getInstance();
		try {
			rightNow.setTime(simpleDate.parse(dyear + "/" + dmouth));
			rightNow.add(Calendar.MONTH, num);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}

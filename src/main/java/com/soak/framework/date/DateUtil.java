package com.soak.framework.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soak.framework.constant.Week;


public class DateUtil {

  private final static Logger logger = LoggerFactory.getLogger(DateUtil.class);
  

  // 获得系统当前日期
  public static Date getCurrentDateTime() {
    Calendar cal = Calendar.getInstance();
    return cal.getTime();
  }

  // 获得系统当前日期
  public static Date getCurrentShortDate() {
    return parseShortDate(getCurrentShortDateString()) ;
  }

  // 获得系统当前日期
  public static String getCurrentShortDateString() {
    Calendar cal = Calendar.getInstance();
    return formatShortDate(cal.getTime());
  }
  
  // 获得系统当前时间
  public static String currentDateTimeToString() {
    Calendar cal = Calendar.getInstance();
    return formatDate(cal.getTime(), DateStyle.DATETIMEFORMAT);
  }

  /**
   * 功能：得到当月有多少天。
   * 
   * @return int
   */
  public int totalDaysOfMonth() {
    Calendar cal = Calendar.getInstance();
    return cal.getActualMaximum(Calendar.DATE);
  }

  /**
   * 获取时间差 单位(秒)
   * 
   * @param startDate
   * @param amount 单位
   * @return
   */
  public static Date addTime(Date startTime, long amount) {
    Calendar calendar = Calendar.getInstance();
    Date date = calendar.getTime();
    amount = startTime.getTime() + amount*1000 ;
    date.setTime(amount);
    return date;
  }

  /**
   * 获取日期中的某数值。如获取月份
   * 
   * @param date
   *          日期
   * @param dateType
   *          日期格式
   * @return 数值
   */
  private static int getInteger(Date date, int dateType) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(dateType);
  }


  /**
   * 获取精确的日期
   * 
   * @param timestamps
   *          时间long集合
   * @return 日期
   */
  private static Date getAccurateDate(List<Long> timestamps) {
    Date date = null;
    long timestamp = 0;
    Map<Long, long[]> map = new HashMap<Long, long[]>();
    List<Long> absoluteValues = new ArrayList<Long>();

    if (timestamps != null && timestamps.size() > 0) {
      if (timestamps.size() > 1) {
        for (int i = 0; i < timestamps.size(); i++) {
          for (int j = i + 1; j < timestamps.size(); j++) {
            long absoluteValue = Math.abs(timestamps.get(i) - timestamps.get(j));
            absoluteValues.add(absoluteValue);
            long[] timestampTmp = { timestamps.get(i), timestamps.get(j) };
            map.put(absoluteValue, timestampTmp);
          }
        }

        // 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的
        long minAbsoluteValue = -1;
        if (!absoluteValues.isEmpty()) {
          // 如果timestamps的size为2，这是差值只有一个，因此要给默认值
          minAbsoluteValue = absoluteValues.get(0);
        }
        for (int i = 0; i < absoluteValues.size(); i++) {
          for (int j = i + 1; j < absoluteValues.size(); j++) {
            if (absoluteValues.get(i) > absoluteValues.get(j)) {
              minAbsoluteValue = absoluteValues.get(j);
            } else {
              minAbsoluteValue = absoluteValues.get(i);
            }
          }
        }

        if (minAbsoluteValue != -1) {
          long[] timestampsLastTmp = map.get(minAbsoluteValue);
          if (absoluteValues.size() > 1) {
            timestamp = Math.max(timestampsLastTmp[0], timestampsLastTmp[1]);
          } else if (absoluteValues.size() == 1) {
            // 当timestamps的size为2，需要与当前时间作为参照
            long dateOne = timestampsLastTmp[0];
            long dateTwo = timestampsLastTmp[1];
            if ((Math.abs(dateOne - dateTwo)) < 100000000000L) {
              timestamp = Math.max(timestampsLastTmp[0], timestampsLastTmp[1]);
            } else {
              long now = new Date().getTime();
              if (Math.abs(dateOne - now) <= Math.abs(dateTwo - now)) {
                timestamp = dateOne;
              } else {
                timestamp = dateTwo;
              }
            }
          }
        }
      } else {
        timestamp = timestamps.get(0);
      }
    }

    if (timestamp != 0) {
      date = new Date(timestamp);
    }
    return date;
  }

  /**
   * 判断字符串是否为日期字符串
   * 
   * @param date
   *          日期字符串
   * @return true or false
   */
  public static boolean isDate(String date) {
    boolean isDate = false;
    if (date != null) {
      if (parseDateString(date, null) != null) {
        isDate = true;
      }
    }
    return isDate;
  }

  /**
   * 获取日期字符串的日期风格。失敗返回null。
   * 
   * @param date
   *          日期字符串
   * @return 日期风格
   */
  public static DateStyle getDateStyle(String date) {
    DateStyle dateStyle = null;
    Map<Long, DateStyle> map = new HashMap<Long, DateStyle>();
    List<Long> timestamps = new ArrayList<Long>();
    for (DateStyle style : DateStyle.values()) {
      Date dateTmp = parseDateString(date, style);
      if (dateTmp != null) {
        timestamps.add(dateTmp.getTime());
        map.put(dateTmp.getTime(), style);
      }
    }
    dateStyle = map.get(getAccurateDate(timestamps).getTime());
    return dateStyle;
  }

  /**
   * 将yyyy-MM-dd HH:mm:ss字符串转换成日期(net.maxt.util.Date)<br/>
   * 
   * @param dateStr
   *          yyyy-MM-dd HH:mm:ss字符串
   * @return net.maxt.util.Date 日期 ,转换异常时返回null。
   */
  public static Date parseDateTime(String dateStr) {
    return parseDateString(dateStr, DateStyle.DATETIMEFORMAT);
  }

  /**
   * @return返回短日期格式 yyyy-MM-dd 将日期转换成短日期字符串,例如：2009-09-09。
   */
  public static Date parseShortDate(String dateStr) {
    return parseDateString(dateStr, DateStyle.SHORTDATEFORMAT);
  }

  /**
   * @return返回短时间格式 HH:mm:ss
   */
  public static Date parseShortTime(String dateStr) {
    return parseDateString(dateStr, DateStyle.TIMEFORMAT);
  }

  /**
   * 将日期字符串转化为日期。失败返回null。
   * 
   * @param date
   *          日期字符串
   * @param dateStyle
   *          日期风格
   * @return 日期
   */
  public static Date parseDateString(String date, DateStyle dateStyle) {
    if (date == null || date.trim().equals("")) {
      return null;
    }
    Date myDate = null;
    if (dateStyle == null) {
      List<Long> timestamps = new ArrayList<Long>();
      for (DateStyle style : DateStyle.values()) {
        Date dateTmp = parseDateString(date, style);
        if (dateTmp != null) {
          timestamps.add(dateTmp.getTime());
        }
      }
      myDate = getAccurateDate(timestamps);
    } else {
      DateFormat sdf = new SimpleDateFormat(dateStyle.getValue());
      try {
        // ParsePosition pos = new ParsePosition(8);
        // Date currentTime_2 = formatter.parse(dateString, pos);
        myDate = sdf.parse(date.trim());
      } catch (ParseException e) {
        logger.error("Parse date String Error  : " + date);
        e.printStackTrace();
      }
    }
    return myDate;
  }

  /**
   * 将日期转化为日期字符串。失败返回null。
   * 
   * @param date
   *          日期
   * @param parttern
   *          日期格式
   * @return 日期字符串
   */
  public static String formatDate(Date date, String parttern) {
    DateFormat df = new SimpleDateFormat(parttern);
    return (null == date) ? null : df.format(date);
  }

  /**
   * 将日期转化为日期字符串。失败返回null。
   * 
   * @param date
   *          日期
   * @param dateStyle
   *          日期风格
   * @return 日期字符串
   */
  public static String formatDate(Date date, DateStyle dateStyle) {
    String parttern = null;
    if (dateStyle != null) {
      parttern = dateStyle.getValue();
    }
    DateFormat df = new SimpleDateFormat(parttern);
    return (null == date) ? null : df.format(date);
  }
  
  /**
   * 将日期转化为日期字符串。失败返回null。
   * 
   * @param date
   *          日期
   * @param dateStyle
   *          日期风格
   * @return 日期字符串
   */
  public static String formatShortDate(Date date) {
    return formatDate(date, DateStyle.SHORTDATEFORMAT);
  }
  


  /**
   * 将日期字符串转化为另一日期字符串。失败返回null。
   * 
   * @param date
   *          旧日期字符串
   * @param parttern
   *          新日期格式
   * @return 新日期字符串
   */
  public static String StringToString(String date, String parttern) {
    return StringToString(date, null, parttern);
  }

  /**
   * 将日期字符串转化为另一日期字符串。失败返回null。
   * 
   * @param date
   *          旧日期字符串
   * @param dateStyle
   *          新日期风格
   * @return 新日期字符串
   */
  public static String StringToString(String date, DateStyle dateStyle) {
    return StringToString(date, null, dateStyle);
  }

  /**
   * 将日期字符串转化为另一日期字符串。失败返回null。
   * 
   * @param date
   *          旧日期字符串
   * @param olddParttern
   *          旧日期格式
   * @param newParttern
   *          新日期格式
   * @return 新日期字符串
   */
  public static String StringToString(String date, String olddParttern, String newParttern) {
    String dateString = null;
    if (olddParttern == null) {
      DateStyle style = getDateStyle(date);
      if (style != null) {
        Date myDate = parseDateString(date, style);
        dateString = formatDate(myDate, newParttern);
      }
      // } else {
      // Date myDate = parseDateString(date, olddParttern);
      // dateString = dateToString(myDate, newParttern);
    }
    return dateString;
  }

  /**
   * 将日期字符串转化为另一日期字符串。失败返回null。
   * 
   * @param date
   *          旧日期字符串
   * @param olddDteStyle
   *          旧日期风格
   * @param newDateStyle
   *          新日期风格
   * @return 新日期字符串
   */
  public static String StringToString(String date, DateStyle olddDteStyle, DateStyle newDateStyle) {
    String dateString = null;
    if (olddDteStyle == null) {
      DateStyle style = getDateStyle(date);
      dateString = StringToString(date, style.getValue(), newDateStyle.getValue());
    } else {
      dateString = StringToString(date, olddDteStyle.getValue(), newDateStyle.getValue());
    }
    return dateString;
  }


  /**
   * 功能：当前时间增加年数。注意遇到2月29日情况，系统会自动延后或者减少一天。
   * 
   * @param years
   *          正值时时间延后，负值时时间提前。
   * @return Date
   */
  public static Date addYears(Date date, int interval) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.YEAR, c.get(Calendar.YEAR) + interval);
    return c.getTime();
  }

  /**
   * 功能：当前时间增加月数。
   * 
   * @param months
   *          正值时时间延后，负值时时间提前。
   * @return Date
   */
  public static Date addMonths(Date date, int interval) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.MONTH, c.get(Calendar.MONTH) + interval);
    return c.getTime();
  }

  /**
   * 功能：当前时间增加天数。
   * 
   * @param days
   *          正值时时间延后，负值时时间提前。
   * @return Date
   */
  public static Date addDays(Date date, int interval) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.DATE, c.get(Calendar.DATE) + interval);
    return c.getTime();
  }

  /**
   * 功能：当前时间增加小时数。
   * 
   * @param hours
   *          正值时时间延后，负值时时间提前。
   * @return Date
   */
  public static Date addHours(Date date, int interval) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.HOUR, c.get(Calendar.HOUR) + interval);
    return c.getTime();
  }
  
  /**
   * 增加日期的分钟。失败返回null。
   * 
   * @param date
   *          日期
   * @param dayAmount
   *          增加数量。可为负数
   * @return 增加分钟后的日期
   */
  public static Date addMinutes(Date date, int interval) {
    Date myDate = null;
    if (date != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.add(Calendar.MINUTE, interval);
      myDate = calendar.getTime();
    }
    return myDate;
  }
  

  /**
   * 增加日期的秒钟。
   * 
   * @param date
   *          日期字符串
   * @param dayAmount
   *          增加数量。可为负数
   * @return 增加秒钟后的日期字符串
   */
  public static Date addSeconds(Date date, int interval) {
    Calendar c = Calendar.getInstance();
    if (date != null) {
      c.setTime(date);
    }
    c.set(Calendar.SECOND, c.get(Calendar.SECOND) + interval);
    return c.getTime();
  }


  /**
   * 获取日期的年份。失败返回0。
   * 
   * @param date
   *          日期
   * @return 年份
   */
  public static int getYear(Date date) {
    return getInteger(date, Calendar.YEAR);
  }

  /**
   * 获取日期的月份。失败返回0。
   * 
   * @param date
   *          日期
   * @return 月份
   */
  public static int getMonth(Date date) {
    return getInteger(date, Calendar.MONTH);
  }

  /**
   * 获取日期的天数。失败返回0。
   * 
   * @param date
   *          日期
   * @return 天
   */
  public static int getDay(Date date) {
    return getInteger(date, Calendar.DATE);
  }

  /**
   * 获取日期的小时。失败返回0。
   * 
   * @param date
   *          日期
   * @return 小时
   */
  public static int getHour(Date date) {
    return getInteger(date, Calendar.HOUR_OF_DAY);
  }

  /**
   * 获取日期的分钟。失败返回0。
   * 
   * @param date
   *          日期
   * @return 分钟
   */
  public static int getMinute(Date date) {
    return getInteger(date, Calendar.MINUTE);
  }

  /**
   * 获取日期的秒钟。失败返回0。
   * 
   * @param date
   *          日期
   * @return 秒钟
   */
  public static int getSecond(Date date) {
    return getInteger(date, Calendar.SECOND);
  }

  /**
   * 获取日期 。默认yyyy-MM-dd格式。失败返回null。
   * 
   * @param date
   *          日期字符串
   * @return 日期
   */
  public static Date getShortDate(String date) {
    DateFormat sdf = new SimpleDateFormat(DateStyle.SHORTDATEFORMAT.getValue());
    return parseDateString(sdf.format(date), DateStyle.SHORTDATEFORMAT);
  }

  /**
   * 获取日期。默认yyyy-MM-dd格式。失败返回null。
   * 
   * @param date
   *          日期
   * @return 日期
   */
  public static String getShortDate(Date date) {
    DateFormat sdf = new SimpleDateFormat(DateStyle.SHORTDATEFORMAT.getValue());
    return sdf.format(date);
  }

  /**
   * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
   * 
   * @param date
   *          日期字符串
   * @return 时间
   */
  public static String getTime(String date) {
    return StringToString(date, DateStyle.TIMEFORMAT);
  }

  /**
   * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
   * 
   * @param date
   *          日期
   * @return 时间
   */
  public static String getTime(Date date) {
    return formatDate(date, DateStyle.TIMEFORMAT);
  }

  /**
   * 获取日期的星期。失败返回null。
   * 
   * @param date
   *          日期字符串
   * @return 星期
   */
  public static Week getWeek(String date) {
    Week week = null;
    DateStyle dateStyle = getDateStyle(date);
    if (dateStyle != null) {
      Date myDate = parseDateString(date, dateStyle);
      week = getWeek(myDate);
    }
    return week;
  }

  /**
   * 获取日期的星期。失败返回null。
   * 
   * @param date
   *          日期
   * @return 星期
   */
  public static Week getWeek(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return Week.getWeek(calendar.get(Calendar.DAY_OF_WEEK));
  }

  /**
   * 功能：将当前日期的分钟进行重新设置。
   * 
   * @param timeFormat
   *          时间字符串
   * @return 设置后的日期
   */
  public static Date setTime(String dateStr, String timeFormat) {
    Calendar c = Calendar.getInstance();
    c.setTime(parseShortDate(dateStr));
    String[] timestr = timeFormat.split(":");
    c.set(Calendar.HOUR_OF_DAY,  timestr.length >= 1 ? Integer.valueOf(timestr[0]) : 0);
    c.set(Calendar.MINUTE, timestr.length >= 2 ? Integer.valueOf(timestr[1]): 0);
    c.set(Calendar.SECOND, timestr.length >= 3 ? Integer.valueOf(timestr[2]): 0);
    return c.getTime();
  }

  /**
   * 功能：将当前日期的分钟进行重新设置。
   * 
   * @param timeFormat
   *          时间字符串
   * @return 设置后的日期
   */
  public static Date setTime(Date date, String timeFormat) {
    return setTime(formatDate(date, DateStyle.SHORTDATEFORMAT), timeFormat);
  }

  /**
   * 功能：计算两个时间的时间差。
   * 
   * @param startDate
   * @param endDate
   * @return  Timespan 时间间隔  单位(秒)
   */
  public static float timeDiff(Date startTime, Date endTime) {
    long diff = endTime.getTime() - startTime.getTime();
    return diff / 1000;
  }
  
  /**
   * 功能：比较两个时间
   * 
   * @param startDate
   * @param endDate
   * @return  Timespan 时间间隔  单位(秒)
   */
  public static boolean isBefore(Date sameTime, Date baseTime) {
    long diff = sameTime.getTime() - baseTime.getTime() ;
    return diff < 0 ? true : false ;
  }
  
  /**
   * 功能：比较两个时间
   * 
   * @param startDate
   * @param endDate
   * @return  Timespan 时间间隔  单位(秒)
   */
  public static boolean isAfter(Date sameTime, Date baseTime) {
    long diff = sameTime.getTime() - baseTime.getTime() ;
    return diff > 0 ? true : false ;
  }
  
  /**
   * 功能：是不是在两个时间之间
   * 
   * @param startDate
   * @param endDate
   * @return  Timespan 时间间隔  单位(秒)
   */
  public static boolean isBetween(Date sameTime, Date startTime , Date endTime ) {
    long total = Math.abs(startTime.getTime() - endTime.getTime()) ;
    long st = Math.abs(sameTime.getTime() - startTime.getTime());
    long et = Math.abs(sameTime.getTime() - endTime.getTime()) ;    
    return (st + et == total) ? true : false ;
  }

  /**
   * 功能：判断日期是否和当前date对象在同一天。
   * 
   * @param date
   *          比较的日期
   * @return boolean 如果在返回true，否则返回false。
   */
  public static boolean isSameDay(Date date1, Date date2) {
    if (date1 == null || date2 == null) {
      // throw new IllegalArgumentException("日期不能为null");
      return false;
    }
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date1);
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date2);
    return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2
        .get(Calendar.DAY_OF_YEAR));
  }

  /**
   * 是否 统一时间
   * @param date1
   * @param date2
   * @return
   */
  public static boolean isSameDateTime(Date date1, Date date2) {
    if(date1 == null && date2 == null ){
      return true ;
    } else if(date1 == null || date2 == null){
      return false ;
    }
    return date1.getTime() == date2.getTime();
  }

  /**
   * 
   * 取最近、且不超过指定时间    的时间
   * Get closest to the previous time
   * @param date   time
   * @return
   */
  public static Date getClosestPreviousTime(Date referenceTime , List<Date> dates) {
    Date prior = null;
    for (Date date : dates) {
      float diff = DateUtil.timeDiff(referenceTime , date);
      if (diff <= 0 ) {
        if(prior == null || prior.before(date) ){
          prior = date;
        }
      }
    }
    return prior;
  }

  /**
   * 
   * 取最近、且超过指定时间    的时间
   * Get closest to the previous time
   * @param date   time
   * @return
   */
  public static Date getClosestBehindTime(Date referenceTime , List<Date> dates) {
    Date behind = null;
    for (Date date : dates) {
      float diff = DateUtil.timeDiff(referenceTime , date);
      if (diff >= 0 ) {
        if(behind == null || behind.after(date) ){
          behind = date;
        }
      }
    }
    return behind;
  }
  
  
  /**
   * 取最接近的的时间
   * 
   * @param date
   * @return
   */
  public static Date getApproximatelyTime(Date referenceTime , List<Date> dates) {
    Date later = null;
    Float interval = null ;
    for (Date date : dates) {
      float diff = DateUtil.timeDiff(referenceTime , date);
      // 获取时间最近的
      if(interval == null || (Math.abs(interval) > Math.abs(diff)) ){
        interval = diff ;
        later = date ;
      }
      
    }
    return later;
  }
  
  
  /**
   * 获取范围内的时间
   * 
   * @param date
   * @return
   */
  public static List<Date> rangeFilter(List<Date> dates, Date startDate, Date endDate) {
    List<Date> rs = new ArrayList<Date>();
    for (Date date : dates) {
      if (date.getTime() >= startDate.getTime() && date.getTime() <= endDate.getTime()) {
        rs.add(date);
      }
    }
    Collections.sort(rs);
    return rs;
  }


  /**
   * 获取指定日期的月天数
   * 
   * @param rq
   * @return
   * @throws ParseException
   */
  public static int getDaysOfMonth(String rq) {
    return getIntervalDays(getFirstDayOfMonth(rq), getLastDayOfMonth(rq));
  }

  /**
   * 获取两个日期相差的天数
   * 
   * @param date
   *          日期字符串
   * @param otherDate
   *          另一个日期字符串
   * @return 相差天数
   */
  public static int getIntervalDays(String date, String otherDate) {
    return getIntervalDays(parseDateString(date, DateStyle.SHORTDATEFORMAT), parseDateString(otherDate, DateStyle.SHORTDATEFORMAT));
  }

  /**
   * 获取两个日期相差的天数
   * 
   * @param date
   *          日期
   * @param otherDate
   *          另一个日期
   * @return 相差天数
   */
  public static int getIntervalDays(Date date, Date otherDate) {
    date = DateUtil.parseDateString(DateUtil.getShortDate(date), DateStyle.SHORTDATEFORMAT);
    long time = Math.abs(date.getTime() - otherDate.getTime());
    return (int) time / (24 * 60 * 60 * 1000);
  }

  /**
   * 
   * 取得某个月的第一天
   */
  public static Date getFirstDayOfMonth(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    return cal.getTime();
  }

  /**
   * 获取选择日期所在月份第一天， 如：2012-01-15 => 2012-01-01
   * 
   * @param rq
   * @return
   */
  public static String getFirstDayOfMonth(String shortDateStr) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(parseShortDate(shortDateStr));
    cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    return formatDate(cal.getTime(), DateStyle.SHORTDATEFORMAT);
  }

  /**
   * 
   * 取得某个月的最后一天
   */
  public static Date getLastDayOfMonth(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND,0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    return cal.getTime();
  }

  /**
   * 
   * 取得某个月的最后一天
   */
  public static String getLastDayOfMonth(String shortDateStr) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(parseShortDate(shortDateStr));
    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    return formatDate(cal.getTime(), DateStyle.SHORTDATEFORMAT);
  }

  /**
   * 获取指定月份所在季度的第一天
   * 
   * @param rq
   * @return
   * @throws ParseException
   */
  public static String getFirstDayOfSeason(String rq) {
    String year = rq.substring(0, 4);
    int month = Integer.parseInt(rq.substring(5, 7));
    int[][] array = {{ 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { 10, 11, 12 } };
    int season = 1;
    if (month >= 1 && month <= 3) {
      season = 1;
    }
    if (month >= 4 && month <= 6) {
      season = 2;
    }
    if (month >= 7 && month <= 9) {
      season = 3;
    }
    if (month >= 10 && month <= 12) {
      season = 4;
    }
    int firstMonth = array[season - 1][0];
    String seasonDate = getFirstDayOfMonth(year + "-" + firstMonth + "-1");
    return seasonDate;
  }
  

  /**
   * 获取当年第一天
   * 
   * @param rq
   * @return
   */
  public static String getFirstDayOfYear(String rq) {
    return rq.substring(0, 4) + "-01-01";
  }
  
  /**
   * 获取选择日期所在年份的最后一天 如：2012-01-15=>2012-12-31
   * 
   * @param rq
   * @return
   */
  public static String getLastDayOfYear(String rq) {
    return rq.substring(0, 4) + "-12-31";
  }
  

  /**
   * 返回 期之间所有日期
   * 
   * @param startDate
   * @param endDate
   * @return
   */
  public static List<Date> getEachDateIn(Date startDate , Date endDate) {
    List<Date> dates = new ArrayList<Date>();
    // 对两个日期之间所有日期的遍历
    Long startTime = parseShortDate(formatShortDate(startDate)).getTime();
    Long endTime = parseShortDate(formatShortDate(endDate)).getTime();
    Long oneDay = 1000 * 60 * 60 * 24l;
    while (startTime <= endTime) {
      Date eachDate = new Date(startTime);
      startTime += oneDay;
      dates.add(eachDate);
    }
    return dates ;
  }

  

}
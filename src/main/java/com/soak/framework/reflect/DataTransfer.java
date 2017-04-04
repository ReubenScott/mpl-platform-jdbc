package com.soak.framework.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 数据类型转化类
 * */
final public class DataTransfer {
	private DataTransfer(){};
	private static Map<Class,Class> m;
	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static {
		m=new HashMap<Class,Class>();
		m.put(Integer.TYPE, Integer.class);
		m.put(Long.TYPE, Long.class);
		m.put(Double.TYPE, Double.class);
		m.put(Boolean.TYPE, Boolean.class);
		//m.put(Integer.TYPE, Integer.class);
	}
	
	/**
	 * 转化一个对象类型数据为指定 的Class类型<br>
	 * 如果不支持该转化，则返回原本的值
	 * */
	public static Object parse(Object value,Class cls){
		
		Method m=getFitMethod(value.getClass(), cls);
		if (m!=null){
			try {
				return m.invoke(null, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//return value;
		throw new RuntimeException("Not supported parse.From <class:<"+value.getClass().getName()+"> value:<"+value+"> to Class <"+cls.getName()+">");
	}
	/**
	 * 提供一个BigDecimal到Long类型的转化
	 * */
	private static Long parseToLong(BigDecimal bg){
		return bg.longValue();
	}
	/**
	 * 提供一个BigDecimal到Integer类型的转化
	 * */
	private static Integer parseToInteger(BigDecimal bg){
		return bg.intValue();
	}
	
	/**
	 * 提供一个BigDecimal到Integer类型的转化
	 * */
	private static java.util.Date parseToDate(java.sql.Date date){
		return new Date(date.getTime());
	}
	
	
	private static BigDecimal parseToBigDecimal (Integer i){
		return new BigDecimal(i);
	}
	private static BigDecimal parseToBigDecimal (Double i){
		return new BigDecimal(i);
	}
	private static Date parseToDate(Long time){
		return new Date(time);
	}
	private static Short parseToShort(Integer intValue){
		return intValue.shortValue();
	}
	private static Integer parseToInteger(String s){
		return Integer.valueOf(s);
	}
	
	private static String parseToString(BigDecimal mal){
		return mal.toString();
	}
	
	private static Boolean parseToBoolean(String s){
		return Boolean.parseBoolean(s);
	}
	
	/**
	 * 提供一个日期的格式化功能
	 * yyyy-MM-dd
	 * */
	/*public static String parseToString (Date date){
		return format.format(date);
	}*/
	/**
	 * 提供一个日期的格式化功能
	 * */
	/*public static String parseToFullString (Date date){
		return formatTime.format(date);
	}*/
	
	/**
	 * 从可选的转化类型中找到合适的方法
	 * */
	private static Method getFitMethod (Class srcCls,Class tarCls){
		
		//基本类型的处理
		if (tarCls.isPrimitive() && m.containsKey(tarCls)){
			tarCls = m.get(tarCls);
		}
		
		try {
			return DataTransfer.class.getDeclaredMethod("parseTo"+tarCls.getSimpleName()
					, srcCls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

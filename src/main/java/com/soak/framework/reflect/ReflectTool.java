package com.soak.framework.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 常用的反射工具类
 * */
final public class ReflectTool {
	
	private static String GET ="get";
	private static String SET ="set";
	private static String IS ="is";
	private static Log log = LogFactory.getLog(ReflectTool.class);

	private ReflectTool(){}
	
	
	/**
	 * 得到某个类的get方法<br>
	 * 对于boolean类型的isXyz/xyz开头的get方法为isXyz
	 * */
	public static Method getGetterMethod(Class cls,String name){
		StringBuffer methodName = new StringBuffer();
		methodName.append(GET).append(name.toUpperCase().substring(0,1));
		methodName.append(name.substring(1));
		
		try {
			return cls.getMethod(methodName.toString());
		} catch (NoSuchMethodException e) {
			//e.printStackTrace();
			Method m = getBooleanGetter(cls, name);
			if (m==null){
				log.debug("A getter method of class <"+cls.getName()+"> not found:<"+methodName+">");
			}
			return m;
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 得到boolean类型的get方法
	 * */
	private static Method getBooleanGetter(Class cls,String fieldName){
		Field f = getField(cls, fieldName);
		
		if (f==null || f.getType()!=Boolean.TYPE){
			//不存在或者非boolean类型返回null
			return null;
		}
		
		String methodName;
		//规则：前两位小写的is+第三位大写使用自身
		if (fieldName.substring(0, 2).equals(IS) && Character.isUpperCase(fieldName.charAt(2))){
			methodName = fieldName;
		}else{
			//否则：is+首字母大写+其他名
			methodName = IS + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		}
		try {
			return cls.getDeclaredMethod(methodName);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			//e.printStackTrace();
			log.debug("A boolean type getter method of class <"+cls.getName()+"> not found:<"+methodName+">");
		}
		return null;
	}
	
	
	/**
	 * 得到某个类的set方法
	 * 对于boolean类型的isXyz/xyz开头的set方法为setXyz
	 * */
	public static Method getSetterMethod(Class cls,String name){
		StringBuffer methodName = new StringBuffer();
		methodName.append(SET).append(name.toUpperCase().substring(0,1));
		methodName.append(name.substring(1));
		
		Method[] ms= cls.getMethods();
		
		for (Method m:ms){
			if (m.getName().equals(methodName.toString())){
				return m;
			}
		}
		
		return null;
	}
	/**
	 * 得到set方法，使用set类型参数
	 * */
	public static Method getSetterMethod(Class cls,String name,Class type){
		StringBuffer methodName = new StringBuffer();
		methodName.append(SET).append(name.toUpperCase().substring(0,1));
		methodName.append(name.substring(1));
		
		try {
			return cls.getMethod(methodName.toString(),type);
		} catch (NoSuchMethodException e) {
			//e.printStackTrace();
			Method m = getBooleanSetter(cls, name);
			if (m==null){
				log.debug("a setter method of class <"+cls.getName()+"> not found:<"+methodName+">");
			}
			return m;
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 得到boolean类型的set方法
	 * */
	private static Method getBooleanSetter(Class cls,String fieldName){
		Field f = getField(cls, fieldName);
		
		if (f==null || f.getType()!=Boolean.TYPE){
			//不存在或者非boolean类型返回null
			return null;
		}
		
		String methodName;
		//规则：前两位小写的is+第三位大写使用去掉is的set+其余
		if (fieldName.substring(0, 2).equals(IS) && Character.isUpperCase(fieldName.charAt(2))){
			methodName = SET + fieldName.substring(2);
		}else{
			//否则：set+首字母大写+其他名
			methodName = SET + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		}
		try {
			return cls.getDeclaredMethod(methodName,f.getType());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			//e.printStackTrace();
			log.debug("A boolean type setter method of class <"+cls.getName()+"> not found:<"+methodName+">");
		}
		return null;
	}
	
	/**
	 * 得到类的所有字段(包含继承和私有)
	 * */
	public static List<Field> getAllField(Class cls){
		Class tc = cls;
		List<Field> list = new ArrayList<Field>();
		while (tc != Object.class){
			for (Field f:tc.getDeclaredFields()){
				list.add(f);
			}
			tc = tc.getSuperclass();
		}
		return list;
	}
	/**
	 * 得到类的Field对象(包含继承和私有)
	 * */
	public static Field getField(Class cls,String fieldName){
		Class tc = cls;
		Field temp = null;
		while (tc != Object.class){
			try {
				temp = cls.getDeclaredField(fieldName);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				//e.printStackTrace();
				log.debug("The field <"+fieldName+"> of class <"+cls.getName()+"> has not been declared.Try to access super class.");
			}
			if (temp!=null){
				return temp;
			}
			tc = tc.getSuperclass();
		}
		log.debug("The field <"+fieldName+"> of class <"+cls.getName()+"> is not exist.");
		return temp;
	}
	
	/**
	 * 得到定义在域上的泛型类型
	 * */
	public static Class getGenericType (Field f){
		Type p = f.getGenericType();
		if (p instanceof ParameterizedType){
			ParameterizedType pt = (ParameterizedType)p;
			return (Class) pt.getActualTypeArguments()[0];
		}
		
		return null;
	}
	
	
	public static void main(String[] arsg){
	}
}

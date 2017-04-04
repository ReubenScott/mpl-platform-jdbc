package com.soak.framework.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * Bean注射器~
 * */
public class BeanInjecter<T> {

	private Class<T> cls;
	private Map<String,Method> getterMap;
	private Map<String,Method> setterMap;
	private static String[] exclude = {"serialVersionUID"};
	/**
	 * 自动数据类型转换
	 * */
	private boolean autoDataTransfer = true;
	
	
	private static Map<Class,BeanInjecter> map=new Hashtable<Class, BeanInjecter>();
	
	synchronized public static <T> BeanInjecter<T> getInstance(Class<T> cls){
		if (!map.containsKey(cls)) {
			map.put(cls, new BeanInjecter<T>(cls));
		}
		return map.get(cls);
	}
	
	private BeanInjecter(Class<T> cls){
		this.cls=cls;
		getterMap = new HashMap<String,Method>();
		setterMap = new HashMap<String,Method>();
		init();
	}
	/**
	 * 初始化方法
	 * */
	private void init() {
		Method m;
		for (Field f:ReflectTool.getAllField(cls)){
			
			if (isExclude(f.getName()))
				continue;
			
			m=ReflectTool.getGetterMethod(cls, f.getName());
			if (m!=null){
				getterMap.put(f.getName(), m);
			}
			m=ReflectTool.getSetterMethod(cls, f.getName(), f.getType());
			if (m!=null){
				setterMap.put(f.getName(), m);
			}else{
				m=ReflectTool.getSetterMethod(cls, f.getName());
				if (m!=null){
					setterMap.put(f.getName(), m);
				}
			}
		}
	}

	/**
	 * 是否排除的字段
	 * */
	private boolean isExclude(String name) {
		for (String s:exclude){
			if (s.equals(name))
				return true;
		}
		return false;
	}

	/**
	 * 设值Bean的field上的值
	 * */
	public void setBeanValue(T src, String field, Object value) {
		
		//参数检查
		if (value==null || field==null || src==null)
			return;
		
		try {
			if (setterMap.containsKey(field)) {

				Method setter = setterMap.get(field);

				if (checkMethod(setter, value)) {
					setter.invoke(src, value);
				} else {
					// 参数类型不匹配
					if (autoDataTransfer) {
						setter.invoke(
								src,
								DataTransfer.parse(value,
										setter.getParameterTypes()[0]));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean checkMethod (Method setter,Object value){
		Class mType = setter.getParameterTypes()[0];
		Class vType = value.getClass();
		//类型一致
		if (mType.equals(vType)){
			return true;
		}
		//实现接口关系
		if (mType.isInterface() && Arrays.asList(vType.getInterfaces()).contains(mType)){
			return true;
		}
		//继承关系
		if (checkExtends(mType, vType)){
			return true;
		}
		return false;
	}
	
	/**
	 * 检查声明类型和值类型是否匹配
	 * 即值类型是和声明类型相同或者是子类返回true
	 * */
	private static boolean checkExtends(Class defType,Class valueType){
		Class tc = valueType;
		while (tc!=Object.class){
			if (defType.equals(valueType)){
				return true;
			}
			tc = tc.getSuperclass();
		}
		return false;
	}
	
	/**
	 * 得到Bean的field上的值
	 * */
	public Object getBeanValue(T src,String field){
		try {
			if (getterMap.containsKey(field))
				return getterMap.get(field).invoke(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 转化一个Map为指定对象的实例
	 * */
	public T parseMapToObj(Map<String,Object> map){
		try {
			T t = cls.newInstance();
			
			for (String field:setterMap.keySet()){
				if (map.containsKey(field)){
					setBeanValue(t, field, map.get(field));
				}
			}
			
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 转化一个Map为指定对象的实例
	 * */
	public Map<String,Object> parseObjToMap(T obj){
		try {
			Map<String,Object> map = new HashMap<String, Object>();
			Object value;
			for (String s:getterMap.keySet()){
				value = getBeanValue(obj, s);
				if (value != null){
					map.put(s, getBeanValue(obj, s));
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 转化一个Map为指定对象的实例，使用String值
	 * 不转化空值，使用get,set方法
	 * */
	public Map<String,String> parseObjToStrMap(T obj){
		try {
			Map<String,String> map = new HashMap<String, String>();
			Object value;
			for (String s:getterMap.keySet()){
				value = getBeanValue(obj, s);
				if (value!=null){
					//TODO
					//目前只是直接toString，未来可以提供注解或者接口来更加人性化字符串化
					map.put(s, value.toString());
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

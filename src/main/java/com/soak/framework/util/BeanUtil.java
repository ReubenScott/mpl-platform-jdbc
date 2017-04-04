package com.soak.framework.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 问题:JDK5中不能传递二个可变参数,如:methodInvoke()方法
 * 
 */
public class BeanUtil {

  private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

  /**
   * 查看 属性
   * 
   * @param obj
   */
  public static void debugBean(Object obj) {
    try {
      if (obj == null) {
        logger.debug("obj == null");
        return;
      }
      Class obj_class = obj.getClass();

      if (obj_class.equals(String.class)) {
        logger.debug(obj.toString());
        return;
      }

      Field[] fields = obj_class.getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
        fields[i].setAccessible(true);
        String fieldName = fields[i].getName();
        Class fieldType = fields[i].getType();
        boolean isArray = fieldType.isArray();
        if (!isArray) {
          logger.debug(fieldName + "=[" + fieldType.getName() + ":" + fields[i].get(obj) + "]");
        } else if (fieldType.equals(String.class)) {
          logger.debug(fieldName + "=[" + fieldType.getName() + ":" + fields[i].get(obj) + "]");
        } else {
          Object[] objs = (Object[]) fields[i].get(obj);
          if ((objs != null) && (objs.length > 0)) {
            logger.debug(fieldName + " Array start ....");
            for (int j = 0; j < objs.length; j++) {
              debugBean(objs[j]);
            }
            logger.debug(fieldName + " Array end.");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 复制
   * 
   * @param obj
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public static Object copyBean(Object obj) {
    Class srcClass = obj.getClass();
    Field[] fs = srcClass.getDeclaredFields();
    Object imitation = null;
    try {
      imitation = srcClass.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < fs.length; i++) {
      Field f = fs[i];
      String fname = f.getName();
      if ("serialVersionUID".endsWith(fname)) {
        continue;
      }
      String getMName = "get" + fname.substring(0, 1).toUpperCase() + fname.substring(1);
      String setMName = "set" + fname.substring(0, 1).toUpperCase() + fname.substring(1);
      try {
        Method gm = srcClass.getDeclaredMethod(getMName, null);// �õ�get����
        Object getV = gm.invoke(obj, new Object[] {});
        Method sm = srcClass.getDeclaredMethod(setMName, new Class[] { f.getType() });// �õ�set����
        sm.invoke(imitation, new Object[] { getV });
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
    return imitation;
  }
  

  /**
   * 按字段属性 拷贝
   * @param source
   * @param target
   * @param ignoreProperties
   */
  public static void copyProperties(Object source, Object target, String... ignoreProperties) {
    try {
      BeanUtils.copyProperties(target, source);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * ��source �����ֵ����target ����
   * 
   * @param source
   *          ActionForm����
   * @param target
   *          DataModel����
   * @param ignoreProperties
   *          ���Ե��������������
   */
  // TODO 未完成
  public static void copyProperties1(Object source, Object target, String... ignoreProperties) {
    // TODO
    // java�ײ㷽������
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
      PropertyDescriptor[] targetPds = beanInfo.getPropertyDescriptors(); 
      // �߼��ж�
      List ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
      // ���ID���ȡ�ѭ����ֵ
      for (int i = 0; i < targetPds.length; i++) {
        // id����targetPd
        PropertyDescriptor targetPd = targetPds[i];
        String propertyName = targetPd.getName() ;
        //
        if (targetPd.getWriteMethod() != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
          // TODO
          PropertyDescriptor sourcePd = new PropertyDescriptor(propertyName, source.getClass()); // / BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
          
          // �����ͣ�Ԥ���峬����Χʱ�����伴�ɡ�
          if (sourcePd != null && sourcePd.getReadMethod() != null) {
            try {
              Object value1 = null;
              Object value = sourcePd.getReadMethod().invoke(source, new Object[0]);
              String paramterType = targetPd.getWriteMethod().getParameterTypes()[0].getName();
              if (paramterType.equals("java.lang.Integer")) {
                value1 = new Integer(value.toString());
              } else if (paramterType.equals("java.lang.Float")) {
                value1 = new Float(value.toString());
              } else if (paramterType.equals("java.lang.Long")) {
                value1 = new Long(value.toString());
              } else if (paramterType.equals("java.util.Date")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (value.toString().indexOf(":") != -1) {
                  sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                }
                Date tt2 = sdf.parse((String) value);// java.util.Date
                value1 = tt2;
              } else if (paramterType.equals("java.lang.String")) {
                value1 = value;
              } else {
                throw new Exception("the Paramter type is not registed:" + paramterType);
              }
              targetPd.getWriteMethod().invoke(target, new Object[] { value1 });
            } catch (Exception ex) {
              // throw new FatalBeanException("Could not copy properties from source to target", ex);
            }
          } else {
            /*
             * ��Ŀ�����Model���� Ҫ��Actionform�ж���ʱ�����=model��Ӧ������+"id"��ȫ��Сд
             */
            // ȡԴ����
            // TODO
            // sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName() + "id");
            if (sourcePd != null && sourcePd.getReadMethod() != null) {
              try {
                // ��ȡԴ�����е�ֵ
                Object value = sourcePd.getReadMethod().invoke(source, new Object[0]);
                // ʵ��Model ���󡢵õ�д��������ֵ
                if (value != null) {
                  Object model = targetPd.getReadMethod().getReturnType().newInstance();
                  // TODO
                  // PropertyDescriptor modelPd = BeanUtils.getPropertyDescriptor(targetPd.getReadMethod().getReturnType(), "id");
                  // modelPd.getWriteMethod().invoke(model, new Object[] { new Long((String) value) });
                  targetPd.getWriteMethod().invoke(target, new Object[] { model });
                }
              } catch (Exception ex) {
                // throw new FatalBeanException("Could not copy properties from source to target", ex);
              }
            }
          }
        }
      }
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }       

  }

  /**
   * 自动封装
   * 
   * @param obj
   * @return
   */
  public static <T> T autoPackageBean(Class<T> sample, Map<String, Object> column_value) {
    Field[] fs = sample.getDeclaredFields();
    Object imitation = null;
    try {
      imitation = sample.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    Set<String> ks = column_value.keySet();
    Iterator<String> it = ks.iterator();
    while (it.hasNext()) {
      String column = it.next();
      Object value = column_value.get(column);
      
      // log.debug(" column:[" +column +"] -- value:[" + value+"]");

      for (int i = 0; i < fs.length; i++) {
        Field f = fs[i];
        String fname = f.getName();
        if (!fname.equalsIgnoreCase(column)) {
          continue;
        }
        try {
          if(value == null ){
            continue;
          } else{
            BeanUtils.setProperty(imitation, fname, value);
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e1) {
          e1.printStackTrace();
        }
      }
    }

    return (T)imitation;
  }

  /**
   * 自动拆包
   * 
   * @param obj
   * @return
   */
  public static Map unpackageBean(Object domain) {
    Class sample = domain.getClass();

    Field[] fs = sample.getDeclaredFields();
    Map map = new HashMap();
    for (int i = 0; i < fs.length; i++) {
      Field f = fs[i];
      String fname = f.getName();
      if ("serialVersionUID".equalsIgnoreCase(fname)) {
        continue;
      }
      String getMName = "get" + fname.substring(0, 1).toUpperCase() + fname.substring(1);
      try {
        Method gm = sample.getDeclaredMethod(getMName, null);// 
        Object value = gm.invoke(domain, new Object[] {});
        if (value == null) {
          continue;
        }
        map.put(fname, value);
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }

    return map;
  }

  /**
   * 将对象转换为Map
   * 
   * @param object
   * @return
   */
  public static void setBeanToMap(Map map, Object bean) {
    Class beanClass = bean.getClass();
    Method[] beanMethods = beanClass.getMethods();
    for (int i = 0; i < beanMethods.length; i++) {
      String methodName = beanMethods[i].getName();
      if (methodName.indexOf("get") == 0) {
        methodName = methodName.substring(3, methodName.length());
        methodName = methodName.toLowerCase();
        try {
          map.put(methodName, beanMethods[i].invoke(bean, new Object[] {}));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  
  /**
   * 
   * 集合 转 数组
   * 
   * @param <T>
   * @param list
   * @return
   */
  public static <T> T[] listToArray(Collection<?> list) {
    return (T[])list.toArray(new Object[list.size()]);
  }
  
  ////        ((((((((((((((((((((((((((((((((((((((((

  /**
   * 通过构造函数实例化对象
   * 
   * @param className
   *          类的全路径名称
   * @param parameterTypes
   *          参数类型
   * @param initargs
   *          参数值
   * @return
   */
  public static Object constructorNewInstance(String className, Class[] parameterTypes, Object[] initargs) {
    try {
      // 暴力反射
      Constructor<?> constructor = (Constructor<?>) Class.forName(className).getDeclaredConstructor(parameterTypes);
      constructor.setAccessible(true);
      return constructor.newInstance(initargs);
    } catch (Exception ex) {
      throw new RuntimeException();
    }

  }

  /**
   * 暴力反射获取字段值
   * 
   * @param fieldName
   *          属性名
   * @param obj
   *          实例对象
   * @return 属性值
   */
  public static Object getFieldValue(String propertyName, Object obj) {
    try {
      Field field = obj.getClass().getDeclaredField(propertyName);
      field.setAccessible(true);
      return field.get(obj);
    } catch (Exception ex) {
      throw new RuntimeException();
    }
  }

  /**
   * 暴力反射获取字段值
   * 
   * @param propertyName
   *          属性名
   * @param object
   *          实例对象
   * @return 字段值
   */
  public static Object getProperty(String propertyName, Object object) {
    try {

      PropertyDescriptor pd = new PropertyDescriptor(propertyName, object.getClass());
      Method method = pd.getReadMethod();
      return method.invoke(object);

      // 其它方式
      /*
       * BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
       * PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
       * Object retVal = null;
       * for(PropertyDescriptor pd : pds){
       * if(pd.getName().equals(propertyName))
       * {
       * Method methodGetX = pd.getReadMethod();
       * retVal = methodGetX.invoke(object);
       * break;
       * }
       * }
       * return retVal;
       */
    } catch (Exception ex) {
      throw new RuntimeException();
    }
  }

  /**
   * 通过BeanUtils工具包获取反射获取字段值,注意此值是以字符串形式存在的,它支持属性连缀操作:如,.对象.属性
   * 
   * @param propertyName
   *          属性名
   * @param object
   *          实例对象
   * @return 字段值
   */
  public static Object getBeanInfoProperty(String propertyName, Object object) {
    try {
      return BeanUtils.getProperty(object, propertyName);
    } catch (Exception ex) {
      throw new RuntimeException();
    }
  }

  /**
   * 通过BeanUtils工具包获取反射获取字段值,注意此值是以字符串形式存在的
   * 
   * @param object
   *          实例对象
   * @param propertyName
   *          属性名
   * @param value
   *          字段值
   * @return
   */
  public static void setBeanInfoProperty(Object object, String propertyName, String value) {
    try {
      BeanUtils.setProperty(object, propertyName, value);
    } catch (Exception ex) {
      throw new RuntimeException();
    }
  }

  /**
   * 通过BeanUtils工具包获取反射获取字段值,注意此值是以对象属性的实际类型
   * 
   * @param propertyName
   *          属性名
   * @param object
   *          实例对象
   * @return 字段值
   */
  public static Object getPropertyUtilByName(String propertyName, Object object) {
    try {
      return PropertyUtils.getProperty(object, propertyName);
    } catch (Exception ex) {
      throw new RuntimeException();
    }
  }

  /**
   * 通过BeanUtils工具包获取反射获取字段值,注意此值是以对象属性的实际类型,这是PropertyUtils与BeanUtils的根本区别
   * 
   * @param object
   *          实例对象
   * @param propertyName
   *          属性名
   * @param value
   *          字段值
   * @return
   */
  public static void setPropertyUtilByName(Object object, String propertyName, Object value) {
    try {
      PropertyUtils.setProperty(object, propertyName, value);
    } catch (Exception ex) {
      throw new RuntimeException();
    }
  }

  /**
   * 设置字段值
   * 
   * @param obj
   *          实例对象
   * @param propertyName
   *          属性名
   * @param value
   *          新的字段值
   * @return
   */
  public static void setProperties(Object object, String propertyName, Object value) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
    PropertyDescriptor pd = new PropertyDescriptor(propertyName, object.getClass());
    Method methodSet = pd.getWriteMethod();
    methodSet.invoke(object, value);
  }

  /**
   * 设置字段值
   * 
   * @param propertyName
   *          字段名
   * @param obj
   *          实例对象
   * @param value
   *          新的字段值
   * @return
   */
  public static void setFieldValue(Object obj, String propertyName, Object value) {
    try {
      Field field = obj.getClass().getDeclaredField(propertyName);
      field.setAccessible(true);
      field.set(obj, value);
    } catch (Exception ex) {
      throw new RuntimeException();
    }
  }

  /**
   * 设置字段值
   * 
   * @param className
   *          类的全路径名称
   * @param methodName
   *          调用方法名
   * @param parameterTypes
   *          参数类型
   * @param values
   *          参数值
   * @param object
   *          实例对象
   * @return
   */
  @SuppressWarnings("rawtypes")
  public static Object methodInvoke(String className, String methodName, Class[] parameterTypes, Object[] values, Object object) {
    try {
      Method method = Class.forName(className).getDeclaredMethod(methodName, parameterTypes);
      method.setAccessible(true);
      return method.invoke(object, values);
    } catch (Exception ex) {
      throw new RuntimeException();
    }
  }

  /**
   * @param <T>
   *          具体对象
   * @param fileds
   *          要进行比较Bean对象的属性值集合(以属性值为key,属性注释为value,集合从数据库中取出)
   * @param oldBean
   *          源对象
   * @param newBean
   *          新对象
   * @return 返回二个Bean对象属性值的异同
   */
  public static <T> String compareBeanValue(Map<String, String> fileds, T oldBean, T newBean) {

    StringBuilder compares = new StringBuilder();
    String propertyName = null;
    Object oldPropertyValue = null;
    Object newPropertyValue = null;

    StringBuilder descrips = new StringBuilder();
    for (Map.Entry<String, String> entity : fileds.entrySet()) {
      // 获取新旧二个对象对应的值
      propertyName = entity.getKey().toLowerCase();
      oldPropertyValue = getProperty(propertyName, oldBean);
      newPropertyValue = getProperty(propertyName, newBean);

      if (null == oldPropertyValue && null == newPropertyValue) {
        continue;
      }
      if ("".equals(oldPropertyValue) && "".equals(newPropertyValue)) {
        continue;
      }
      if (null == oldPropertyValue) {
        oldPropertyValue = "";
      }
      if (null == newPropertyValue) {
        newPropertyValue = "";
      }

      if (oldPropertyValue.equals(newPropertyValue)) {
        continue;
      }
      compares.append("字段注释: ").append(entity.getValue()).append("】").append("原属性值\"");
      if (StringUtil.isEmpty(oldPropertyValue + "")) {
        oldPropertyValue = " ";
      }
      compares.append(oldPropertyValue).append("\"现属性值\"");
      if (StringUtil.isEmpty(newPropertyValue + "")) {
        newPropertyValue = " ";
      }
      compares.append(newPropertyValue).append("\";");
    }
    return compares.toString();
  }
}

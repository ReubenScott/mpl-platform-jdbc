package com.soak.common.json;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.soak.framework.date.DateStyle;
import com.soak.framework.date.DateUtil;
import com.soak.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * JSON转换工具类
 * 
 * @author penghuaiyi
 * @date 2014-04-04
 */
public class JsonUtil {

  private static JsonConfig jsonConfig = new JsonConfig();

  static {
    // 将Date转换为指定格式
    JsonValueProcessor jsonValueProcessor = new JsonValueProcessor() {
      public Object processArrayValue(Object paramObject, JsonConfig paramJsonConfig) {
        List<String> obj = new ArrayList<String>();
        for(Object date : (Object[])paramObject){
          obj.add(process(date));
        }
        return obj.toArray(new Object[obj.size()]);
      }

      public Object processObjectValue(String paramString, Object paramObject, JsonConfig paramJsonConfig) {
        return process(paramObject);
      }

      private String process(Object date) {
        if (date instanceof Time) {
          return DateUtil.formatDate((Date) date, DateStyle.TIMEFORMAT);
        }
        if (date instanceof Timestamp) {
          return DateUtil.formatDate((Date) date, DateStyle.DATETIMEFORMAT);
        }
        if (date instanceof Date) {
          return DateUtil.formatDate((Date) date, DateStyle.SHORTDATEFORMAT);
        }
        return date == null ? "" : date.toString();
      }
    };

    jsonConfig.registerJsonValueProcessor(Time.class, jsonValueProcessor);
    jsonConfig.registerJsonValueProcessor(Timestamp.class, jsonValueProcessor);
    jsonConfig.registerJsonValueProcessor(Date.class, jsonValueProcessor);
  }

  /**
   * 对象转换成JSON字符串
   * 
   * @param obj
   *          需要转换的对象
   * @return 对象的string字符
   */
  public static String toJson(Object obj) {
    JSONArray array = JSONArray.fromObject(obj, jsonConfig);
    return array.toString();
  }

  /**
   * 对象转换成JSON字符串
   * 
   * @param obj
   *          需要转换的对象
   * @return 对象的string字符
   */
  public static String toJson(String key, Object obj) {
    if (StringUtil.isEmpty(key)) {

    } else {
      JSONObject json = new JSONObject();
      json.put(key, obj);// JSONObject对象中添加键值对
      obj = json;
    }
    JSONArray array = JSONArray.fromObject(obj, jsonConfig);
    return array.toString();
  }

  /**
   * JSON字符串转换成对象
   * 
   * @param jsonString
   *          需要转换的字符串
   * @param type
   *          需要转换的对象类型
   * @return 对象
   */
  public static <T> T fromJson(String jsonString, Class<T> type) {
    // JSONObject jsonObject = JSONObject.fromObject(jsonString);
    // return (T) JSONObject.toBean(jsonObject, type);

    return null;
  }

  /**
   * 将JSONArray对象转换成list集合
   * 
   * @param jsonArr
   * @return
   */
  // public static List<Object> jsonToList(JSONArray jsonArr) {
  // List<Object> list = new ArrayList<Object>();
  // for(int i = 0 ;i<jsonArr.length() ;i++){
  // Object obj = jsonArr.get(i);
  // if (obj instanceof JSONArray) {
  // list.add(jsonToList((JSONArray) obj));
  // } else if (obj instanceof JSONObject) {
  // list.add(jsonToMap((JSONObject) obj));
  // } else {
  // list.add(obj);
  // }
  // }
  // return list;
  // }

  /**
   * 将json字符串转换成map对象
   * 
   * @param json
   * @return
   */
  // public static Map<String, Object> jsonToMap(String json) {
  // // 转换成为JSONObject对象
  // JSONObject jsonObj = new JSONObject(json);
  // return jsonToMap(jsonObj);
  // }

  /**
   * 将JSONObject转换成map对象
   * 
   * @param json
   * @return
   */
  // public static Map<String, Object> jsonToMap(JSONObject obj) {
  // Set<String> set = obj.keySet();
  // Map<String, Object> map = new HashMap<String, Object>(set.size());
  // for (String key : set) {
  // Object value = obj.get(key);
  // if (value instanceof JSONArray) {
  // map.put(key.toString(), jsonToList((JSONArray) value));
  // } else if (value instanceof JSONObject) {
  // map.put(key.toString(), jsonToMap((JSONObject) value));
  // } else {
  // map.put(key.toString(), obj.get(key));
  // }
  //
  // }
  // return map;
  // }

}

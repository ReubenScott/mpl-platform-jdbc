package com.soak.framework.context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;

public class AppConfigContext {
  
  private volatile static AppConfigContext instance;

  private final Properties properties = new Properties();

  /**
   * 获取配置文件信息
   */
  private AppConfigContext() {
    this.readConfig();
//    this.loadConfig();
  }
  

  // 获取配置文件信息 config.properties 方式一 ：
  private void readConfig() {
    ResourceBundle rb = ResourceBundle.getBundle("config");
    for (Enumeration en = rb.getKeys(); en.hasMoreElements();) {
      String key = (String) en.nextElement();
      properties.put(key, rb.getString(key));
    }
  }

  // 获取配置文件信息 config.properties 方式二 ：
  private void loadConfig() {
    InputStream is = this.getClass().getResourceAsStream("/config.properties");
    try {
      properties.load(is);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  

  /**
   * 实例化
   * 
   * @return
   */
  public static AppConfigContext getInstance() {
    if (instance == null) {
      synchronized (AppConfigContext.class) {
        if (instance == null) {
          instance = new AppConfigContext();
        }
      }
    }
    return instance;
  }

  /**
   * 获取 键值
   * 
   * @param key
   * @return
   */
  public String getProperty(String key) {
    return (String) properties.get(key);
  }

  /**
   * 将设置参数写入配置文件
   */
  public synchronized void setParameter(String driv, String url, String user, String password) throws Exception {
    String fileName = getClass().getResource("/test.properties").toString();
    if (fileName == null || "".equals(fileName))
      fileName = "test.properties";
    if (fileName.startsWith("file:/"))
      fileName = fileName.substring("file:/".length(), fileName.length());
    Properties p = new Properties();
    p.put("driverName", driv);
    p.put("url", url);
    p.put("user", user);
    p.put("password", password);
    p.store(new FileOutputStream(fileName, false), "connection parameter");

  }

  /**
   * 查看系统属性
   * 
   * @param args
   */
  public void showSystemEnv() {
    Properties p = System.getProperties();
    for (Iterator it = p.keySet().iterator(); it.hasNext();) {
      String key = (String) it.next();
      String value = (String) p.get(key);
      System.out.println(key + ":" + value);
    }
  }

}
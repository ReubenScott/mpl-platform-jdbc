package com.soak.framework.jdbc.context;

public class JdbcConfig {   // JdbcConfig   DBParameter

  /**
   * 数据库相关信息 1、驱动；2、url；3、user；4、password
   */
  private String driverclass;

  private String url;

  private String username;

  private String password;
  
  private String initialPoolSize ;

  private String maxPoolActive ;
  
  private String maxPoolIdle ;


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDriverclass() {
    return driverclass;
  }

  public void setDriverclass(String driverclass) {
    this.driverclass = driverclass;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
  
  
  public String getInitialPoolSize() {
    return initialPoolSize;
  }

  public void setInitialPoolSize(String initialPoolSize) {
    this.initialPoolSize = initialPoolSize;
  }

  public String getMaxPoolActive() {
    return maxPoolActive;
  }

  public void setMaxPoolActive(String maxPoolActive) {
    this.maxPoolActive = maxPoolActive;
  }

  public String getMaxPoolIdle() {
    return maxPoolIdle;
  }

  public void setMaxPoolIdle(String maxPoolIdle) {
    this.maxPoolIdle = maxPoolIdle;
  }

  @Override  
  public String toString() {  
    return "driverName:" + this.driverclass + "\nurl:" + this.url + "\nuser:" + this.username + "\npassword:" + this.password;  
  }  
  
  
}
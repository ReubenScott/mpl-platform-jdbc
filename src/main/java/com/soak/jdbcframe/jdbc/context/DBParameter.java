package com.soak.jdbcframe.jdbc.context;

public class DBParameter {

  /**
   * 数据库相关信息 1、驱动；2、url；3、user；4、password
   */
  private String driverclass;

  private String url;

  private String username;

  private String password;

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
  
  @Override  
  public String toString() {  
    return "driverName:" + this.driverclass + "\nurl:" + this.url + "\nuser:" + this.username + "\npassword:" + this.password;  
  }  
  
  
}
package com.soak.common.terminal;

public class UserAuthInfo {

  private String host;
  private String user;
  private String passwd;
  private int port;

  public String getHost() {
    return host;
  }

  public UserAuthInfo(String host, int port , String username, String password) {
    this.host = host;
    this.user = username;
    this.passwd = password;
    this.port = port;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPasswd() {
    return passwd;
  }

  public void setPasswd(String passwd) {
    this.passwd = passwd;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

}
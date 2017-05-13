package com.soak.framework.jdbc.datasource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.soak.framework.jdbc.context.JdbcConfig;
import com.soak.framework.jdbc.core.JdbcTemplate;
import com.soak.framework.jdbc.template.PostgreSQLTemplate;


public final class SimpleDataSource  {

  private volatile static SimpleDataSource instance;

  private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

  private String drivername ;
  private String url ;
  private String username ;
  private String password ;
  private String dataSouceJndi ;
  private int initialSize = 20 ;
  private int maxIdle = 30 ;
  private int minIdle = 15 ;
  private int maxWait ;
  private int maxActive = 20;

//  static {
//    properties = DatabaseProperties.getInstance().getProperties();
//    try {
//      Class.forName(properties.getProperty("drivername"));
//    } catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    }
//  }

  private SimpleDataSource() {
//    InputStream is = this.getClass().getResourceAsStream("/jdbc.properties");
//    if (properties == null)
//      properties = new Properties();
//    try {
//      properties.load(is);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
    ResourceBundle rb = ResourceBundle.getBundle("dbparameter");
    drivername = rb.getString("jdbc.driverClassName");
    url = rb.getString("jdbc.url");
    username = rb.getString("jdbc.username");
    password = rb.getString("jdbc.password");

    try {
      Class.forName(drivername);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  
  // 
  public static SimpleDataSource getInstance() {
    if (instance == null) {
      synchronized (SimpleDataSource.class) {
        if (instance == null) {
          instance = new SimpleDataSource();
        }
      }
    }
  
    return instance;
  }
  

  /**
   * 获取数据库连接
   */
  public Connection getConnection() {
    Connection conn = null;
    if (threadLocal.get() == null) {
      try {
        conn = DriverManager.getConnection(url, username, password);
        threadLocal.set(conn);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } else {
      conn = (Connection) threadLocal.get();
    }
    return conn;
  }

  public Connection getConnection(JdbcConfig dbParameter) {
    Connection conn = null;
    if (threadLocal.get() == null) {
      try {
        conn = DriverManager.getConnection(url, username, password);
        threadLocal.set(conn);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } else {
      conn = (Connection) threadLocal.get();
    }
    return conn;
  }

  /**
   * 获取JNDI 数据库连接
   * 
   * @return
   */
  public Connection getJndiConnection() {
    Connection conn = null;
    try {
      if (threadLocal.get() == null) {
        Context initContext = null;
        DataSource dataSource = null;
        try {
          initContext = new InitialContext();
          dataSource = (DataSource) initContext.lookup("dataSouceJndi");
        } finally {
          if (initContext != null) {
            initContext.close();
          }
        }
        if (dataSource != null) {
          conn = dataSource.getConnection();
        }
        threadLocal.set(conn);
      } else {
        conn = (Connection) threadLocal.get();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return conn;
  }

  /**
   * 关闭数据库连接
   */
  public void closeConnection() {
    Connection conn = threadLocal.get();
    if (conn != null) {
      try {
        conn.close();
        threadLocal.set(null);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }
}

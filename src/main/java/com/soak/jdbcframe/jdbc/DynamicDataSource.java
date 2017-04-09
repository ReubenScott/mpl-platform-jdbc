package com.soak.jdbcframe.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.DataSourceConnectionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soak.common.util.StringUtil;

@SuppressWarnings("unchecked")
public class DynamicDataSource {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  private volatile static DynamicDataSource dbSource;

  private final Map<String, JdbcConfig> jdbcConfigMap = new HashMap<String, JdbcConfig>();

  private static String defaultDBalias;

  private DataSource dataSource;

  private DataSourceConnectionFactory dataSourceConnectionFactory;
  
  /**
   * Hash tables to hold the used and unused objects.
   */

  private final Hashtable<String , Vector<Connection>> activeConn, idleConn ;

  // 数据库别称
  private static final ThreadLocal<String> DBALIAS = new ThreadLocal<String>();

  public static void setDBAlias(String dbalias) {
    DBALIAS.set(dbalias);
  }

  public static String getDBAlias(){
    String alias  = DBALIAS.get() ;
    if (StringUtil.isEmpty(alias)) {
      DBALIAS.set(defaultDBalias);
    }
    return alias;
  }

  public static void clearDBAlias() {
    DBALIAS.remove();
  }
  

  public static String getDefaultDBalias() {
    return defaultDBalias;
  }

  /**
   * 构造函数
   * 解析XML
   */
  private DynamicDataSource() {
    activeConn = new Hashtable<String, Vector<Connection>>();
    idleConn = new Hashtable<String, Vector<Connection>>();
    
    try {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("sys-config.xml");
      Document doc = new SAXReader().read(is);
      Element root = doc.getRootElement();
      for (Iterator<Element> i = root.elementIterator("db-info"); i.hasNext();) {
        Element foo = i.next();
        String alias = foo.attributeValue("name").trim();
        String driverName = foo.elementText("driverclass").trim();
        String url = foo.elementText("url").trim();
        String user = foo.elementText("username").trim();
        String password = foo.elementText("password").trim();

        JdbcConfig jdbcConfig = new JdbcConfig();
        jdbcConfig.setDriverclass(driverName);
        jdbcConfig.setUrl(url);
        jdbcConfig.setUsername(user);
        jdbcConfig.setPassword(password);

        if (jdbcConfigMap.containsKey(alias)) {
          System.out.println("12222222222222222222222222222");
          // throw new XSmartException("statement named \"" + id + "\" has bean defined!");
          jdbcConfigMap.clear();
          return;
        } else {
          jdbcConfigMap.put(alias, jdbcConfig);
        }
      }

      // 获取默认数据库连接
      List defaultdb = root.elements("default");
      for (int i = 0; i < defaultdb.size(); ++i) {
        Element stmt = (Element) defaultdb.get(i);
        String alias = stmt.getText();
        if (StringUtil.isEmpty(alias)) {
          // throw new XSmartException("statement's \"id\" should not be empty!");
        } else {
          defaultDBalias = alias.trim();
        }
      }
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }

  /***
   * 获取实例
   * 
   * @return
   */
  public static DynamicDataSource getInstance() {
    if (dbSource == null) {
      synchronized (DynamicDataSource.class) {
        if (dbSource == null) {
          dbSource = new DynamicDataSource();
        }
      }
    }
    return dbSource;
  }

  /**
   * 初始化数据库连接池
   */
  private void initPool() {
    String driveClassName = "jdbc.driverClassName";
    String url = "jdbc.url";
    String username = "jdbc.username";
    String password = "jdbc.password";

    String initialSize = "jdbc.initialSize";
    String maxActive = "jdbc.maxActive";
    String minIdle = "dataSource.minIdle";
    String maxIdle = "jdbc.maxIdle";
    String maxWait = "jdbc.maxWait";
    String validationQuery = "jdbc.validationQuery";

    // 是否在自动回收超时连接的时候打印连接的超时错误
    boolean logAbandoned = (Boolean.valueOf("dataSource.logAbandoned")).booleanValue();

    // 是否自动回收超时连接
    boolean removeAbandoned = (Boolean.valueOf("dataSource.removeAbandoned")).booleanValue();

    // 超时时间(以秒数为单位)
    int removeAbandonedTimeout = Integer.parseInt("dataSource.removeAbandonedTimeout");

    BasicDataSource dbcpDataSource = new BasicDataSource();
    dbcpDataSource.setDriverClassName(driveClassName);
    dbcpDataSource.setUrl(url);
    dbcpDataSource.setUsername(username);
    dbcpDataSource.setPassword(password);
    dbcpDataSource.setValidationQuery(validationQuery);

    // 初始化连接数
    if (initialSize != null)
      dbcpDataSource.setInitialSize(Integer.parseInt(initialSize));

    // 最小空闲连接
    if (minIdle != null)
      dbcpDataSource.setMinIdle(Integer.parseInt(minIdle));

    // 最大空闲连接
    if (maxIdle != null)
      dbcpDataSource.setMaxIdle(Integer.parseInt(maxIdle));

    // 超时回收时间(以毫秒为单位)
    if (maxWait != null)
      dbcpDataSource.setMaxWait(Long.parseLong(maxWait));

    // 最大连接数
    if (maxActive != null) {
      if (!maxActive.trim().equals("0"))
        dbcpDataSource.setMaxActive(Integer.parseInt(maxActive));
    }

    dbcpDataSource.setLogAbandoned(logAbandoned);
    dbcpDataSource.setRemoveAbandoned(removeAbandoned);
    dbcpDataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);

    dataSourceConnectionFactory = new DataSourceConnectionFactory(dbcpDataSource);

    try {
      dbcpDataSource.getClass();
      Connection conn = dataSourceConnectionFactory.createConnection();
      if (conn != null) {
        conn.close();
        logger.info("连接池创建成功!!!");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error("创建连接池失败!请检查设置!!!" + e.getMessage());
    }
  }

  /**
   * 连接池连接
   * 
   * @return
   */
  public void getPoolConnection() {

  }

  /**
   * 单一连接
   * 
   * @return
   */
  public Connection getSingleConnection(String dbalias) {
    if (StringUtil.isEmpty(dbalias)) {
      dbalias = defaultDBalias;
    }
    JdbcConfig dbconf = jdbcConfigMap.get(dbalias);
    if(dbconf!=null){
      String driveClassName = dbconf.getDriverclass();
      String url = dbconf.getUrl();
      String username = dbconf.getUsername();
      String password = dbconf.getPassword();
      try {
        Class.forName(driveClassName);
        return DriverManager.getConnection(url, username, password);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (SQLException e) {
        e.printStackTrace();
        logger.error("connect is null!" + e.getMessage());
        throw new RuntimeException(e);
      }
    }
    return null ;
  }

  /**
   * 通过别名 获取数据库连接
   * 
   * @param alias
   * @return
   */
  public Connection getConnection(String alias) {
    JdbcConfig config = jdbcConfigMap.get(alias);
    String driveClassName = config.getDriverclass();
    String url = config.getUrl();
    String username = config.getUsername();
    String password = config.getPassword();
    try {
      Class.forName(driveClassName);
      return DriverManager.getConnection(url, username, password);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error("connect is null!" + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * JNDI 方式获取数据库连接
   * 
   * @return
   */
  private void initDataSource(String dbJNDI) {
    if (dataSource == null) {
      synchronized (DynamicDataSource.class) {
        if (dataSource == null) {
          try {
            Context ic = new InitialContext();
            dataSource = (DataSource) ic.lookup(dbJNDI);
          } catch (NamingException e) {
            e.printStackTrace();
            logger.info("Not successfully obtain the database link, check the database JNDI :[" + dbJNDI + "] configuration is correct ");
          }
        }
      }
    }
  }

  /**
   * 获取JNDI Connection
   */
  public Connection getJNDIConnection(String dataSouceJndi) {
    Connection connection = null;
    try {
      Context initContext = null;
      DataSource dataSource = null;
      try {
        initContext = new InitialContext();
        dataSource = (DataSource) initContext.lookup(dataSouceJndi);
      } finally {
        if (initContext != null) {
          initContext.close();
        }
      }
      if (dataSource != null) {
        connection = dataSource.getConnection();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return connection;
  }
  
  
  

  /**
   * Checking object available in the used Hash table and return the available object to use.
   * @return Object.
   */
  public final synchronized Connection checkOut(String alias) {
    long now = System.currentTimeMillis();

    if (StringUtil.isEmpty(alias)) {
      alias = defaultDBalias;
    }
    
//    if (idleConn.size() > 0) {
//      Enumeration<String> keys = idleConn.keys();
//      
//      while (keys.hasMoreElements()) {
//        alias = keys.nextElement();
        Vector<Connection> idleConnections = idleConn.get(alias);
        Vector<Connection> activeConnections = activeConn.get(alias);
        Connection conn = null ;

        if(idleConnections == null ){
          idleConnections = new Vector<Connection>();
          idleConn.put(alias, idleConnections);
        } else {
//        }
//        if(idleConnections!=null && idleConnections.size() > 0){
          for(Connection connection : idleConnections ){
            boolean flag = true ;
            try {
              flag = connection.isClosed();
            } catch (SQLException e) {
              e.printStackTrace();
            }

            // 判断连接是否关闭
            if (flag) {  // 连接校验  关闭
              
            } else { // 连接没有关闭
              conn = connection ;
              break ;
            }
          }
        }
//      }
//    }
        
    if(conn == null){
      conn =this.getSingleConnection(alias);
    }

    
    // 添加 
    if(activeConnections == null ){
      activeConnections = new Vector<Connection>();
      activeConn.put(alias, activeConnections);
    }
    activeConnections.add(conn);

    // 初始 Connection 状态
    try {
      conn.setAutoCommit(true);
      conn.setReadOnly(false);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    try {
      if(conn.isClosed()){
        logger.error("connection is closed ");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    return conn ;
  }
  
  
  /**
   * Removing object from the locked state to unlocked state once job completed.
   * @param objPool .
   */
  public final synchronized void checkIn(String alias , Connection connection) {
    if (StringUtil.isEmpty(alias)) {
      alias = defaultDBalias;
    }
    Vector<Connection> idleConnections = idleConn.get(alias);
    Vector<Connection> activeConnections = activeConn.get(alias);
    
    activeConnections.remove(connection);
    idleConnections.add(connection);
  }

}

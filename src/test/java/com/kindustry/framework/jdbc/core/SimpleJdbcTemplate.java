package com.kindustry.framework.jdbc.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kindustry.framework.jdbc.core.ProcedureCallBack;
import com.kindustry.framework.jdbc.core.ProcedureResult;
import com.kindustry.framework.jdbc.core.RowMapper;
import com.kindustry.framework.jdbc.core.SimpleJdbcTemplate;
import com.kindustry.framework.jdbc.util.JdbcUtils;
import com.kindustry.system.exception.DataAccessException;

/**
 * <p>
 * Title:简单的jdbc封装
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * @author 孙钰佳
 * @main sunyujia@yahoo.cn
 * @date May 17, 2008 7:26:48 PM
 */
public class SimpleJdbcTemplate {
  private static final Log logger = LogFactory.getLog(SimpleJdbcTemplate.class);
  public static final String ORACLE = "ORACLE";
  public static final String MSSQL = "MSSQL";
  /**
   * 默认的行结果集回调处理
   */
  private RowMapper rowMapper = new RowMapper() {
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
      ResultSetMetaData resultSetMetaData = rs.getMetaData();
      Map map = new HashMap(1);
      for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
        Object value = JdbcUtils.getResultSetValue(rs, i);
        map.put(resultSetMetaData.getColumnName(i), value);
      }
      return map;
    }
  };

  /**
   * 执行sql返回执行结果
   * 
   * @param sql
   * @return List中存放的Map,key为字段,value为值
   */
  public List executeQuery(String sql) {
    return executeQuery(sql, rowMapper, null);
  }

  /**
   * 执行sql返回执行结果
   * 
   * @param sql
   * @param rowMapper
   *          针对行结果集的回调对象
   * @return
   */
  public List executeQuery(String sql, RowMapper rowMapper) {
    return executeQuery(sql, rowMapper, null);
  }

  /**
   * 执行sql返回执行结果
   * 
   * @param sql
   *          支持参数的sql 例: select * from xx where x=?
   * @param rowMapper
   *          针对行结果集的回调对象
   * @param parameters
   *          参数
   * 
   * @return
   */
  public List executeQuery(String sql, RowMapper rowMapper, List parameters) {
    Connection conn = null;
    List list = new ArrayList();
    Statement stmt = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      if (parameters != null && parameters.size() != 0) {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        stmt = pstmt;
        JdbcUtils.setParameters(pstmt, parameters);
        rs = pstmt.executeQuery();
      } else {
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
      }
      while (rs.next()) {
        list.add(rowMapper.mapRow(rs, rs.getRow()));
      }
    } catch (SQLException e) {
      throw new DataAccessException(e);
    } finally {
      closeResultSet(rs);
      closeStatement(stmt);
      closeConnection(conn);
    }
    return list;
  }

  public ProcedureResult execProcedure(String procedure, ProcedureCallBack procedureCallBack) {
    return this.execProc(procedure, null, procedureCallBack);
  }

  public ProcedureResult execProcedure(String procedure, int sqlType, ProcedureCallBack procedureCallBack) {
    return this.execProc(procedure, isOracle() ? null : new Integer(sqlType), procedureCallBack);
  }

  /**
   * 执行存储过程
   * 
   * @param procedure过程名
   * @param sqlType
   *          返回值类型,无返回值传null
   * @param procedureCallBack
   *          回调对象
   * @return
   */
  private ProcedureResult execProc(String procedure, Integer sqlType, ProcedureCallBack procedureCallBack) {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    ProcedureResult procResult = new ProcedureResult();
    try {
      procedureCallBack.jt = this;
      conn = getConnection();// 取连接
      procedureCallBack.registerParameter();// 计算参数的个数
      int parameterSize = procedureCallBack.parametersCount;// 取参数的个数
      procedureCallBack.parametersCount = 0;
      if (parameterSize > 0)
        procedure = procedure + getProcParameters(parameterSize, "?");// 根据参数的个数拼装sql
      String sql = "{" + (sqlType != null ? "?=" : "") + "call " + procedure + "}";
      logger.debug("exec " + sql);
      CallableStatement cstm = conn.prepareCall(sql);// 预编译过程
      procedureCallBack.cstm = cstm;
      procedureCallBack.startParametersIdx = sqlType != null ? 1 : 0;// 对外界忽略带返回值过程的参数下标
      if (sqlType != null)
        procedureCallBack.registerOutParameter(0, sqlType.intValue());// 注册带返回值过程的返回类型oracle不支持mssql支持
      procedureCallBack.registerParameter();// 注册参数
      boolean results = cstm.execute();// 执行过程
      int rsIndex = 1;// 结果集个数计数器
      for (; results; rsIndex++) {
        rs = cstm.getResultSet();// 取当前结果集
        List list = new ArrayList();// 为当前结果集创建一个容器
        while (rs.next())
          list.add(procedureCallBack.mapRow(rs, rsIndex));
        procResult.addRs(list);
        results = cstm.getMoreResults();// 取下一个结果集
      }
      Map map = procedureCallBack.getOutParameters();// 取声明的out参数,
      Iterator iterator = map.keySet().iterator();// 遍历声明的out参数
      while (iterator.hasNext()) {
        String key = iterator.next().toString();// out参数类型
        int idx = Integer.valueOf(key).intValue();
        if (new Integer(-10).equals(map.get(key))) {// oracle结果集列表是通过游标的形式利用out参数输出的oracle.jdbc.OracleTypes.CURSOR的值是-10
          closeResultSet(rs);// 关闭上次使用的结果集
          rs = (ResultSet) cstm.getObject(Integer.valueOf(key).intValue());// 从out参数中取结果集
          List list = new ArrayList();// 为当前结果集创建一个容器
          while (rs.next())
            list.add(procedureCallBack.mapRow(rs, rsIndex));
          rsIndex++;// 结果集计数器累加
          procResult.addRs(list);
        } else if (idx == 1 && sqlType != null)// 如果过程有返回值,从第一个out参数中取得这个返回值
          procResult.setValue(JdbcUtils.getCallableStatementValue(cstm, idx));
        else
          procResult.getOutput().add(JdbcUtils.getCallableStatementValue(cstm, idx));// 取的普通的out参数输出到out输出容器
      }
      if (sqlType == null && procResult.getOutput().size() > 0)
        // 如果过程没用返回值,例如oracle过程根本不支持返回值,将第一个非游标的out参数返回值当作过程返回值
        procResult.setValue(procResult.getOutput().get(0));
    } catch (SQLException e) {
      throw new DataAccessException(e);
    } finally {
      closeResultSet(rs);
      closeStatement(stmt);
      closeConnection(conn);
    }
    return procResult;
  }

  /**
   * 
   * @return返回一个数据库连接
   * @throws SQLException
   */
  private Connection getConnection() throws SQLException {
    Connection conn = null;
    try {
      try {
        Class.forName(driver);// 注册驱动
      } catch (ClassNotFoundException e) {
        Class.forName(driver, true, Thread.currentThread().getContextClassLoader());// 注册驱动
      }
      conn = DriverManager.getConnection(url, userName, passWord);
    } catch (ClassNotFoundException e) {
      throw new SQLException("Could not found driver " + e.getMessage());
    } catch (SQLException e) {
      throw e;
    }
    return conn;
  }

  private String getProcParameters(int count, String str) {
    String ret = "";
    while (count-- > 0)
      ret += "," + str;
    return "(" + ret.substring(1) + ")";
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setPassWord(String passWord) {
    this.passWord = passWord;
  }

  public void closeConnection(Connection con) {
    JdbcUtils.closeConnection(con);
  }

  public void closeStatement(Statement stmt) {
    JdbcUtils.closeStatement(stmt);
  }

  public void closeResultSet(ResultSet rs) {
    JdbcUtils.closeResultSet(rs);
  }

  public String getDbType() {
    return dbType;
  }

  public void setDbType(String dbType) {
    this.dbType = dbType;
  }

  public boolean isOracle() {
    return dbType.equalsIgnoreCase(ORACLE);
  }

  /**
   * 数据库的URL
   */
  private String url;
  /**
   * 数据库用户名
   */
  private String userName;
  /**
   * 数据库密码
   */
  private String passWord;
  /**
   * 数据库驱动名
   */
  private String driver;
  /**
   * 数据库类型
   */
  private String dbType;

}

package com.soak.framework.jdbc.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Table;

import com.soak.common.util.BeanUtil;
import com.soak.common.util.StringUtil;

//import com.soak.framework.jdbc.NumException;

public class PostgreSQLTemplate extends JdbcTemplate {

  /***
   * 
   * @return
   */
  public String getCurrentSchema() {
    Connection conn = getConnection();
    String schema = null;
    try {
      // DatabaseMetaData metaData = conn.getMetaData();
      schema = conn.getMetaData().getUserName();
      // schema = conn.getCatalog();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.release(conn, null, null);
    }

    return schema;
  }

  /***
   * 
   * @return
   */
  public List<String> getSchemas(String dbalias) {
    Connection conn = getConnection();
    List<String> schemas = new ArrayList<String>();
    try {
      DatabaseMetaData metaData = conn.getMetaData();
      ResultSet rs = metaData.getSchemas();
      while (rs.next()) {
        schemas.add(rs.getString("TABLE_SCHEM"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.release(conn, null, null);
    }
    return schemas;
  }

  /***
   * 判断数据库 表 是否存在
   * 
   * @param schema
   * @param tableName
   * @return
   */
  public boolean isTableExits(String schema, String tableName) {
    boolean flag = false;
    schema = StringUtil.isEmpty(schema) ? null : schema.toUpperCase();
    Connection connection = getConnection();
    DatabaseMetaData meta;
    ResultSet rs = null;
    try {
      meta = connection.getMetaData();
      rs = meta.getTables(null, schema.toLowerCase(), tableName.toLowerCase(), new String[] { "TABLE" });
      if (rs != null) {
        flag = rs.next();
        rs.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.release(connection, null, rs);
    }

    return flag;
  }

  /**
   * 删除表
   * 
   * @param schema
   * @param tableName
   * @return
   */
  public boolean dropTable(String schema, String tableName) {
    schema = StringUtil.isEmpty(schema) ? null : schema.toUpperCase();
    if (isTableExits(schema, tableName)) {
      this.truncateTable(schema, tableName);
      if (StringUtil.isEmpty(schema)) {
        this.execute("Drop table " + tableName);
      } else {
        this.execute("Drop table " + schema + "." + tableName);
      }
    }
    return !isTableExits(schema, tableName);
  }

  /***
   * 获取表字段 类型信息
   * 
   */
  protected List<Integer> getColumnTypes(String schema, String tablename) {
    Connection connection = getConnection();
    
    List<Integer> columnTypes = new ArrayList<Integer>();
    ResultSet rs = null;
    try {
      DatabaseMetaData dbmd = connection.getMetaData();

      // 获取 schema
      if (StringUtil.isEmpty(schema)) {
        schema = getCurrentSchema();
      }

      rs = dbmd.getColumns(schema.toLowerCase(), null, tablename.toLowerCase(), null);
      while (rs != null && rs.next()) {
        columnTypes.add(rs.getInt("DATA_TYPE")); // 类型
        // System.out.print(rs.getString("TABLE_CAT")); //
        // System.out.print(" " + rs.getString("TABLE_SCHEM"));
        // System.out.print(" " + rs.getString("TABLE_NAME"));
        // System.out.print(" " + rs.getString("IS_NULLABLE"));
        // System.out.print(" " + rs.getString("REMARKS"));
        // System.out.print(" " + rs.getString("SOURCE_DATA_TYPE"));
        // String typeName = rs.getString("TYPE_NAME");//类型名称
        // int precision = rs.getInt("COLUMN_SIZE");//精度
        // int isNull = rs.getInt("NULLABLE");//是否为空
        // int scale = rs.getInt("DECIMAL_DIGITS");// 小数的位数
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      super.release(connection, null, rs);
    }
    return columnTypes;
  }

  //
  public String[] readParaTable(String tableName, String columnName) {
    Connection conn = getConnection();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {

      int count = 0; // 记录表中数据的计数器
      String cntSql = ""; // 查询表中有几条记录的SQL语句
      String sltSql = ""; // 查询表中数据的SQL语句
      if (tableName == null || tableName.equals("") || columnName == null || columnName.equals("")) {
        return null;
      }

      if (conn == null) { // 数据库连接不成功
        return null;
      } else { // 测试数据库连接是否成功，如果成功进行以下查询操作
        cntSql = "select count(*) from (select " + columnName + "  from " + tableName + " group by " + columnName + ")";
        ps = conn.prepareStatement(cntSql);
        rs = ps.executeQuery();
        rs.next(); // 得到记录条数
        count = rs.getInt(1);
        if (count == 0) {
          return null; // 如果未检索到记录，则返回null
        }

        // 从表中查询数据
        String[] rsArray = new String[count]; // 创建结果集数组
        sltSql = "select nvl(" + columnName + ",' ')  from " + tableName + " group by " + columnName;
        ps = conn.prepareStatement(sltSql);
        rs = ps.executeQuery();

        // 将查询出来的结果放入到结果集数组中，作为返回值
        for (int i = 0; i < count; i++) {
          rs.next();
          rsArray[i] = rs.getString(1);

        }
        return rsArray;
      }

    } catch (SQLException e) {
      e.printStackTrace();
      try {
        conn.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      return null;
    } finally {
      this.release(conn, ps, rs);
    }
  }

  public String[] readParaTable(String tableName, String destName, String sourceName, String sourceValue) {
    Connection conn = getConnection();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      if (tableName == null || tableName.equals("") || destName == null || destName.equals("") || sourceName == null || sourceName.equals("") || sourceValue == null
          || sourceValue.equals("")) {
        return null;
      }
      int count;
      String sql = "select count(*) from (select " + destName + " from " + tableName + "  where " + sourceName + "='" + sourceValue + "' group by " + destName + ")";
      if (conn == null) {
        return null;
      } else {
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();
        rs.next();
        count = rs.getInt(1);
        if (count == 0) {
          return null;
        }
        sql = "select nvl(" + destName + ",' ') from " + tableName + " where " + sourceName + "='" + sourceValue + "' group by " + destName;
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();
        rs.next();
        String[] dest = new String[count]; // 申明一个字符串数组
        for (int i = 0; i < count; i++) {
          dest[i] = rs.getString(1);
          rs.next();
        } // end for
        return dest;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        conn.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      return null;
    } finally {
      this.release(conn, ps, rs);
    }
  }

  /**
   * 数据库 插入记录
   * 
   * @param domain
   * @return
   */
  public int saveSample(Object domain) {
    Class sample = domain.getClass();
    String packageName = sample.getPackage().getName();
    String tablename = sample.getName();
    tablename = tablename.replaceFirst(packageName + ".", "");
    // insert into account values(?,?,?)

    // 根据对象的字段 拼接 insert 语句
    Map map = BeanUtil.unpackageBean(domain);
    Set ks = map.keySet();
    Object[] columns = ks.toArray();
    Object[] params = new Object[columns.length];
    StringBuffer keystr = new StringBuffer(" (");
    StringBuffer valuestr = new StringBuffer(" values (");

    for (int i = 0; i < columns.length; i++) {
      String column = (String) columns[i];
      params[i] = map.get(column);
      if (i == 0) {
        keystr.append(column);
        valuestr.append("?");
      } else {
        keystr.append(" ," + column);
        valuestr.append(" , ?");
      }
    }
    keystr.append(") ");
    valuestr.append(") ");

    String sql = new String("insert into " + tablename + keystr + valuestr);

    logger.debug(sql);

    return executeUpdate(sql, params);
  }

  /***
   * 
   * 清空表
   */
  public boolean truncateTable(String schema, String tablename) {
    Connection connection = getConnection();
    Statement st = null;
    boolean result = false;
    try {
      st = connection.createStatement();
      String stabName = null;
      if (!StringUtil.isEmpty(schema)) {
        stabName = schema.trim() + "." + tablename.trim();
      } else {
        stabName = tablename.trim();
      }

      String sql = "TRUNCATE TABLE " + stabName.toUpperCase();
      st.execute(sql.toString());
      // connection.commit();
      result = true;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.release(connection, st, null);
    }
    return result;
  }

  
}
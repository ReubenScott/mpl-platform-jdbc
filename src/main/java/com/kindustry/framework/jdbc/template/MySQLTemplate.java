package com.kindustry.framework.jdbc.template;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kindustry.common.util.StringUtility;
import com.kindustry.framework.jdbc.core.JdbcTemplate;
import com.kindustry.framework.jdbc.orm.ColumnField;
import com.kindustry.framework.jdbc.support.Pagination;

public class MySQLTemplate extends JdbcTemplate {

  /**
   * Create new datasource if there is one not already available.
   * 
   * @return MySQLDataSource object.
   * @throws SQLException
   */
//  public Connection getConnection() {
//    if (datasource == null) {
//      datasource = new MysqlDataSource();
//      datasource.setUser(this.user);
//      datasource.setPassword(this.password);
//      datasource.setServerName(this.host);
//      datasource.setPort(Integer.parseInt(this.port));
//    }
//    return datasource.getConnection();
//  }

  /***
   * 
   * @return
   */
  public String getCurrentSchema() {
    Connection conn = getConnection();
    String schema = null;
    try {
      schema = conn.getCatalog();
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
  public List<String> getSchemas() {
    Connection conn = getConnection();
    List<String> schemas = new ArrayList<String>();
    try {
      DatabaseMetaData metaData = conn.getMetaData();
      ResultSet rs = metaData.getCatalogs();
      while (rs.next()) {
        schemas.add(rs.getString("TABLE_CAT"));
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
    schema = StringUtility.isEmpty(schema) ? null : schema.toUpperCase();
    Connection connection = getConnection();
    DatabaseMetaData meta;
    ResultSet rs = null;
    try {
      meta = connection.getMetaData();
      rs = meta.getTables(schema, null, tableName, new String[] { "TABLE" });
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
      if (StringUtility.isEmpty(schema)) {
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
      if (!StringUtility.isEmpty(schema)) {
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

  @Override
  public List<String> getPrimaryKeys(String schema, String tablename) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<ColumnField> getColumnFields(String schema, String tablename) {
    // TODO Auto-generated method stub
    return null;
  }
  

  @Override
  public boolean replaceTable(String srcSchema, String srcTabName, String targetSchema, String destTabName) {
    
    return false ;
  }


  @Override
  public boolean insertTable(String srcSchema, String srcTabName, String targetSchema, String destTabName) {
    
    
    return false ;
  }
  
  @Override
  public boolean mergeTable(String srcSchema, String srcTabName, String targetSchema, String destTabName) {
    // TODO Auto-generated method stub
    return false;
  }
  
  
  @Override
  public Pagination queryPageBySQL(String sql, int startIndex, int pageSize, Object... params) {
    long totalCount = queryCountResult(sql, params);
    sql = sql + " limit " + pageSize;
    List<List> items =  queryForList(sql, params);
    
    Pagination ps = new Pagination(items,totalCount, startIndex, pageSize);
    return ps;
  }
  
  
  public Pagination querySamplePageBySQL(Class sample, String sql, int startIndex, int pageSize, Object... params) {
    long totalCount = queryCountResult(sql, params);
    sql = sql + " limit " + startIndex + " , " + pageSize;
    List items =  querySampleList(sample, sql, params);
    System.out.println(sql);
    Pagination ps = new Pagination(items,totalCount, startIndex, pageSize);
    return ps;
  }
  
  
}
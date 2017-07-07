package com.kindustry.framework.jdbc.template;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kindustry.common.util.StringUtil;
import com.kindustry.framework.jdbc.core.JdbcTemplate;
import com.kindustry.framework.jdbc.orm.ColumnField;


public class PostgreSQLTemplate extends JdbcTemplate {

  /***
   * 
   * @return
   */
  public String getCurrentSchema() {
    Connection conn = getConnection();
    String schema = null;
    try {
      schema = conn.getMetaData().getURL();
      if(schema.contains("?")){
        schema = schema.substring(schema.indexOf("?")+1);
        schema = schema.replace("searchpath=", "").trim();
      } else {
        schema = conn.getMetaData().getUserName() ;
      }
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
      rs = dbmd.getColumns(connection.getCatalog(), schema.toLowerCase(), tablename.toLowerCase(), null);
//      System.out.println(String.format("|%-26s|%-10s|%-10s|%-10s|%-10s|%-10s|", "表类别","表模式","表名称","字段名称","类型名称","字段类型"));      
      while (rs != null && rs.next()) {
        columnTypes.add(rs.getInt("DATA_TYPE")); // 类型

//        System.out.println(String.format("|%-10s|%-10s|%-10s|%-10s|%-10s|%-10s|%-10s|%-10s|", 
//          rs.getString("TABLE_CAT"),rs.getString("TABLE_SCHEM"),rs.getString("TABLE_NAME")
//         ,rs.getString("COLUMN_NAME"),rs.getString("TYPE_NAME"),rs.getString("DATA_TYPE")
//         ,rs.getString("IS_NULLABLE"),rs.getString("SOURCE_DATA_TYPE")
//        ));
        
//         System.out.print(" " + rs.getString("IS_NULLABLE"));
//         String typeName = rs.getString("TYPE_NAME");//类型名称
//         int precision = rs.getInt("COLUMN_SIZE");//精度
//         int isNull = rs.getInt("NULLABLE");//是否为空
//         int scale = rs.getInt("DECIMAL_DIGITS");// 小数的位数
//         System.out.println();
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
  public boolean mergeTable(String srcSchema, String srcTabName, String targetSchema, String destTabName) {
    // TODO Auto-generated method stub
    return false;
  }
}
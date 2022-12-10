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

/**
 * 未测试
 * @author reuben
 *
 */
public class OracleTemplateUntest extends JdbcTemplate {

  /***
   * 
   * @return
   */
  public String getCurrentSchema() {
    Connection conn = getConnection();
    String schema = null;
    try {
      schema = conn.getMetaData().getUserName();
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
    schema = StringUtility.isEmpty(schema) ? null : schema.toUpperCase();
    Connection connection = getConnection();
    DatabaseMetaData meta;
    ResultSet rs = null;
    try {
      meta = connection.getMetaData();
      rs = meta.getTables(null, schema, tableName.toUpperCase(), new String[] { "TABLE" });
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

      String sql = "TRUNCATE TABLE " + stabName.toUpperCase() + " IMMEDIATE";
      // sql = "ALTER TABLE " + stabName + " ACTIVATE NOT LOGGED INITIALLY WITH EMPTY TABLE";
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
    Connection connection = getConnection();
    List<String> primaryKeys = new ArrayList<String>();
    ResultSet rs = null;
    try {
      DatabaseMetaData dbmd = connection.getMetaData();

      // 获取 schema
      if (StringUtility.isEmpty(schema)) {
        schema = getCurrentSchema();
      }

      rs = dbmd.getPrimaryKeys(null, schema.toUpperCase(), tablename.toUpperCase());
      while (rs != null && rs.next()) {
        String columnName = rs.getString("COLUMN_NAME");//列名
        primaryKeys.add(columnName);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      super.release(connection, null, rs);
    }
    
    return primaryKeys;
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

      rs = dbmd.getColumns(null, schema.toUpperCase(), tablename.toUpperCase(), null);
      while (rs != null && rs.next()) {
        columnTypes.add(rs.getInt("DATA_TYPE")); // 类型
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      super.release(connection, null, rs);
    }
    return columnTypes;
  }


  
  @Override
  protected List<ColumnField> getColumnFields(String schema, String tablename) {
    Connection connection = getConnection();
    List<ColumnField> columnFields = new ArrayList<ColumnField>();
    ResultSet rs = null;
    try {
      DatabaseMetaData dbmd = connection.getMetaData();

      // 获取 schema
      if (StringUtility.isEmpty(schema)) {
        schema = getCurrentSchema();
      }
      

      rs = dbmd.getColumns(null, schema.toUpperCase(), tablename.toUpperCase(), null);
      while (rs != null && rs.next()) {
        ColumnField field = new ColumnField();
        String columnName = rs.getString("COLUMN_NAME");//列名
        String typeName = rs.getString("TYPE_NAME");//类型名称
        int dataType = rs.getInt("DATA_TYPE") ; // 字段数据类型
        int precision = rs.getInt("COLUMN_SIZE");//精度
        int scale = rs.getInt("DECIMAL_DIGITS");// 小数的位数
        int isNull = rs.getInt("NULLABLE");//是否为空
        
        // System.out.print(rs.getString("TABLE_CAT")); //
        // System.out.print(" " + rs.getString("TABLE_SCHEM"));
        // System.out.print(" " + rs.getString("TABLE_NAME"));
        // System.out.print(" " + rs.getString("IS_NULLABLE"));
        // System.out.print(" " + rs.getString("REMARKS"));
        // System.out.print(" " + rs.getString("SOURCE_DATA_TYPE"));
        
        field.setColumnName(columnName);
        field.setTypeName(typeName);
        field.setDataType(dataType);
        field.setPrecision(precision);
        field.setIsNull(isNull);
        field.setScale(scale);
        
        columnFields.add(field);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      super.release(connection, null, rs);
    }
    
    return columnFields;
  }

  @Override
  public boolean replaceTable(String srcSchema, String srcTabName, String targetSchema, String destTabName) {
    truncateTable(targetSchema, destTabName);
    
    StringBuffer sql = new StringBuffer();
    sql.append("insert into " + targetSchema + "." + destTabName +" ( ");

    StringBuffer columns = new StringBuffer();
    StringBuffer select = new StringBuffer(" select ");
    
    
    List<ColumnField> fields =  getColumnFields(srcSchema, srcTabName);
    for(int i = 0 ; i< fields.size() ; i++ ){
      ColumnField field = fields.get(i);
      String columnName = field.getColumnName() ;
      
      if(i > 0){
        columns.append( "," + columnName ) ;
      } else {
        columns.append( columnName ) ;
      }

      if(i > 0){
        select.append(","+ columnName ) ;
      } else {
        select.append(columnName) ;
      }
      
    }
    sql.append(columns + " ) ");
    sql.append(select);
    sql.append( " from " + srcSchema + "." + srcTabName ) ;

    return execute(sql.toString());
  }
  

  @Override
  public boolean insertTable(String srcSchema, String srcTabName, String targetSchema, String destTabName) {
    StringBuffer sql = new StringBuffer();
    sql.append("insert into " + targetSchema + "." + destTabName +" ( ");

    StringBuffer columns = new StringBuffer();
    StringBuffer select = new StringBuffer(" select ");
    
    
    List<ColumnField> fields =  getColumnFields(srcSchema, srcTabName);
    for(int i = 0 ; i< fields.size() ; i++ ){
      ColumnField field = fields.get(i);
      String columnName = field.getColumnName() ;
      
      if(i > 0){
        columns.append( "," + columnName ) ;
      } else {
        columns.append( columnName ) ;
      }

      if(i > 0){
        select.append(","+ columnName ) ;
      } else {
        select.append(columnName) ;
      }
      
    }
    sql.append(columns + " ) ");
    sql.append(select);
    sql.append(" from " + srcSchema + "." + srcTabName ) ;
    
    List<String> keys =  getPrimaryKeys(targetSchema, destTabName);
    if(keys.size() > 0 ){  // 如果有主键
      sql.append(" S WHERE NOT EXISTS ( ") ;
      sql.append(" SELECT 1 FROM  ") ;
      sql.append(targetSchema + "." + destTabName +" T WHERE ");

      for(int i =0 ; i< keys.size() ; i++  ){
        String key = keys.get(i);
        if(i > 0){
          sql.append( " and T." + key + " = S." + key ) ;
        } else {
          sql.append( " T." + key + " = S." + key ) ;
        }
      }
      sql.append(" )  ") ;
    }

    return execute(sql.toString());
  }

  @Override
  public boolean mergeTable(String srcSchema, String srcTabName, String targetSchema, String destTabName) {
    StringBuffer sql = new StringBuffer();
    sql.append("merge into " + targetSchema + "." + destTabName +" T USING ( select ");

    StringBuffer columns = new StringBuffer();
    StringBuffer sets = new StringBuffer();
    StringBuffer values = new StringBuffer();
    
    
    List<ColumnField> fields =  getColumnFields(srcSchema, srcTabName);
    for(int i = 0 ; i< fields.size() ; i++ ){
      ColumnField field = fields.get(i);
      String columnName = field.getColumnName() ;
//      System.out.println(field.getColumnName() + " "+ field.getTypeName() + " " + " " + field.getPrecision() + " "+ field.getScale() );
      
      if(i > 0){
        columns.append( "," + columnName ) ;
      } else {
        columns.append( columnName ) ;
      }

      if(i > 0){
        sets.append(", T."+ columnName  + " = S."+ columnName  ) ;
      } else {
        sets.append(" T."+ columnName  + " = S."+ columnName  ) ;
      }

      if(i > 0){
        values.append( ",S." + columnName ) ;
      } else {
        values.append( "S."+columnName ) ;
      }
      
    }
    sql.append(columns);
    sql.append( " from " + srcSchema + "." + srcTabName + " ) S ON (") ;

    List<String> keys =  getPrimaryKeys(targetSchema, destTabName);
    for(int i =0 ; i< keys.size() ; i++  ){
      String key = keys.get(i);
      if(i > 0){
        sql.append( " and T." + key + " = S." + key ) ;
      } else {
        sql.append( " T." + key + " = S." + key ) ;
      }
    }
    sql.append( " ) WHEN MATCHED THEN UPDATE SET ") ;
    sql.append(sets);
    sql.append( " WHEN NOT MATCHED THEN INSERT ( ") ;
    sql.append(columns);
    sql.append( " ) values ( ") ;
    sql.append(values);
    sql.append( " )") ;
    
    return execute(sql.toString());
  }

  @Override
  public Pagination queryPageBySQL(String sql, int startIndex, int pageSize, Object... params) {
    // TODO Auto-generated method stub {
    StringBuffer querySQL = new StringBuffer();
    querySQL.append("select * from (select my_table.*,rownum as my_rownum from(").append(sql).append(") my_table where rownum<").append(startIndex + pageSize).append(
        ") where my_rownum>=").append(startIndex);

    return null ;
  }

  
  @Override
  public Pagination querySamplePageBySQL(Class sample, String sql, int startIndex, int pageSize, Object... params) {
    // TODO Auto-generated method stub
    return null;
  }
}
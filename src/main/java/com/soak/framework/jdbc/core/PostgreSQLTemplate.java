package com.soak.framework.jdbc.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.soak.framework.constant.DBType;
import com.soak.framework.date.DateStyle;
import com.soak.framework.date.DateUtil;
import com.soak.framework.io.IOHandler;
//import com.soak.framework.jdbc.NumException;
import com.soak.framework.jdbc.Restrictions;
import com.soak.framework.jdbc.datasource.DynamicDataSource;
import com.soak.framework.orm.Column;
import com.soak.framework.orm.Table;
import com.soak.framework.util.BeanUtil;
import com.soak.framework.util.ExcelUtil;
import com.soak.framework.util.StringUtil;


public class PostgreSQLTemplate extends JdbcTemplate {


  @Override
  public boolean validate(Connection connection) {
    try {
      return !connection.isClosed();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    return false ;
  }
  

  private void setPreparedValues(PreparedStatement ps, List params) {
    try {
      if (params != null && params.size() > 0) {
        for (int i = 0; i < params.size(); i++) {
          ps.setObject(i + 1, params.get(i));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /***
   * 
   * @return
   */
  public String getCurrentSchema() {
    Connection conn = checkOut();
    String schema = null;
    try {
//      DatabaseMetaData metaData = conn.getMetaData();
          schema = conn.getMetaData().getUserName();
//          schema = conn.getCatalog();  
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
    Connection conn = checkOut();
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
   *  判断数据库 表 是否存在
   *  
   * @param schema
   * @param tableName
   * @return
   */
  public boolean isTableExits(String schema, String tableName){
    boolean flag = false ;
    schema  = StringUtil.isEmpty(schema) ? null : schema.toUpperCase() ;
    Connection connection = checkOut();
    DatabaseMetaData meta;
    ResultSet rs = null;
    try {
      meta = connection.getMetaData();
//          rs = meta.getTables(null, schema, tableName.toUpperCase(), new String[]{"TABLE"});
          rs = meta.getTables(schema, null, tableName.toUpperCase(), new String[]{"TABLE"});
      
      if(rs != null ){
        flag = rs.next();
        rs.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.release(connection, null, rs);
    }
    
    return flag ;
  }
  

  /**
   * 删除表
   * @param schema
   * @param tableName
   * @return
   */
  public boolean dropTable(String schema, String tableName){
    schema  = StringUtil.isEmpty(schema) ? null : schema.toUpperCase() ;
    if(isTableExits( schema, tableName)){
      this.truncateTable(schema, tableName);
      if(StringUtil.isEmpty(schema)){
         this.execute( "Drop table "+ tableName);
      } else {
        this.execute( "Drop table "+ schema + "." + tableName);
      }
    }
    return !isTableExits(schema, tableName) ;
  }
  

  /***
   * 获取表字段 类型信息
   * 
   */
   protected List<Integer> getColumnTypes(Connection connection, String schema, String tablename) {
    List<Integer> columnTypes = new ArrayList<Integer>();
    ResultSet rs = null;
    try {
      DatabaseMetaData dbmd = connection.getMetaData();
      String dbType = dbmd.getDatabaseProductName();
      
      // 获取 schema 
      if (StringUtil.isEmpty(schema)) {
        schema = getCurrentSchema();
      }
      
      if (dbType.startsWith("DB2")) {
        rs = dbmd.getColumns(null, schema.toUpperCase(), tablename.toUpperCase(), null);
      } else if (dbType.startsWith("MySQL")) {
        rs = dbmd.getColumns(schema.toUpperCase(), null, tablename.toUpperCase(), null);
      } else {
        // TODO

      }
      while (rs != null && rs.next()) {
        columnTypes.add(rs.getInt("DATA_TYPE")); // 类型
//        System.out.print(rs.getString("TABLE_CAT")); //  
//        System.out.print(" " + rs.getString("TABLE_SCHEM"));
//        System.out.print(" " + rs.getString("TABLE_NAME"));
//        System.out.print(" " + rs.getString("IS_NULLABLE"));
//        System.out.print(" " + rs.getString("REMARKS"));
//        System.out.print(" " + rs.getString("SOURCE_DATA_TYPE"));
        // String colName = rs.getString("COLUMN_NAME");//列名
        // String typeName = rs.getString("TYPE_NAME");//类型名称
        // int precision = rs.getInt("COLUMN_SIZE");//精度
        // int isNull = rs.getInt("NULLABLE");//是否为空
        // int scale = rs.getInt("DECIMAL_DIGITS");// 小数的位数
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return columnTypes;
  }

  //
  public String[] readParaTable(String tableName, String columnName) {
    Connection conn = checkOut();
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
    Connection conn = checkOut();
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

    return executeUpdate( sql, params);
  }

  
  /** ************** "select" start *************** */
  

  /**
   * 根据模板查询 queryBySample
   * 
   */
  public <T> T findOneByAnnotatedSample(T annotatedSample , Restrictions... restrictions ) {
    List<String> columns = new ArrayList<String>();
    List<String> fieldNames = new ArrayList<String>();
    List<Object> params = new ArrayList<Object>();
    StringBuilder condition = new StringBuilder(" where 1=1 ");
    String schema = null;
    String tablename = null;

    // 获取类的class
    Class<? extends Object> stuClass = annotatedSample.getClass();

    /* 通过获取类的类注解，来获取类映射的表名称 */
    if (stuClass.isAnnotationPresent(Table.class)) { // 如果类映射了表
      Table table = (Table) stuClass.getAnnotation(Table.class);
      // sb.append(table.name() + " where 1=1 "); // 加入表名称
      schema = table.schema();
      tablename = table.name();

      /* 遍历所有的字段 */
      Field[] fields = stuClass.getDeclaredFields();// 获取类的字段信息
      for (Field field : fields) {
        if (field.isAnnotationPresent(Column.class)) {
          Column col = field.getAnnotation(Column.class); // 获取列注解
          String columnName = col.name(); // 数据库映射字段
          columns.add(columnName);
          String fieldName = field.getName(); // 获取字段名称
          fieldNames.add(fieldName);
          String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);// 获取字段的get方法

          try {
            // get到field的值
            Method method = stuClass.getMethod(methodName);
            Object fieldValue = method.invoke(annotatedSample);
            /* 空字段跳过拼接过程。。。 */// 如果没有值，不拼接
            if (fieldValue != null) {
              condition.append(" and " + columnName + "=" + "?");
              params.add(fieldValue);
            }
          } catch (SecurityException e) {
            e.printStackTrace();
          } catch (NoSuchMethodException e) {
            e.printStackTrace();
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }

        }
      }
    }

    // 附加条件
    for(Restrictions term : restrictions){
      condition.append(term.getSql());
      params.addAll(term.getParams());
    }
    
    String sql = "select " + StringUtil.arrayToString(columns) + " from " ;
    if(!StringUtil.isEmpty(schema)){
      sql += schema + "." ;
    }
    
    sql +=  tablename + condition;
    logger.debug(sql);

    Connection conn = checkOut();
    PreparedStatement ps = null;
    ResultSet rs = null;
    T obj = null ;
    try {
      conn.setReadOnly(true);
      ps = conn.prepareStatement(sql);
      this.setPreparedValues(ps, params);
      rs = ps.executeQuery();
      ResultSetMetaData rsmd = rs.getMetaData();
      while (rs.next()) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          map.put(fieldNames.get(i - 1), rs.getObject(i));
        }
        obj = (T) BeanUtil.autoPackageBean(stuClass, map);
        break;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.release(conn, ps, rs);
    }

    return obj;
  }



  
  /**
   * 查询一条记录
   * 
   * @param sql
   *          String
   * @param paramList
   *          ArrayList
   * @return HashMap
   */
  public Object queryOneObject(String sql, Object... params) {
    Connection conn = checkOut();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn.setReadOnly(true);
      ps = conn.prepareStatement(sql);
      this.setPreparedValues(ps, params);
      rs = ps.executeQuery();
      rs.next();
      return rs.getObject(1);
    } catch (SQLException e) {
      logger.info(e.getMessage());
      return null;
    } finally {
      this.release(conn, ps, rs);
    }
  }
  
  /**
   * 查询一条记录
   * 
   * @param sql
   *          String
   * @param paramList
   *          ArrayList
   * @return HashMap
   */
  public List<?> queryOneAsList(String sql, Object... params) {
    Connection conn = checkOut();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    List row = new ArrayList();
    try {
      conn.setReadOnly(true);
      ps = conn.prepareStatement(sql);
      this.setPreparedValues(ps, params);
      rs = ps.executeQuery();
      ResultSetMetaData rsmd = rs.getMetaData();
      while (rs.next()) {
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          row.add(rs.getObject(i));
        }
      }
      return row;
    } catch (SQLException e) {
      logger.info(e.getMessage());
      return null;
    } finally {
      this.release(conn, ps, rs);
    }
  }



  /**
   * 查询数据
   * 
   * @param sql
   *          String
   * @return HashMap[]
   */
  public HashMap[] queryForMap(String sql, Object... params) {
    Connection conn = checkOut();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn.setReadOnly(true);
      ps = conn.prepareStatement(sql);
      if (params != null) {
        for (int i = 0; i < params.length; i++) {
          ps.setObject(i + 1, params[i]);
        }
      }
      rs = ps.executeQuery();

      ResultSetMetaData rsmdt = rs.getMetaData();
      String[] colNames = new String[rsmdt.getColumnCount()];
      for (int i = 1; i <= rsmdt.getColumnCount(); i++) {
        colNames[i - 1] = rsmdt.getColumnName(i).toLowerCase();
      }
      Vector allRow = new Vector();
      while (rs.next()) {
        HashMap hashRow = new HashMap();
        for (int i = 0; i < colNames.length; i++) {
          hashRow.put(colNames[i], rs.getObject(colNames[i]));
        }
        allRow.add(hashRow);
      }
      HashMap[] HashAllRows = new HashMap[allRow.size()];
      for (int i = 0; i < HashAllRows.length; i++) {
        HashAllRows[i] = (HashMap) allRow.get(i);
      }
      return HashAllRows;
    } catch (SQLException e) {
      e.printStackTrace();
      logger.info(e.getMessage());
      try {
        conn.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    } finally {
      this.release(conn, ps, rs);
    }
    return null;
  }

  /**
   * 查询sql执行的总记录数,带参数
   * 
   * @param sql
   *          String
   * @param paramList
   *          ArrayList
   * @return int
   */
  public int queryCountResult(String sql, Object... params) {
    Connection conn = checkOut();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      StringBuffer querySQL = new StringBuffer();
      querySQL.append("select count(1) from (").append(sql).append(") as my_table");
      ps = conn.prepareStatement(querySQL.toString());
      this.setPreparedValues(ps, params);
      rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getInt(1);
      } else {
        return 0;
      }
    } catch (SQLException e) {
      logger.info(e.getMessage());
      logger.info(sql);
      return 0;
    } finally {
      this.release(conn, ps, rs);
    }
  }

  /** ************** "select" end *************** */
  
  

  /** ************** "insert, delete , update" start *************** */



  
  /**
   * 
   * 根据实例更新
   * 
   */
  public boolean updateAnnotatedEntity(Object annotatedSample , List<Restrictions> restrictions ) {
    return updateAnnotatedEntity(annotatedSample, restrictions.toArray(new Restrictions[restrictions.size()]));
  }

  /**
   * 
   * 根据实例更新
   * 
   */
  public boolean updateAnnotatedEntity(Object annotatedSample , Restrictions... restrictions ) {
    boolean result = false ;
    List<String> fieldNames = new ArrayList<String>();
    List<Object> params = new ArrayList<Object>();
    String schema = null;
    String tablename = null;
    StringBuffer sql = new StringBuffer("update ");
    
    
    // 获取类的class
    Class<? extends Object> stuClass = annotatedSample.getClass();

    /* 通过获取类的类注解，来获取类映射的表名称 */
    if (stuClass.isAnnotationPresent(Table.class)) { // 如果类映射了表
      Table table = (Table) stuClass.getAnnotation(Table.class);
      schema = table.schema();
      tablename = table.name();
      
      if(!StringUtil.isEmpty(schema)){
        sql.append( schema + "." );
      }
      sql.append(tablename + " set ");

      /* 遍历所有的字段 */
      Field[] fields = stuClass.getDeclaredFields();// 获取类的字段信息
      for (Field field : fields) {
        if (field.isAnnotationPresent(Column.class)) {
          Column col = field.getAnnotation(Column.class); // 获取列注解
          String columnName = col.name(); // 数据库映射字段
          String fieldName = field.getName(); // 获取字段名称
          fieldNames.add(fieldName);
          String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);// 获取字段的get方法

          try {
            // get到field的值
            Method method = stuClass.getMethod(methodName);
            Object fieldValue = method.invoke(annotatedSample);
            /* 空字段跳过拼接过程。。。 */// 如果没有值，不拼接
            if (fieldValue != null) {
              if(params.size() == 0 ){
                sql.append(columnName + " = " + " ? ");
              } else{
                sql.append(", " + columnName + " = " + " ? ");
              }
              params.add(fieldValue);
            }
          } catch (SecurityException e) {
            e.printStackTrace();
          } catch (NoSuchMethodException e) {
            e.printStackTrace();
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }

        }
      }
    }

    // 条件
    StringBuilder condition = new StringBuilder(" where 1=1 ");
    for(Restrictions term : restrictions){
      condition.append(term.getSql());
      params.addAll(term.getParams());
    }
    sql.append( condition);
    
    logger.debug(sql.toString().replace("?", "?[{}]"), params.toArray());    

    Connection conn = checkOut();
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(sql.toString());
      this.setPreparedValues(ps, params);
      ps.execute();
      result = true ;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.release(conn, ps, null);
    }

    return result;
  }
  
  

  /** ************** "insert, delete , update" end *************** */

  // *********** ***************

  /**
   * 带参数的翻页功能(oracle)
   * 
   * @param sql
   *          String
   * @param paramList
   *          ArrayList
   * @param startIndex
   *          int
   * @param size
   *          int
   * @return HashMap[]
   */
  public HashMap[] queryPageSQL( String sql, int startIndex, int size, Object... paramList) {
    StringBuffer querySQL = new StringBuffer();
    querySQL.append("select * from (select my_table.*,rownum as my_rownum from(").append(sql).append(") my_table where rownum<").append(startIndex + size).append(
        ") where my_rownum>=").append(startIndex);

    return queryForMap(querySQL.toString(), paramList);
  }

  /***
   * 
   * 清空表
   */
  public boolean truncateTable(String schema, String tablename) {
    Connection connection = checkOut();
    Statement st = null;
    boolean result = false ;
    try {
      st = connection.createStatement();
      String stabName = null ;
      if (!StringUtil.isEmpty(schema)) {
        stabName  = schema.trim() + "." + tablename.trim() ;
      } else {
        stabName  = tablename.trim() ;
      }
      
      String sql = "TRUNCATE TABLE " + stabName.toUpperCase()  ;
      st.execute(sql.toString());
   //   connection.commit();
      result = true ;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.release(connection, st, null);
    }
    return result;
  }

  
  /***
   * 
   * DEL文件入库
   * 
   * @param sql
   * 
   * @param filePath
   *          DEL 文件路径
   * 
   * @param split
   *          字段分隔符
   * 
   */
  public boolean loadDelFile( String schema, String tablename, String filePath, char split) {
    // 数据库文件 分割符号 0X1D : 29
    // split = new String(new byte[] { 29 });
    long start = System.currentTimeMillis();
    Connection connection = checkOut();
    PreparedStatement ps = null;
    BufferedReader reader = null;

    boolean flag = false ;
    int loadCount = 0; // 批量计数
    try {
      String encode = IOHandler.getCharSetEncoding(filePath);  //TODO 不靠谱
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), encode));
      // 获取字段类型
      List<Integer> columnTypes = this.getColumnTypes(connection, schema, tablename);
      int cloumnCount = columnTypes.size();   // 表 字段数
      // 根据表名 生成 Insert语句
      // "insert into CBOD_ECCMRAMR values (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?,?,?,?)"
      StringBuffer sql;
      if (schema == null || "".equals(schema.trim())) {
        sql = new StringBuffer("insert into " + tablename + " values (");
      } else {
        sql = new StringBuffer("insert into " + schema.trim() + "." + tablename + " values (");
      }
      for (int i = 1; i < cloumnCount; i++) {
        sql.append("?,");
      }
      sql.append("?)");
      
      logger.debug(sql.toString());

      ps = connection.prepareStatement(sql.toString());
      connection.setAutoCommit(false);

      String line;
      while ((line = reader.readLine()) != null) {
        String[] lineData = line.split(String.valueOf(split));  
        int dataNum = lineData.length ; // 数据文件 字段数
        for (int i = 0; i < cloumnCount; i++) {
          try {
            if( i + 1 > dataNum){
              ps.setObject(i + 1, null);
            } else {
              ps.setObject(i + 1, this.castDBType(columnTypes.get(i), lineData[i]));
            }
          } catch (Exception e) {
            logger.error(columnTypes.get(i) + " :  " + lineData[i]);
            e.printStackTrace();
          }
        }
        ps.addBatch();
        // 1w条记录插入一次
        if (++loadCount % BATCHCOUNT == 0) {
          ps.executeBatch();
          connection.commit();
        }
      }
      // 最后插入不足1w条的数据
      ps.executeBatch();
      connection.commit();
      connection.setAutoCommit(true);
      flag = true ;
      long end = System.currentTimeMillis();
      logger.debug("load into ["+ schema +"." + tablename +  "] Total : [" + loadCount + "] records, Take [" + (float)(end - start)/1000 + "] seconds . Average : "  + 1000*loadCount/(end - start) + " records/second"  );
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
      e.getNextException().printStackTrace();
      logger.error(e.getMessage());
    } finally {
      try {
        this.release(connection, ps, null);
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return flag ;
  }

  



}
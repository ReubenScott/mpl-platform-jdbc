package com.soak.jdbcframe.jdbc.core;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title:存储过程回调类
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
 * @date May 17, 2008 6:23:50 PM
 */
public abstract class ProcedureCallBack {
  /**
   * jdbc模版 包可见
   */
  SimpleJdbcTemplate jt;
  /**
   * 执行过程的语句 包可见
   */
  CallableStatement cstm;
  /**
   * 0表示过程无返回值,1表示过程有返回值,声明的参数下标需加1 包可见
   */
  int startParametersIdx = 0;
  /**
   * 参数个数(排除返回值参数) 包可见
   */
  int parametersCount;
  /**
   * 注册的out参数(包括返回值参数)
   */
  private Map outParameters = new HashMap();

  /**
   * 参数计数器加1
   */
  private void addParametersCount() {
    parametersCount++;
  };

  /**
   * 根据用户声明下标取参数实际下标
   * 
   * @param parameterIndex
   * @return
   */
  private int getParameterIndex(int parameterIndex) {
    return parameterIndex + startParametersIdx;
  }

  /**
   * Registers the OUT parameter in ordinal position
   * <code>parameterIndex</code> to the JDBC type <code>sqlType</code>.
   * All OUT parameters must be registered before a stored procedure is
   * executed.
   * <p>
   * The JDBC type specified by <code>sqlType</code> for an OUT parameter
   * determines the Java type that must be used in the <code>get</code>
   * method to read the value of that parameter.
   * <p>
   * If the JDBC type expected to be returned to this output parameter is
   * specific to this particular database, <code>sqlType</code> should be
   * <code>java.sql.Types.OTHER</code>. The method {@link #getObject}
   * retrieves the value.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, and so on
   * @param sqlType
   *            the JDBC type code defined by <code>java.sql.Types</code>.
   *            If the parameter is of JDBC type <code>NUMERIC</code> or
   *            <code>DECIMAL</code>, the version of
   *            <code>registerOutParameter</code> that accepts a scale value
   *            should be used.
   * 
   * @exception SQLException
   *                if a database access error occurs
   * @see Types
   */
  public void registerOutParameter(int parameterIndex, int sqlType)
      throws SQLException {
    if (cstm != null) {
      parameterIndex = getParameterIndex(parameterIndex);
      cstm.registerOutParameter(parameterIndex, sqlType);
      outParameters.put(String.valueOf(parameterIndex), new Integer(
          sqlType));
    }
    addParametersCount();
  };

  /**
   * 添加一个oracle游标,mssql环境下执行无影响相当于空调用
   * 
   * @param parameterIndex
   * @throws SQLException
   */
  public void addOracleCursor(int parameterIndex) throws SQLException {
    if (!jt.isOracle())
      return;
    if (cstm != null) {
      parameterIndex = getParameterIndex(parameterIndex);
//      cstm.registerOutParameter(parameterIndex, oracle.jdbc.OracleTypes.CURSOR);
//      outParameters.put(String.valueOf(parameterIndex), new Integer(oracle.jdbc.OracleTypes.CURSOR));
    }
    addParametersCount();
  };

  /**
   * Registers the parameter in ordinal position <code>parameterIndex</code>
   * to be of JDBC type <code>sqlType</code>. This method must be called
   * before a stored procedure is executed.
   * <p>
   * The JDBC type specified by <code>sqlType</code> for an OUT parameter
   * determines the Java type that must be used in the <code>get</code>
   * method to read the value of that parameter.
   * <p>
   * This version of <code>registerOutParameter</code> should be used when
   * the parameter is of JDBC type <code>NUMERIC</code> or
   * <code>DECIMAL</code>.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, and so on
   * @param sqlType
   *            the SQL type code defined by <code>java.sql.Types</code>.
   * @param scale
   *            the desired number of digits to the right of the decimal
   *            point. It must be greater than or equal to zero.
   * @exception SQLException
   *                if a database access error occurs
   * @see Types
   */
  public void registerOutParameter(int parameterIndex, int sqlType, int scale)
      throws SQLException {
    if (cstm != null)
      cstm.registerOutParameter(getParameterIndex(parameterIndex),
          sqlType, scale);
    outParameters.put(new Integer(parameterIndex), new Integer(sqlType));
    addParametersCount();
  };

  /**
   * 回调方法,内部类实现
   * 
   * @throws SQLException
   */
  public void registerParameter() throws SQLException {
  }

  /**
   * 回调方法,内部类实现 结果集行对象的封装
   * 
   * @param rs
   * @param rsIndex
   * @return
   * @throws SQLException
   */
  public Object mapRow(ResultSet rs, int rsIndex) throws SQLException {
    return null;
  }

  /**
   * 取声明的所有out参数
   * 
   * @return
   */
  public Map getOutParameters() {
    return outParameters;
  }

  /**
   * Sets the designated parameter to SQL <code>NULL</code>.
   * 
   * <P>
   * <B>Note:</B> You must specify the parameter's SQL type.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param sqlType
   *            the SQL type code defined in <code>java.sql.Types</code>
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    if (cstm != null)
      cstm.setNull(getParameterIndex(parameterIndex), sqlType);
    addParametersCount();
  }

  /**
   * Sets the designated parameter to the given Java <code>boolean</code>
   * value. The driver converts this to an SQL <code>BIT</code> value when
   * it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    if (cstm != null)
      cstm.setBoolean(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given Java <code>byte</code>
   * value. The driver converts this to an SQL <code>TINYINT</code> value
   * when it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setByte(int parameterIndex, byte x) throws SQLException {
    if (cstm != null)
      cstm.setByte(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given Java <code>short</code>
   * value. The driver converts this to an SQL <code>SMALLINT</code> value
   * when it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setShort(int parameterIndex, short x) throws SQLException {
    if (cstm != null)
      cstm.setShort(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given Java <code>int</code> value.
   * The driver converts this to an SQL <code>INTEGER</code> value when it
   * sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setInt(int parameterIndex, int x) throws SQLException {
    if (cstm != null)
      cstm.setInt(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given Java <code>long</code>
   * value. The driver converts this to an SQL <code>BIGINT</code> value
   * when it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setLong(int parameterIndex, long x) throws SQLException {
    if (cstm != null)
      cstm.setLong(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given Java <code>float</code>
   * value. The driver converts this to an SQL <code>FLOAT</code> value when
   * it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setFloat(int parameterIndex, float x) throws SQLException {
    if (cstm != null)
      cstm.setFloat(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given Java <code>double</code>
   * value. The driver converts this to an SQL <code>DOUBLE</code> value
   * when it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setDouble(int parameterIndex, double x) throws SQLException {
    if (cstm != null)
      cstm.setDouble(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given
   * <code>java.math.BigDecimal</code> value. The driver converts this to an
   * SQL <code>NUMERIC</code> value when it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setBigDecimal(int parameterIndex, BigDecimal x)
      throws SQLException {
    if (cstm != null)
      cstm.setBigDecimal(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given Java <code>String</code>
   * value. The driver converts this to an SQL <code>VARCHAR</code> or
   * <code>LONGVARCHAR</code> value (depending on the argument's size
   * relative to the driver's limits on <code>VARCHAR</code> values) when it
   * sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setString(int parameterIndex, String x) throws SQLException {
    if (cstm != null)
      cstm.setString(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given Java array of bytes. The
   * driver converts this to an SQL <code>VARBINARY</code> or
   * <code>LONGVARBINARY</code> (depending on the argument's size relative
   * to the driver's limits on <code>VARBINARY</code> values) when it sends
   * it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setBytes(int parameterIndex, byte x[]) throws SQLException {
    if (cstm != null)
      cstm.setBytes(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given <code>java.sql.Date</code>
   * value. The driver converts this to an SQL <code>DATE</code> value when
   * it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setDate(int parameterIndex, java.sql.Date x)
      throws SQLException {
    if (cstm != null)
      cstm.setDate(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given <code>java.sql.Time</code>
   * value. The driver converts this to an SQL <code>TIME</code> value when
   * it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setTime(int parameterIndex, java.sql.Time x)
      throws SQLException {
    if (cstm != null)
      cstm.setTime(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given
   * <code>java.sql.Timestamp</code> value. The driver converts this to an
   * SQL <code>TIMESTAMP</code> value when it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setTimestamp(int parameterIndex, java.sql.Timestamp x)
      throws SQLException {
    if (cstm != null)
      cstm.setTimestamp(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given input stream, which will have
   * the specified number of bytes. When a very large ASCII value is input to
   * a <code>LONGVARCHAR</code> parameter, it may be more practical to send
   * it via a <code>java.io.InputStream</code>. Data will be read from the
   * stream as needed until end-of-file is reached. The JDBC driver will do
   * any necessary conversion from ASCII to the database char format.
   * 
   * <P>
   * <B>Note:</B> This stream object can either be a standard Java stream
   * object or your own subclass that implements the standard interface.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the Java input stream that contains the ASCII parameter value
   * @param length
   *            the number of bytes in the stream
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setAsciiStream(int parameterIndex, java.io.InputStream x,
      int length) throws SQLException {
    if (cstm != null)
      cstm.setAsciiStream(getParameterIndex(parameterIndex), x, length);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given input stream, which will have
   * the specified number of bytes. When a very large binary value is input to
   * a <code>LONGVARBINARY</code> parameter, it may be more practical to
   * send it via a <code>java.io.InputStream</code> object. The data will be
   * read from the stream as needed until end-of-file is reached.
   * 
   * <P>
   * <B>Note:</B> This stream object can either be a standard Java stream
   * object or your own subclass that implements the standard interface.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the java input stream which contains the binary parameter
   *            value
   * @param length
   *            the number of bytes in the stream
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setBinaryStream(int parameterIndex, java.io.InputStream x,
      int length) throws SQLException {
    if (cstm != null)
      cstm.setAsciiStream(getParameterIndex(parameterIndex), x, length);
    addParametersCount();
  };

  // ----------------------------------------------------------------------
  // Advanced features:

  /**
   * <p>
   * Sets the value of the designated parameter with the given object. The
   * second argument must be an object type; for integral values, the
   * <code>java.lang</code> equivalent objects should be used.
   * 
   * <p>
   * The given Java object will be converted to the given targetSqlType before
   * being sent to the database.
   * 
   * If the object has a custom mapping (is of a class implementing the
   * interface <code>SQLData</code>), the JDBC driver should call the
   * method <code>SQLData.writeSQL</code> to write it to the SQL data
   * stream. If, on the other hand, the object is of a class implementing
   * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>,
   * <code>Struct</code>, or <code>Array</code>, the driver should pass
   * it to the database as a value of the corresponding SQL type.
   * 
   * <p>
   * Note that this method may be used to pass database-specific abstract data
   * types.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the object containing the input parameter value
   * @param targetSqlType
   *            the SQL type (as defined in java.sql.Types) to be sent to the
   *            database. The scale argument may further qualify this type.
   * @param scale
   *            for java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types,
   *            this is the number of digits after the decimal point. For all
   *            other types, this value will be ignored.
   * @exception SQLException
   *                if a database access error occurs
   * @see Types
   */
  public void setObject(int parameterIndex, Object x, int targetSqlType,
      int scale) throws SQLException {
    if (cstm != null)
      cstm.setObject(getParameterIndex(parameterIndex), x, targetSqlType,
          scale);
    addParametersCount();
  };

  /**
   * Sets the value of the designated parameter with the given object. This
   * method is like the method <code>setObject</code> above, except that it
   * assumes a scale of zero.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the object containing the input parameter value
   * @param targetSqlType
   *            the SQL type (as defined in java.sql.Types) to be sent to the
   *            database
   * @exception SQLException
   *                if a database access error occurs
   */
  public void setObject(int parameterIndex, Object x, int targetSqlType)
      throws SQLException {
    if (cstm != null)
      cstm.setObject(getParameterIndex(parameterIndex), x, targetSqlType);
    addParametersCount();
  };

  /**
   * <p>
   * Sets the value of the designated parameter using the given object. The
   * second parameter must be of type <code>Object</code>; therefore, the
   * <code>java.lang</code> equivalent objects should be used for built-in
   * types.
   * 
   * <p>
   * The JDBC specification specifies a standard mapping from Java
   * <code>Object</code> types to SQL types. The given argument will be
   * converted to the corresponding SQL type before being sent to the
   * database.
   * 
   * <p>
   * Note that this method may be used to pass datatabase- specific abstract
   * data types, by using a driver-specific Java type.
   * 
   * If the object is of a class implementing the interface
   * <code>SQLData</code>, the JDBC driver should call the method
   * <code>SQLData.writeSQL</code> to write it to the SQL data stream. If,
   * on the other hand, the object is of a class implementing <code>Ref</code>,
   * <code>Blob</code>, <code>Clob</code>, <code>Struct</code>, or
   * <code>Array</code>, the driver should pass it to the database as a
   * value of the corresponding SQL type.
   * <P>
   * This method throws an exception if there is an ambiguity, for example, if
   * the object is of a class implementing more than one of the interfaces
   * named above.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the object containing the input parameter value
   * @exception SQLException
   *                if a database access error occurs or the type of the given
   *                object is ambiguous
   */
  public void setObject(int parameterIndex, Object x) throws SQLException {
    if (cstm != null)
      cstm.setObject(getParameterIndex(parameterIndex), x);
    addParametersCount();
  };

  // --------------------------JDBC 2.0-----------------------------

  /**
   * Sets the designated parameter to the given <code>Reader</code> object,
   * which is the given number of characters long. When a very large UNICODE
   * value is input to a <code>LONGVARCHAR</code> parameter, it may be more
   * practical to send it via a <code>java.io.Reader</code> object. The data
   * will be read from the stream as needed until end-of-file is reached. The
   * JDBC driver will do any necessary conversion from UNICODE to the database
   * char format.
   * 
   * <P>
   * <B>Note:</B> This stream object can either be a standard Java stream
   * object or your own subclass that implements the standard interface.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param reader
   *            the <code>java.io.Reader</code> object that contains the
   *            Unicode data
   * @param length
   *            the number of characters in the stream
   * @exception SQLException
   *                if a database access error occurs
   * @since 1.2
   */
  public void setCharacterStream(int parameterIndex, java.io.Reader reader,
      int length) throws SQLException {
    if (cstm != null)
      cstm.setCharacterStream(getParameterIndex(parameterIndex), reader,
          length);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given
   * <code>REF(&lt;structured-type&gt;)</code> value. The driver converts
   * this to an SQL <code>REF</code> value when it sends it to the database.
   * 
   * @param i
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            an SQL <code>REF</code> value
   * @exception SQLException
   *                if a database access error occurs
   * @since 1.2
   */
  public void setRef(int i, Ref x) throws SQLException {
    if (cstm != null)
      cstm.setRef(getParameterIndex(i), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given <code>Blob</code> object.
   * The driver converts this to an SQL <code>BLOB</code> value when it
   * sends it to the database.
   * 
   * @param i
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            a <code>Blob</code> object that maps an SQL
   *            <code>BLOB</code> value
   * @exception SQLException
   *                if a database access error occurs
   * @since 1.2
   */
  public void setBlob(int i, Blob x) throws SQLException {
    if (cstm != null)
      cstm.setBlob(getParameterIndex(i), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given <code>Clob</code> object.
   * The driver converts this to an SQL <code>CLOB</code> value when it
   * sends it to the database.
   * 
   * @param i
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            a <code>Clob</code> object that maps an SQL
   *            <code>CLOB</code> value
   * @exception SQLException
   *                if a database access error occurs
   * @since 1.2
   */
  public void setClob(int i, Clob x) throws SQLException {
    if (cstm != null)
      cstm.setClob(getParameterIndex(i), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given <code>Array</code> object.
   * The driver converts this to an SQL <code>ARRAY</code> value when it
   * sends it to the database.
   * 
   * @param i
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            an <code>Array</code> object that maps an SQL
   *            <code>ARRAY</code> value
   * @exception SQLException
   *                if a database access error occurs
   * @since 1.2
   */
  public void setArray(int i, Array x) throws SQLException {
    if (cstm != null)
      cstm.setArray(getParameterIndex(i), x);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given <code>java.sql.Date</code>
   * value, using the given <code>Calendar</code> object. The driver uses
   * the <code>Calendar</code> object to construct an SQL <code>DATE</code>
   * value, which the driver then sends to the database. With a
   * <code>Calendar</code> object, the driver can calculate the date taking
   * into account a custom timezone. If no <code>Calendar</code> object is
   * specified, the driver uses the default timezone, which is that of the
   * virtual machine running the application.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @param cal
   *            the <code>Calendar</code> object the driver will use to
   *            construct the date
   * @exception SQLException
   *                if a database access error occurs
   * @since 1.2
   */
  public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
      throws SQLException {
    if (cstm != null)
      cstm.setDate(getParameterIndex(parameterIndex), x, cal);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given <code>java.sql.Time</code>
   * value, using the given <code>Calendar</code> object. The driver uses
   * the <code>Calendar</code> object to construct an SQL <code>TIME</code>
   * value, which the driver then sends to the database. With a
   * <code>Calendar</code> object, the driver can calculate the time taking
   * into account a custom timezone. If no <code>Calendar</code> object is
   * specified, the driver uses the default timezone, which is that of the
   * virtual machine running the application.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @param cal
   *            the <code>Calendar</code> object the driver will use to
   *            construct the time
   * @exception SQLException
   *                if a database access error occurs
   * @since 1.2
   */
  public void setTime(int parameterIndex, java.sql.Time x, Calendar cal)
      throws SQLException {
    if (cstm != null)
      cstm.setTime(getParameterIndex(parameterIndex), x, cal);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to the given
   * <code>java.sql.Timestamp</code> value, using the given
   * <code>Calendar</code> object. The driver uses the <code>Calendar</code>
   * object to construct an SQL <code>TIMESTAMP</code> value, which the
   * driver then sends to the database. With a <code>Calendar</code> object,
   * the driver can calculate the timestamp taking into account a custom
   * timezone. If no <code>Calendar</code> object is specified, the driver
   * uses the default timezone, which is that of the virtual machine running
   * the application.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the parameter value
   * @param cal
   *            the <code>Calendar</code> object the driver will use to
   *            construct the timestamp
   * @exception SQLException
   *                if a database access error occurs
   * @since 1.2
   */
  public void setTimestamp(int parameterIndex, java.sql.Timestamp x,
      Calendar cal) throws SQLException {
    if (cstm != null)
      cstm.setTimestamp(getParameterIndex(parameterIndex), x, cal);
    addParametersCount();
  };

  /**
   * Sets the designated parameter to SQL <code>NULL</code>. This version
   * of the method <code>setNull</code> should be used for user-defined
   * types and REF type parameters. Examples of user-defined types include:
   * STRUCT, DISTINCT, JAVA_OBJECT, and named array types.
   * 
   * <P>
   * <B>Note:</B> To be portable, applications must give the SQL type code
   * and the fully-qualified SQL type name when specifying a NULL user-defined
   * or REF parameter. In the case of a user-defined type the name is the type
   * name of the parameter itself. For a REF parameter, the name is the type
   * name of the referenced type. If a JDBC driver does not need the type code
   * or type name information, it may ignore it.
   * 
   * Although it is intended for user-defined and Ref parameters, this method
   * may be used to set a null parameter of any JDBC type. If the parameter
   * does not have a user-defined or REF type, the given typeName is ignored.
   * 
   * 
   * @param paramIndex
   *            the first parameter is 1, the second is 2, ...
   * @param sqlType
   *            a value from <code>java.sql.Types</code>
   * @param typeName
   *            the fully-qualified name of an SQL user-defined type; ignored
   *            if the parameter is not a user-defined type or REF
   * @exception SQLException
   *                if a database access error occurs
   * @since 1.2
   */
  public void setNull(int paramIndex, int sqlType, String typeName)
      throws SQLException {
    if (cstm != null)
      cstm.setNull(getParameterIndex(paramIndex), sqlType, typeName);
    addParametersCount();
  };

  // ------------------------- JDBC 3.0 -----------------------------------

  /**
   * Sets the designated parameter to the given <code>java.net.URL</code>
   * value. The driver converts this to an SQL <code>DATALINK</code> value
   * when it sends it to the database.
   * 
   * @param parameterIndex
   *            the first parameter is 1, the second is 2, ...
   * @param x
   *            the <code>java.net.URL</code> object to be set
   * @exception SQLException
   *                if a database access error occurs
   * @since 1.4
   */
  public void setURL(int parameterIndex, java.net.URL x) throws SQLException {
    if (cstm != null)
      cstm.setURL(getParameterIndex(parameterIndex), x);
    addParametersCount();
  }

}

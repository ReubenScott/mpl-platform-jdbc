package com.soak.jdbcframe.jdbc.util;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generic utility methods for working with JDBC. Mainly for internal use within
 * the framework, but also useful for custom JDBC access code.
 * 
 * @author Thomas Risberg
 * @author Juergen Hoeller
 */
public abstract class JdbcUtils {
	private static final Log logger = LogFactory.getLog(JdbcUtils.class);

	/**
	 * Close the given JDBC Connection and ignore any thrown exception. This is
	 * useful for typical finally blocks in manual JDBC code.
	 * 
	 * @param con
	 *            the JDBC Connection to close (may be <code>null</code>)
	 */
	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
				logger.debug("Could not close JDBC Connection", ex);
			} catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw
				// RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC Connection",
						ex);
			}
		}
	}

	/**
	 * Close the given JDBC Statement and ignore any thrown exception. This is
	 * useful for typical finally blocks in manual JDBC code.
	 * 
	 * @param stmt
	 *            the JDBC Statement to close (may be <code>null</code>)
	 */
	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				logger.debug("Could not close JDBC Statement", ex);
			} catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw
				// RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC Statement",
						ex);
			}
		}
	}

	/**
	 * Close the given JDBC ResultSet and ignore any thrown exception. This is
	 * useful for typical finally blocks in manual JDBC code.
	 * 
	 * @param rs
	 *            the JDBC ResultSet to close (may be <code>null</code>)
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				logger.debug("Could not close JDBC ResultSet", ex);
			} catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw
				// RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC ResultSet",
						ex);
			}
		}
	}

	/**
	 * Retrieve a JDBC column value from a ResultSet, using the most appropriate
	 * value type. The returned value should be a detached value object, not
	 * having any ties to the active ResultSet: in particular, it should not be
	 * a Blob or Clob object but rather a byte array respectively String
	 * representation.
	 * <p>
	 * Uses the <code>getObject(index)</code> method, but includes additional
	 * "hacks" to get around Oracle 10g returning a non-standard object for its
	 * TIMESTAMP datatype and a <code>java.sql.Date</code> for DATE columns
	 * leaving out the time portion: These columns will explicitly be extracted
	 * as standard <code>java.sql.Timestamp</code> object.
	 * 
	 * @param rs
	 *            is the ResultSet holding the data
	 * @param index
	 *            is the column index
	 * @return the value object
	 * @throws SQLException
	 *             if thrown by the JDBC API
	 * @see java.sql.Blob
	 * @see java.sql.Clob
	 * @see java.sql.Timestamp
	 */
	public static Object getResultSetValue(ResultSet rs, int index)
			throws SQLException {
		Object obj = rs.getObject(index);
		if (obj instanceof Blob) {
			obj = rs.getBytes(index);
		} else if (obj instanceof Clob) {
			obj = rs.getString(index);
		} else if (obj != null
				&& obj.getClass().getName().startsWith("oracle.sql.TIMESTAMP")) {
			obj = rs.getTimestamp(index);
		} else if (obj != null
				&& obj.getClass().getName().startsWith("oracle.sql.DATE")) {
			String metaDataClassName = rs.getMetaData().getColumnClassName(
					index);
			if ("java.sql.Timestamp".equals(metaDataClassName)
					|| "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
				obj = rs.getTimestamp(index);
			} else {
				obj = rs.getDate(index);
			}
		} else if (obj != null && obj instanceof java.sql.Date) {
			if ("java.sql.Timestamp".equals(rs.getMetaData()
					.getColumnClassName(index))) {
				obj = rs.getTimestamp(index);
			}
		}
		return obj;
	}

	public static Object getCallableStatementValue(CallableStatement cstm,
			int index) throws SQLException {
		Object obj = cstm.getObject(index);
		if (obj instanceof Blob) {
			obj = cstm.getBytes(index);
		} else if (obj instanceof Clob) {
			obj = cstm.getString(index);
		} else if (obj != null
				&& obj.getClass().getName().startsWith("oracle.sql.TIMESTAMP")) {
			obj = cstm.getTimestamp(index);
		} else if (obj != null
				&& obj.getClass().getName().startsWith("oracle.sql.DATE")) {
			String metaDataClassName = cstm.getMetaData().getColumnClassName(
					index);
			if ("java.sql.Timestamp".equals(metaDataClassName)
					|| "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
				obj = cstm.getTimestamp(index);
			} else {
				obj = cstm.getDate(index);
			}
		} else if (obj != null && obj instanceof java.sql.Date) {
			if ("java.sql.Timestamp".equals(cstm.getMetaData()
					.getColumnClassName(index))) {
				obj = cstm.getTimestamp(index);
			}
		}
		return obj;
	}

	public static Object getCallableStatementValue(CallableStatement cstm,
			String name) throws SQLException {
		Object obj = cstm.getObject(name);
		if (obj instanceof Blob) {
			obj = cstm.getBytes(name);
		} else if (obj instanceof Clob) {
			obj = cstm.getString(name);
		} else if (obj != null) {
			if (obj instanceof java.sql.Timestamp
					|| "java.sql.Timestamp".equals(obj.getClass().getName())) {
				obj = cstm.getTimestamp(name);
			} else if (obj.getClass().getName().startsWith(
					"oracle.sql.TIMESTAMP")) {
				obj = cstm.getTimestamp(name);
			} else if (obj instanceof java.sql.Date
					|| "java.sql.Date".equals(obj.getClass().getName())) {
				obj = cstm.getDate(name);
			}
		}
		return obj;
	}

	public static Object getResultSetValue(ResultSet rs, String name)
			throws SQLException {
		Object obj = rs.getObject(name);
		if (obj instanceof Blob) {
			obj = rs.getBytes(name);
		} else if (obj instanceof Clob) {
			obj = rs.getString(name);
		} else if (obj != null) {
			if (obj instanceof java.sql.Timestamp
					|| "java.sql.Timestamp".equals(obj.getClass().getName())) {
				obj = rs.getTimestamp(name);
			} else if (obj.getClass().getName().startsWith(
					"oracle.sql.TIMESTAMP")) {
				obj = rs.getTimestamp(name);
			} else if (obj instanceof java.sql.Date
					|| "java.sql.Date".equals(obj.getClass().getName())) {
				obj = rs.getDate(name);
			}
		}
		return obj;
	}

	public static void setParameters(PreparedStatement pstmt, List parameters)
			throws SQLException {
		for (int i = 1, size = parameters.size(); i <= size; i++) {
			Object value = parameters.get(i - 1);
			if (value instanceof String) {
				pstmt.setString(i, (String) value);
			} else if (value instanceof Integer) {
				pstmt.setInt(i, ((Integer) value).intValue());
			} else if (value instanceof Long) {
				pstmt.setLong(i, ((Long) value).longValue());
			} else if (value instanceof Double) {
				pstmt.setDouble(i, ((Double) value).doubleValue());
			} else if (value instanceof Float) {
				pstmt.setFloat(i, ((Float) value).floatValue());
			} else if (value instanceof Short) {
				pstmt.setShort(i, ((Short) value).shortValue());
			} else if (value instanceof Byte) {
				pstmt.setByte(i, ((Byte) value).byteValue());
			} else if (value instanceof BigDecimal) {
				pstmt.setBigDecimal(i, (BigDecimal) value);
			} else if (value instanceof Boolean) {
				pstmt.setBoolean(i, ((Boolean) value).booleanValue());
			} else if (value instanceof Timestamp) {
				pstmt.setTimestamp(i, (Timestamp) value);
			} else if (value instanceof java.util.Date) {
				pstmt.setDate(i, new java.sql.Date(((java.util.Date) value)
						.getTime()));
			} else if (value instanceof java.sql.Date) {
				pstmt.setDate(i, (java.sql.Date) value);
			} else if (value instanceof Time) {
				pstmt.setTime(i, (Time) value);
			} else if (value instanceof Blob) {
				pstmt.setBlob(i, (Blob) value);
			} else if (value instanceof Clob) {
				pstmt.setClob(i, (Clob) value);
			} else {
				pstmt.setObject(i, value);
			}
		}
	}

	/**
	 * Return whether the given JDBC driver supports JDBC 2.0 batch updates.
	 * <p>
	 * Typically invoked right before execution of a given set of statements: to
	 * decide whether the set of SQL statements should be executed through the
	 * JDBC 2.0 batch mechanism or simply in a traditional one-by-one fashion.
	 * <p>
	 * Logs a warning if the "supportsBatchUpdates" methods throws an exception
	 * and simply returns <code>false</code> in that case.
	 * 
	 * @param con
	 *            the Connection to check
	 * @return whether JDBC 2.0 batch updates are supported
	 * @see java.sql.DatabaseMetaData#supportsBatchUpdates()
	 */
	public static boolean supportsBatchUpdates(Connection con) {
		try {
			DatabaseMetaData dbmd = con.getMetaData();
			if (dbmd != null) {
				if (dbmd.supportsBatchUpdates()) {
					logger.debug("JDBC driver supports batch updates");
					return true;
				} else {
					logger.debug("JDBC driver does not support batch updates");
				}
			}
		} catch (SQLException ex) {
			logger
					.debug(
							"JDBC driver 'supportsBatchUpdates' method threw exception",
							ex);
		} catch (AbstractMethodError err) {
			logger
					.debug(
							"JDBC driver does not support JDBC 2.0 'supportsBatchUpdates' method",
							err);
		}
		return false;
	}

	/**
	 * Check whether the given SQL type is numeric.
	 * 
	 * @param sqlType
	 *            the SQL type to be checked
	 * @return whether the type is numeric
	 */
	public static boolean isNumeric(int sqlType) {
		return Types.BIT == sqlType || Types.BIGINT == sqlType
				|| Types.DECIMAL == sqlType || Types.DOUBLE == sqlType
				|| Types.FLOAT == sqlType || Types.INTEGER == sqlType
				|| Types.NUMERIC == sqlType || Types.REAL == sqlType
				|| Types.SMALLINT == sqlType || Types.TINYINT == sqlType;
	}

}

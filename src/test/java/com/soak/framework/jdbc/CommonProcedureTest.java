package com.soak.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import junit.framework.TestCase;

import com.soak.jdbcframe.jdbc.core.ProcedureCallBack;
import com.soak.jdbcframe.jdbc.core.ProcedureResult;
import com.soak.jdbcframe.jdbc.core.SimpleJdbcTemplate;

/**
 * <p>
 * Title:
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
 * @author ���ڼ�
 * @main sunyujia@yahoo.cn
 * @date May 17, 2008 10:05:56 PM
 */
public abstract class CommonProcedureTest extends TestCase {

	public void test1() {
		ProcedureResult result = getSimpleJdbcTemplate().execProcedure("p1",
				new ProcedureCallBack() {
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getObject(1);
					}

					public void registerParameter() throws SQLException {
						registerOutParameter(1, Types.INTEGER);
						setInt(2, 9);
						addOracleCursor(3);
						addOracleCursor(4);
					}
				});
//		TestUtil.println(result);
	}

	public void test2() {
		ProcedureResult result = getSimpleJdbcTemplate().execProcedure("p2",
				Types.INTEGER, new ProcedureCallBack() {
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getObject(1);
					}

					public void registerParameter() throws SQLException {
						registerOutParameter(1, Types.INTEGER);
						setInt(2, 9);
						addOracleCursor(3);
					}

				});
//		TestUtil.println(result);
	}

	public void test3() {
		ProcedureResult result = getSimpleJdbcTemplate().execProcedure("p3",
				new ProcedureCallBack() {
					public void registerParameter() throws SQLException {
						registerOutParameter(1, Types.INTEGER);
						setInt(2, 9);
						addOracleCursor(3);
					}

					public Object mapRow(ResultSet rs, int rsIndex)
							throws SQLException {
						return rs.getObject(1);
					}
				});
//		TestUtil.println(result);
	}

	public void test4() {
		ProcedureResult result = getSimpleJdbcTemplate().execProcedure("p4",
				new ProcedureCallBack() {
				});
//		TestUtil.println(result);
	}

	public abstract SimpleJdbcTemplate getSimpleJdbcTemplate();
}

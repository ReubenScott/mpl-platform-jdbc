package com.soak.framework.jdbc;

import com.soak.framework.jdbc.core.SimpleJdbcTemplate;

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
 * @date May 17, 2008 10:42:04 PM
 */
public class TestUtil {
	private static SimpleJdbcTemplate oracleJt = createJdbcTemplateOracle();
	private static SimpleJdbcTemplate mssqlJt = createJdbcTemplateMssql();



	public static SimpleJdbcTemplate createJdbcTemplateMssql() {
		SimpleJdbcTemplate jt = new SimpleJdbcTemplate();
		jt.setDriver("com.microsoft.jdbc.sqlserver.SQLServerDriver");
		jt
				.setUrl("jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=test");
		jt.setUserName("sa");
		jt.setPassWord("sa");
		jt.setDbType(SimpleJdbcTemplate.MSSQL);
		return jt;
	}

	public static SimpleJdbcTemplate createJdbcTemplateOracle() {
		SimpleJdbcTemplate jt = new SimpleJdbcTemplate();
		jt.setDriver("oracle.jdbc.driver.OracleDriver");
		jt.setUrl("jdbc:oracle:thin:@localhost:1521:test");
		jt.setUserName("sa");
		jt.setPassWord("sa");
		jt.setDbType(SimpleJdbcTemplate.ORACLE);
		return jt;
	}

	public static SimpleJdbcTemplate getOracleJt() {
		return oracleJt;
	}

	public static void setOracleJt(SimpleJdbcTemplate oracleJt) {
		TestUtil.oracleJt = oracleJt;
	}

	public static SimpleJdbcTemplate getMssqlJt() {
		return mssqlJt;
	}

	public static void setMssqlJt(SimpleJdbcTemplate mssqlJt) {
		TestUtil.mssqlJt = mssqlJt;
	}

}

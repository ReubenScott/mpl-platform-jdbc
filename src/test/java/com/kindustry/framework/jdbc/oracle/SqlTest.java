package com.kindustry.framework.jdbc.oracle;


import junit.framework.TestCase;


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
public class SqlTest extends TestCase {
	public void test0() {
		String sql = "select 'syj1' as id from dual union all select 'syj2' as id from dual ";
//		List list = TestUtil.getOracleJt().executeQuery(sql);
//		List list1 = TestUtil.getOracleJt().executeQuery(sql, new RowMapper() {
//			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
//				return rs.getObject(1);
//			}
//		});
	}
}

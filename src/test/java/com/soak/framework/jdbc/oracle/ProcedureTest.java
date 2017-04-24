package com.soak.framework.jdbc.oracle;

import com.soak.framework.jdbc.CommonProcedureTest;
import com.soak.framework.jdbc.TestUtil;
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
 * @date May 17, 2008 10:05:56 PM
 */
public class ProcedureTest extends CommonProcedureTest {

	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return TestUtil.getOracleJt();
	}

}

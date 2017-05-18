package com.soak.daily;

import org.junit.Before;
import org.junit.Test;

import com.soak.framework.jdbc.core.JdbcTemplate;

public class DailyDataTaskTest {

  JdbcTemplate jdbc;

  @Before
  public void setUp() {
    jdbc = JdbcTemplate.getInstance();
  }

  @Test
  public void testInsert() throws Exception {
    // 导入数据     
//    jdbc.loadExcelFile("sche", "hkbyrdkhd", "E:/启东农商银行/报表/财务部/20170517/户口本与个人贷款核对明细.xlsx");

    jdbc.loadExcelFile("sche", "sfzscmd", "E:/启东农商银行/报表/财务部/20170517/3.最终同名一二代身份证和身份证录入错误筛查名单.xlsx");
  }

}

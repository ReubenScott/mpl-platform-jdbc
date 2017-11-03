package com.kindustry.daily;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.kindustry.framework.jdbc.core.JdbcTemplate;
import com.kindustry.framework.jdbc.core.RowMapper;

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
public class QDNSHDailyTest {

  JdbcTemplate jdbc;

  @Before
  public void setUp() {
    jdbc = JdbcTemplate.getInstance();
  }

  @Test
  public void testLoadExcel() {
    // 导入数据
    String schema = "SCHE";
    // String tablename = "YG_BUTONGMING5Z10" ;
    // String filepath = "E:/启东农商银行/报表/账户情况行长考核/附件2：同证件不同名5至10账号.xlsx" ;

    // String tablename = "YG_TONGMING5Z10" ;
    // String filepath = "E:/启东农商银行/报表/账户情况行长考核/附件3：同证件同名5至10账号.xlsx" ;

    // String tablename = "YG_TONGZJ10" ;
    // String filepath = "E:/启东农商银行/报表/账户情况行长考核/附件1：10户以上账户数客户明细新.xlsx" ;

    String tablename = "UTEST";
    String filepath = "E:/Downloads/企业关键人信息样表.xlsx";
    
    jdbc.loadExcelFile(schema, tablename, filepath);
  }
  
  
  
  
  
}

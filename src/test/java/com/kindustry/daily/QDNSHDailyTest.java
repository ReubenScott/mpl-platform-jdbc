package com.kindustry.daily;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;

import com.kindustry.common.date.DateUtil;
import com.kindustry.framework.jdbc.core.JdbcTemplate;
import com.kindustry.framework.xml.XmlSqlMapper;

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
  public void testExecute() {
    Date startDate = DateUtil.getShortDate("2017-01-01") ;
    Date endDate = DateUtil.getShortDate("2017-11-21") ;
    
    int interval =  1 ;
    Date eachDate =  startDate ;
    
    while( DateUtil.isBefore(eachDate, endDate) ){
      Date monEnd = DateUtil.getLastDayOfMonth(eachDate) ;
      
      if(DateUtil.isSameDay(eachDate, monEnd) ){
        System.out.println(DateUtil.formatShortDate(eachDate));
      } else {
        String sql =  "delete from S_DEP_TD_ACCOUNT_D where statdate = '" + DateUtil.formatShortDate(eachDate) + "' ";
        System.out.println(sql);
        jdbc.execute(sql); 
      }

      eachDate =  DateUtil.addDays(eachDate, interval);
    }
    
  }



//  @Test
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

//  @Test
  public void testExportExcelBySQL() {
    System.out.printf("JOB OUTPUT: %tF %<tT%n", System.currentTimeMillis());
    // String sql = "select * from edw.CBOD_ECCMRAMR";

    String sql = XmlSqlMapper.getInstance().getPreparedSQL("大额存单产品分类合计");
    sql = sql.replaceAll("@statdate", "2017-11-22");
//    sql = sql.replaceAll("@startDate", "2015-11-21");
//    sql = sql.replaceAll("@endDate", "2015-11-30");

    System.out.println(sql);

    Workbook workbook = JdbcTemplate.getInstance().exportNamelessWorkbook(sql);

    FileOutputStream fos;
    try {
      fos = new FileOutputStream(new File("D:/workspace/20171122.xlsx"));
      workbook.write(fos);
      fos.flush();
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}

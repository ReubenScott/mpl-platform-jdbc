package com.soak.framework.jdbc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class JdbcTemplateTest {

  JdbcHandler jdbcHandler;

  @Before
  public void setUp() throws Exception {
    jdbcHandler = jdbcHandler.getInstance();
  }

  // @Test
  public void testGetSchema() {
//    String schema = jdbcHandler.getSchem("");
//    System.out.println(schema);
  }

  // @Test
  public void testQueryForList() {
    String sql = "select * from etl.systempara ";
    List list = jdbcHandler.queryForList("",sql);
    for (Object obj : list) {
      System.out.println(obj.toString());
    }
  }
  
  /**
   * 第一步 导入打卡记录
   * 
   * @param filePath
   */
  @Test
  public void testloadPunchRecord() {
    String dirPath= "E:/考勤/刷卡记录/";
    File dir = new File(dirPath);
    if (dir.exists() && dir.isDirectory()) {
      File files[] = dir.listFiles();
      for (File file : files) {
        String filePath = file.getAbsolutePath();
        System.out.println(filePath);
//        jdbcHandler.truncateTable("",null, "atnd_punch_record");
        // 导入打卡记录
        jdbcHandler.loadExcelFile("","","atnd_punch_record", filePath);
        // 打卡记录 合并
        jdbcHandler.callProcedure("","sp_f_atnd_punch_record", new Object[] { 1 });
      }
    }
    
    // 更新打卡记录  没有员工号的部分数据
    jdbcHandler.execute("","UPDATE f_atnd_punch_record AS t1 LEFT JOIN f_emp_info T2 ON t1.empname = T2.empname SET t1.empno = T2.empno WHERE t1.empno IS  NULL");
    
  }

//   @Test
  public void testLoadDelFile() {
//    jdbcHandler.loadDelFile("edw","CBOD_GLGLGHTD", "E:/ftpdata/P_063_CBOD_GLGLGHTD_20150701.del", (char) 29);
//    jdbcHandler.loadDelFile("edw","IND_REPORT_DATA", "E:/ftpdata/P_063_CMIS_IND_REPORT_DATA_20150708.del", (char)29 );
  }

//  @Test
  public void testLoadCVS() {
//    jdbcHandler.loadCsvFile("EDW","YKJD_CUST_ENT", "D:/home/20160318/CUST_ENT.del", (char) 44);
    jdbcHandler.loadCsvFile("","etl","edw_tabdellist", "E:/启东农商银行/二期/ETL_92/init/data/ETL.EDW_TABDELLIST.del", (char)44 );
//    jdbcHandler.loadCsvFile("YKJD_CUST_ENT", "D:/home/20160318/CUST_ENT.del", ',');
  }


//   @Test
  public void testLoadExcelFile() {
    // Date date1 = new Date();//获取当前时间
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // String str = sdf.format(date1);//时间存储为字符串
    // System.out.println(str);
    // Timestamp.valueOf(str);//转换时间字符串为Timestamp
    // System.out.println(Timestamp.valueOf(str));//输出结果
    jdbcHandler.loadExcelFile("","","attendance_record", "E:/考勤/打卡记录.xlsx");
    // etlJobImpl.loadExcelFile("attendance_record", "E:/考勤/详细刷卡记录2016年2月份.xls");
  }
  
  

}

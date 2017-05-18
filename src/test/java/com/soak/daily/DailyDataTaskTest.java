package com.soak.daily;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.soak.common.io.CSVUtils;
import com.soak.framework.jdbc.core.JdbcTemplate;

public class DailyDataTaskTest {

  JdbcTemplate jdbc;

  @Before
  public void setUp() {
    jdbc = JdbcTemplate.getInstance();
  }

  public void testInsert() throws Exception {
    // 导入数据
    // jdbc.loadExcelFile("sche", "hkbyrdkhd", "E:/启东农商银行/报表/财务部/20170517/户口本与个人贷款核对明细.xlsx");

    jdbc.loadExcelFile("sche", "sfzscmd", "E:/启东农商银行/报表/财务部/20170517/3.最终同名一二代身份证和身份证录入错误筛查名单.xlsx");
  }

  @Test
  public void testSqlQuery() {
    String sqlQuery = "select * from sche.sfzscmd";

    List<List> result = jdbc.queryForList(sqlQuery);
    
    List<String[]>  arrays = new ArrayList<String[]>();

    int size = result.size();
    for (int i = 0; i < size; i++) {
      List<String> row = result.get(i);
      String cid = row.get(0).trim(); // 客户号
      String name = row.get(1).trim(); // 客户姓名
      String certtype = row.get(2).trim(); // 证件类型
      String certId = row.get(3).trim(); // 证件号
      

      String[] line = new String[5];
      line[0] = cid ;
      line[1] = name ;
      line[2] = certtype ;
      line[3] = certId ;
      
      boolean flag = false;
      
      for (int j = 0; j < size; j++) {
        if(i==j){
          continue ;
        } else {
          List<String> row2 =  result.get(j);
          String cid2 = row2.get(0).trim(); // 客户号
          String name2 = row2.get(1).trim(); // 客户姓名
          String certtype2 = row2.get(2).trim(); // 证件号
          String certId2 = row2.get(3).trim(); // 证件号
          int len2 = certId.length() ;  // 证件位数
          
          if(name.equals(name2)){
            if(isSame(certId, certId2.trim())){
              flag = true ;
              break ;
            }
          }
          
        }
      }
      
      line[4] = flag? "是" : "否" ;

      arrays.add(line);
    }
    

    CSVUtils.exportCSV("d:/aa.csv", arrays);

  }

  public boolean isSame(String certId1 , String certId2){
    boolean flag = false ;
    int len1 = certId1.length() ;  // 证件位数
    int len2 = certId2.length() ;  // 证件位数
    int max =  Math.max(len1, len2);
    
    String id1 = certId1 ;
    String id2  = certId2 ;
    
    if(len1!=max){
      id1 = certId2 ;
      id2 = certId1 ;
    }
    
    if(len1 == len2){
      flag = certId1.equals(certId2);
    } else {
      
      switch(max){
        case 18 :
          String prefix = id1.substring(0, 6);
          String end = id1.substring(8, 17);
          flag = id2.equals(prefix+end);
          break ;
        case 15 :
          System.out.println("111111111111111111111111");
//          System.out.println(id1);
//          System.out.println(id2);
          break ;
        default :
          System.out.println("2222222222222222222222222");
//          System.out.println(id1);
//          System.out.println(id2);
      }
    }
    
    
    
    return flag ;
  }
  
}

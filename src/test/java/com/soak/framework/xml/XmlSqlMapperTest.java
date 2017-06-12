package com.soak.framework.xml;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;

import com.soak.common.constant.CharSetType;
import com.soak.framework.jdbc.core.JdbcTemplate;
import com.soak.framework.xml.XmlSqlMapper;

public class XmlSqlMapperTest {

  XmlSqlMapper xmlSqlMapper;
  JdbcTemplate jdbcHandler;

  @Before
  public void setUp() throws Exception {
    jdbcHandler = JdbcTemplate.getInstance();
  }

//  @Test
  public void testGetSQL() {
    for (int i = 0; i < 25 ; i++) {
      String sql = XmlSqlMapper.getInstance().getPreparedSQL("证件");
      sql = sql.replaceAll("@count", i+"");
      System.out.println(sql);
      Workbook workbook = JdbcTemplate.getInstance().exportNamelessWorkbook(sql);
      FileOutputStream out = null;

      try {
        out = new FileOutputStream("E:/Ghost/2017/启东"+(i+1)+".xlsx");
        workbook.write(out);
        out.flush();
        out.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

  }

  public void testAddHashtable() {
    String sql = XmlSqlMapper.getInstance().getPreparedSQL("证件");
    jdbcHandler.exportCSV("E:/export/jngrkhckmx.csv",CharSetType.UTF8 , '|', '\0' , sql);
  }

  @Test
  public void testExportData() {
//    String sql = XmlSqlMapper.getInstance().getPreparedSQL("零售客户信息");
//    jdbcHandler.exportCSV("E:/export/零售客户信息.csv",CharSetType.GBK , ',', '\0' , sql);

    String sql = XmlSqlMapper.getInstance().getPreparedSQL("对公客户信息");
    jdbcHandler.exportCSV("E:/export/对公客户信息.csv",CharSetType.GBK , ',', '\0' , sql);
    

//    String sql = XmlSqlMapper.getInstance().getPreparedSQL("个体工商户");
//    jdbcHandler.exportCSV("E:/export/个体工商户.csv",CharSetType.GBK , ',', '\0' , sql);
    
    
  }
}

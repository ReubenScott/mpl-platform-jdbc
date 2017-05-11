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

import com.soak.framework.jdbc.core.JdbcTemplate;
import com.soak.framework.xml.XmlSqlMapper;

public class XmlSqlMapperTest {

  XmlSqlMapper xmlSqlMapper;

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetSQL() {
    for (int i = 0; i < 25 ; i++) {
      String sql = XmlSqlMapper.getInstance().getPreparedSQL("证件");
      sql = sql.replaceAll("@count", i+"");
      System.out.println(sql);
      Workbook workbook = JdbcTemplate.getInstance().exportExcel(sql);
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

  @Test
  public void testAddHashtable() {
  }

  @Test
  public void testGetHashtable() {
  }
}

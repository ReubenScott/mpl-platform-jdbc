package com.soak.framework.xml;

import static org.junit.Assert.*;

import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;


public class XmlSqlMapperTest {

  
  XmlSqlMapper xmlSqlMapper ;
  
  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetSQL() {
    String sql =  XmlSqlMapper.getInstance().getPreparedSQL("每旬理财");
    sql = sql.replaceAll("@data", "2015-12-10");
    System.out.println(sql);
  }

  @Test
  public void testAddHashtable() {
  }

  @Test
  public void testGetHashtable() {
  }
}

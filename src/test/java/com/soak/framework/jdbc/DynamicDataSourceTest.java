package com.soak.framework.jdbc;


import org.junit.Before;
import org.junit.Test;


public class DynamicDataSourceTest {

  DynamicDataSource dataSource ;;

  @Before
  public void setUp() throws Exception {
    dataSource = DynamicDataSource.getInstance();
  }

  @Test
  public void testInsert() throws Exception {
    System.out.println(123123123);
  }
  
  
  
  
}

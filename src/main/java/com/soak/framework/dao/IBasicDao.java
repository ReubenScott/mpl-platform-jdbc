package com.soak.framework.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.soak.framework.jdbc.Restrictions;

/**
 */
public interface IBasicDao {
  
  public <T> List<T> findByAnnotatedSample(T annotatedSample , Restrictions... restrictions );
  
  public <T> List<T> querySampleList(Class<T> sample, String sql, Object... params) ;
  
  public List<List> queryForList(String sql, Object... params);

  public List findByXmlSqlMapper(String dbalias , String sqlName, String value);

  public Workbook exportExcel(String dbalias , String title, String sql, Object... params);

  public HashMap queryOneAsMap(String sql, Object... params);
  
  public HashMap queryOneAsMap(String dbalias, String sql, Object... params);
  
  public void exportCSV(String filePath , String encoding, char split, String sql, Object... params);
  
  public void exportCSV(String dbalias , String filePath , String encoding, char split, String sql, Object... params);
  
  public boolean saveAnnotatedBean(Object annoBean);
  
  public boolean saveAnnotatedBean(List<?> annoBeans);
  
  public boolean truncateTable(String schema, String tablename);
  
  public void loadExcelFile(String tablename, String filePath) ;
  
  public List callProcedure(String procedureName, Object[] in , int... outTypes) ;
  
  public int execute(String sql, Object... params);

  public boolean deleteAnnotatedEntity(Object annoEntity);
  
}

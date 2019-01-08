package com.kindustry.framework.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.kindustry.framework.jdbc.Restrictions;
import com.kindustry.framework.jdbc.support.Pagination;

/**
 */
public interface IBasicDao {

  public <T> List<T> findByAnnotatedSample(T annotatedSample , Restrictions... restrictions );
  
  public <T> List<T> querySampleList(Class<T> sample, String sql, Object... params) ;
  
  public List<List> queryForList(String sql, Object... params);

  public List findByXmlSqlMapper(String dbalias , String sqlName, String value);

  public Workbook exportExcel(String sql, Object... params);

  public HashMap queryOneAsMap(String sql, Object... params);
  
  public void exportCSV(String filePath , String encoding, char split, String sql, Object... params);
  
  public boolean updateNullunableEntity(Object annoEntity);
  
//  public boolean updateAnnotatedBean(Object annoEntity);
  
  public boolean saveAnnotatedEntity(Object... annoEntity);
  
  public boolean saveAnnotatedEntity(List<?> annoEntities);
  
  public boolean truncateTable(String schema, String tablename);
  
  public void loadExcelFile(String tablename, String filePath) ;
  
  public List callProcedure(String procedureName, Object[] in , int... outTypes) ;
  
  public int execute(String sql, Object... params);
  
  public boolean deleteEntityBySID(Class entity, Serializable sid);
  
  public boolean deleteAnnotatedEntity(Object annoEntity);

  public Pagination queryPageBySQL(String sql, final int startIndex, final int pageSize, Object... params) ;

  public Pagination querySamplePageBySQL(Class sample, String sql, final int startIndex, final int pageSize, Object... params);
  
}

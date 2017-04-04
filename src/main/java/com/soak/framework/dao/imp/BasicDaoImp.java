package com.soak.framework.dao.imp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soak.framework.dao.IBasicDao;
import com.soak.framework.jdbc.JdbcHandler;
import com.soak.framework.jdbc.Restrictions;
import com.soak.framework.util.BeanUtil;
import com.soak.framework.xml.XmlSqlMapper;

/**
 * 
 */
public class BasicDaoImp implements IBasicDao {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  protected JdbcHandler jdbcHandler = JdbcHandler.getInstance();

  public JdbcHandler getJdbcHandler() {
    return jdbcHandler;
  }

  public void setJdbcHandler(JdbcHandler jdbcHandler) {
    this.jdbcHandler = jdbcHandler;
  }

  
  
  public int execute(String sql, Object... params) {
    return jdbcHandler.executeUpdate(null, sql, params);
  }
  
  
  public <T> List<T> querySampleList(Class<T> sample, String sql, Object... params) {
    return jdbcHandler.querySampleList(null, sample, sql, params);
  }
  
  public List<List> queryForList(String sql, Object... params) {
    return jdbcHandler.queryForList(null, sql, params);
  }
  
  /**
   * 
   */
  public <T> List<T> findByAnnotatedSample(T annotatedSample, Restrictions... restrictions) {
    return jdbcHandler.findByAnnotatedSample(null, annotatedSample, restrictions);
  }

  public List findByXmlSqlMapper(String dbalias, String sqlName, String value) {
    String sql = XmlSqlMapper.getInstance().getPreparedSQL(sqlName);
    sql = sql.replaceAll("@date", value);
    logger.debug(sql);
    return jdbcHandler.queryForList(dbalias, sql);
  }

  public Workbook exportExcel(String dbalias, String title, String sql, Object... params) {
    return jdbcHandler.exportExcel(null, title, sql, params);
  }

  public HashMap queryOneAsMap(String sql, Object... params) {
    return jdbcHandler.queryOneAsMap(null, sql, params);
  }
  
  public HashMap queryOneAsMap(String dbalias, String sql, Object... params) {
    return jdbcHandler.queryOneAsMap(dbalias, sql, params);
  }

  public void exportCSV(String filePath, String encoding, char split, String sql, Object... params) {
    jdbcHandler.exportCSV(null, filePath, encoding, split, sql, params);
  }
  
  public void exportCSV(String dbalias, String filePath, String encoding, char split, String sql, Object... params) {
    jdbcHandler.exportCSV(dbalias, filePath, encoding, split, sql, params);
  }

  public boolean saveAnnotatedBean(Object annoBean) {
    return jdbcHandler.saveAnnotatedBean(null, annoBean);
  }
  
  public boolean saveAnnotatedBean(List<?> annoBeans) {
    return jdbcHandler.saveAnnotatedBean(null,BeanUtil.listToArray(annoBeans));
  }
  
  public boolean truncateTable(String schema, String tablename) {
    return jdbcHandler.truncateTable(null, schema, tablename);
  }
  
  public void loadExcelFile(String tablename, String filePath) {
    jdbcHandler.loadExcelFile(null, null, tablename, filePath);
  }
  
  public List callProcedure(String procedureName, Object[] in, int... outTypes) {
    return jdbcHandler.callProcedure(null, procedureName, in, outTypes);
  }
  

  public boolean deleteAnnotatedEntity(Object annoEntity){
    return jdbcHandler.deleteAnnotatedBean(null, annoEntity);
  }
  
}

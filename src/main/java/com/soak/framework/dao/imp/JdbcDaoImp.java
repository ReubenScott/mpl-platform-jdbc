package com.soak.framework.dao.imp;

import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soak.common.util.BeanUtil;
import com.soak.framework.dao.IBasicDao;
import com.soak.framework.jdbc.Restrictions;
import com.soak.framework.jdbc.core.JdbcTemplate;
import com.soak.framework.xml.XmlSqlMapper;

/**
 * 
 */
public class JdbcDaoImp implements IBasicDao {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  protected JdbcTemplate jdbc = JdbcTemplate.getInstance();

  public JdbcTemplate getJdbcTemplate() {
    return jdbc;
  }

  public void setJdbcHandler(JdbcTemplate jdbcHandler) {
    this.jdbc = jdbcHandler;
  }

  public int execute(String sql, Object... params) {
    return jdbc.executeUpdate(null, sql, params);
  }

  public <T> List<T> querySampleList(Class<T> sample, String sql, Object... params) {
    return jdbc.querySampleList(sample, sql, params);
  }

  public List<List> queryForList(String sql, Object... params) {
    return jdbc.queryForList(sql, params);
  }

  /**
   * 
   */
  public <T> List<T> findByAnnotatedSample(T annotatedSample, Restrictions... restrictions) {
    return jdbc.findByAnnotatedSample(annotatedSample, restrictions);
  }

  public List findByXmlSqlMapper(String dbalias, String sqlName, String value) {
    String sql = XmlSqlMapper.getInstance().getPreparedSQL(sqlName);
    sql = sql.replaceAll("@date", value);
    logger.debug(sql);
    return jdbc.queryForList(dbalias, sql);
  }

  public Workbook exportExcel(String dbalias, String title, String sql, Object... params) {
    return jdbc.exportWorkbook(title, sql, params);
  }

  public HashMap queryOneAsMap(String sql, Object... params) {
    return jdbc.queryOneAsMap(sql, params);
  }

  public HashMap queryOneAsMap(String dbalias, String sql, Object... params) {
    return jdbc.queryOneAsMap(dbalias, sql, params);
  }

  public void exportCSV(String filePath, String encoding, char split, String sql, Object... params) {
    jdbc.exportCSV(filePath, encoding, split, sql, params);
  }

  public boolean saveAnnotatedBean(Object... annoBean) {
    return jdbc.saveAnnotatedBean(annoBean);
  }

  public boolean saveAnnotatedBean(List<?> annoBeans) {
    return jdbc.saveAnnotatedBean(BeanUtil.listToArray(annoBeans));
  }

  public boolean truncateTable(String schema, String tablename) {
    return jdbc.truncateTable(schema, tablename);
  }
  
  public  boolean wipeTableData(Class<? extends Object> entityClass){
    return jdbc.truncateAnnotatedTable(entityClass);
  }

  public void loadExcelFile(String tablename, String filePath) {
    jdbc.loadExcelFile(null, tablename, filePath);
  }

  public List callProcedure(String procedureName, Object[] in, int... outTypes) {
    return jdbc.callProcedure(procedureName, in, outTypes);
  }

  public boolean deleteAnnotatedEntity(Object annoEntity) {
    return jdbc.deleteAnnotatedBean(annoEntity);
  }

}

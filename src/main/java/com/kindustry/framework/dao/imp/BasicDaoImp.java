package com.kindustry.framework.dao.imp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kindustry.common.util.BeanUtility;
import com.kindustry.framework.dao.IBasicDao;
import com.kindustry.framework.jdbc.Restrictions;
import com.kindustry.framework.jdbc.core.JdbcTemplate;
import com.kindustry.framework.jdbc.support.Pagination;
import com.kindustry.framework.xml.XmlSqlMapper;

/**
 * BasicDaoImp
 */
public class BasicDaoImp implements IBasicDao {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  protected JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();

  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  public void setJdbcTemplate(JdbcTemplate jdbcHandler) {
    this.jdbcTemplate = jdbcHandler;
  }

  public int execute(String sql, Object... params) {
    return jdbcTemplate.executeUpdate(sql, params);
  }

  public <T> List<T> querySampleList(Class<T> sample, String sql, Object... params) {
    return jdbcTemplate.querySampleList(sample, sql, params);
  }

  public List<List> queryForList(String sql, Object... params) {
    return jdbcTemplate.queryForList(sql, params);
  }

  /**
   * 
   */
  public <T> List<T> findByAnnotatedSample(T annotatedSample, Restrictions... restrictions) {
    return jdbcTemplate.findByAnnotatedSample(annotatedSample, restrictions);
  }

  public List findByXmlSqlMapper(String dbalias, String sqlName, String value) {
    String sql = XmlSqlMapper.getInstance().getPreparedSQL(sqlName);
    sql = sql.replaceAll("@date", value);
    logger.debug(sql);
    return jdbcTemplate.queryForList(dbalias, sql);
  }

  public Workbook exportExcel(String sql, Object... params) {
    return jdbcTemplate.exportNamelessWorkbook(sql, params);
  }

  public HashMap queryOneAsMap(String sql, Object... params) {
    return jdbcTemplate.queryOneAsMap(sql, params);
  }

  public HashMap queryOneAsMap(String dbalias, String sql, Object... params) {
    return jdbcTemplate.queryOneAsMap(dbalias, sql, params);
  }

  public void exportCSV(String filePath, String encoding, char split, String sql, Object... params) {
    jdbcTemplate.exportCSV(filePath, encoding, split, sql, params);
  }

  public boolean updateNullunableEntity(Object annoEntity){
    return jdbcTemplate.updateNullunableEntity(annoEntity);
  }

  public boolean saveAnnotatedEntity(Object... annoEntity) {
    return jdbcTemplate.saveAnnotatedEntity(annoEntity);
  }

  public boolean saveAnnotatedEntity(List<?> annoEntities) {
    return jdbcTemplate.saveAnnotatedEntity(BeanUtility.listToArray(annoEntities));
  }

  public boolean truncateTable(String schema, String tablename) {
    return jdbcTemplate.truncateTable(schema, tablename);
  }

  public boolean wipeTableData(Class<? extends Object> entityClass) {
    return jdbcTemplate.truncateAnnotatedTable(entityClass);
  }

  public void loadExcelFile(String tablename, String filePath) {
    jdbcTemplate.loadExcelFile(null, tablename, filePath);
  }

  public List callProcedure(String procedureName, Object[] in, int... outTypes) {
    return jdbcTemplate.callProcedure(procedureName, in, outTypes);
  }

  public boolean deleteAnnotatedEntity(Object annoEntity) {
    return jdbcTemplate.deleteAnnotatedEntity(annoEntity);
  }

  public boolean deleteEntityBySID(Class entity, Serializable sid) {
    return jdbcTemplate.deleteEntityBySID(entity,sid);
  }
  

  public Pagination queryPageBySQL(String sql, final int startIndex, final int pageSize, Object... params) {
    return jdbcTemplate.queryPageBySQL(sql,startIndex,pageSize,params);
  }

  public Pagination querySamplePageBySQL(Class sample, String sql, final int startIndex, final int pageSize, Object... params) {
    return jdbcTemplate.querySamplePageBySQL(sample, sql,startIndex,pageSize,params);
  }
}

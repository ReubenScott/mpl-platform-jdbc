package com.soak.framework.jdbc;

import java.util.ArrayList;
import java.util.List;


public class Restrictions {
  
  private List<Condition>  disjunction ;
  
  private List<Condition>  conjunction ;
  
  
  private StringBuffer sql  = new StringBuffer(); ;// 拼接SQL
  private List<Object>  params = new ArrayList<Object>();// 条件值

  // 拼接SQL
  public String getSql() {
    return sql.toString();
  }

  public List<Object> getParams() {
    return params;
  }

  /**
   * 添加查询条件
   * @param propertyName
   * @param value
   * @return
   */
  public void addCondition(Condition condition , String propertyName, Object... value) {
    sql.append(" and " + propertyName + condition.escapeOperator() );
    for(Object param : value ){
      params.add(param);
    }
  }

  /**
   * 添加查询条件 in 
   * @param propertyName
   * @param value
   */
  public Conditions in(String propertyNames, Object... value) {
    Conditions conditions = new Conditions() ;
    StringBuffer sql  = new StringBuffer(); ;// 拼接SQL
    List<Object>  params = new ArrayList<Object>();// 条件值
    
    sql.append(" (" + propertyNames + ") in (" );
    int fieldsNumber = propertyNames.split(",").length;  // Number of fields
    
    for(int i = 0 ; i< ( value.length / fieldsNumber) ; i++ ){
      if( fieldsNumber == 1 ){  // 1个属性条件
        if(i > 0){
          sql.append(",?");
        } else{
          sql.append("?");
        }
        params.add(value[i]);
      } else {  // 多个 属性条件
        if(i > 0){
          sql.append(",(");
        } else {
          sql.append("(");
        }
        for(int k =0 ; k < fieldsNumber ; k++ ){
          if(k > 0){
            sql.append(",?");
          } else {
            sql.append("?");
          }
          params.add(value[ i*fieldsNumber + k]);
        }
        sql.append(")");
      }
    }
    sql.append(") ");
    
    conditions.setSql(sql);
    conditions.setParams(params);
    
    return conditions;
  }
  
  
  
  /**
   * OR 条件
   * @param conditions
   */
  public void or(Conditions... conditions ){
    sql.append(" OR (");
    for(int i = 0 ; i< conditions.length ; i++ ){
        Conditions condition = conditions[i];
        if(i > 0){
          sql.append(" and " + condition.getSql());
        }else{
          sql.append(condition.getSql());
        }
        params.add(condition.getParams());
    }
    sql.append(" ) ");
  }
  
  

  public static void disjunction(){
//    Disjunction dis= Restrictions.disjunction();  
//    dis.add(Restrictions.like("chanpin", "冰箱", MatchMode.ANYWHERE));  
//    dis.add(Restrictions.like("chanpin", "洗衣机", MatchMode.ANYWHERE));  
//    dis.add(Restrictions.like("chanpin", "热水器", MatchMode.ANYWHERE));  
//    dis.add(Restrictions.like("chanpin", "空调", MatchMode.ANYWHERE));  
//    detachedCriteria.add(dis);  
  }

  
}
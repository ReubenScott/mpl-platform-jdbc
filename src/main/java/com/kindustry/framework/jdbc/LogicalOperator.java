package com.kindustry.framework.jdbc;

/** 
 * 逻辑运算符  主要有OR和AND
 *
 */
public enum LogicalOperator {

  AND {
    public String escapeOperator() {
      return " AND ";
    }
  },
  OR {
    @Override
    public String escapeOperator() {
      return " OR ";
    }
  };

  // 操作符
  public abstract String escapeOperator();
  
}
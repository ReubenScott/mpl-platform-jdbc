package com.soak.framework.jdbc;

/** 查询条件类型 */
public enum Condition {
  
  Equal {// 等于   
    public String escapeOperator() {
      return " = ? ";
    }
  },
  UnEqual {// 不等于
    @Override
    public String escapeOperator() {
      return " <> ? ";
    }
  },
  GreaterThan { // 大于  
    @Override
    public String escapeOperator() {
      return " > ? ";
    }
  },
  GreaterOrEqual {  /** 大于等于  */
    @Override
    public String escapeOperator() {
      return " >= ? ";
    }
  },
  LessThan { // 小于
    @Override
    public String escapeOperator() {
      return " < ? ";
    }
  },
  LessOrEqual {  /** 小于等于   */
    public String escapeOperator() {
      return " <= ? ";
    }
  },
  Between {  /* 区间  */
    public String escapeOperator() {
      return " between ? and ? ";
    }
  },
  FrontLike {   /* 前匹配 */
    public String escapeOperator() {
      return " like '%?' ";
    }
  },
  BehindLike {  /** 后匹配 */
    @Override
    public String escapeOperator() {
      return " like '?%' ";
    }
  },
  Like { /** 模糊 */
    @Override
    public String escapeOperator() {
      return " like '%?%' ";
    }
  };
  
  // 操作符
  public abstract String escapeOperator();
  

//  private String fieldname;// 条件名
//  private Object[] params;// 条件值

  // 拼接SQL
//  public String prepareSql() {
//    StringBuffer sb = new StringBuffer();
//    sb.append(" and " + this.fieldname + escapeOperator() );
//    return sb.toString();
//  }
  
  
  /**
   * 添加查询条件
   * @param propertyName
   * @param value
   * @return
   */
//  public Condition setParam(String propertyName, Object... value) {
//    this.fieldname = propertyName;
//    this.params = value;
//    return this ;
//  }

//  public String getFieldname() {
//    return fieldname;
//  }
//
//  public Object[] getParam() {
//    return params;
//  }

}
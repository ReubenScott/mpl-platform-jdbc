package com.soak.framework.jdbc;

public class ColumnInfo {

  private String columnName;// 列名 = rs.getString("COLUMN_NAME")

  private int dataType; // 类型 "DATA_TYPE"

  private String typeName;// 类型名称 = rs.getString("TYPE_NAME")

  private int precision;// 精度 = rs.getInt("COLUMN_SIZE")

  private int isNull;// 是否为空 = rs.getInt("NULLABLE")

  private int scale; // 小数的位数 = rs.getInt("DECIMAL_DIGITS")
  
  
  
  

}
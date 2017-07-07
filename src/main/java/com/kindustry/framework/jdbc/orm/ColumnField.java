package com.kindustry.framework.jdbc.orm;

public class ColumnField {

  private String columnName;// 列名 = rs.getString("COLUMN_NAME")
  
  private String typeName;// 类型名称 = rs.getString("TYPE_NAME")
  
  private Integer dataType; // 字段类型  "DATA_TYPE"

  private Integer precision;// 精度 = rs.getInt("COLUMN_SIZE")

  private Integer scale; // 小数的位数 = rs.getInt("DECIMAL_DIGITS")

  private Integer isNull;// 是否为空 = rs.getInt("NULLABLE")

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public Integer getDataType() {
    return dataType;
  }

  public void setDataType(Integer dataType) {
    this.dataType = dataType;
  }

  public Integer getPrecision() {
    return precision;
  }

  public void setPrecision(Integer precision) {
    this.precision = precision;
  }

  public Integer getScale() {
    return scale;
  }

  public void setScale(Integer scale) {
    this.scale = scale;
  }

  public Integer getIsNull() {
    return isNull;
  }

  public void setIsNull(Integer isNull) {
    this.isNull = isNull;
  }


}
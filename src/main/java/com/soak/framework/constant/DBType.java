package com.soak.framework.constant;


/**
 * 
 * 获取数据库类型
 */
public enum DBType {
  
  DB2 { 
    @Override
    public String getName() {
      return "DB2";
    }

    @Override
    public String getValue() {
      return "DB2";
    }
  }, 
  ORACLE { 
    @Override
    public String getName() {
      return "ORACLE";
    }

    @Override
    public String getValue() {
      return "ORACLE";
    }
  },
  MYSQL {
    @Override
    public String getName() {
      return "MySQL";
    }

    @Override
    public String getValue() {
      return "MySQL";
    }
  };

  public abstract String getName();

  public abstract String getValue();

  
  public static DBType getDBType(String typeValue) {
    for (DBType ctype : values()) {
      if (typeValue.startsWith(ctype.getValue())) {
        return ctype;
      }
    }
    return null;
  }

}
package com.soak.framework.constant;

import java.util.StringTokenizer;


/**
 * 
 * 获取数据库类型
 */
public enum CharSetEncodingType {  
  
  GBK {
    @Override
    public String getSubset() {
      return "GB2312,GBK,GB18030";
    }

    @Override
    public String getValue() {
      return "GB18030";
    }
  }, 
  ORACLE { 
    @Override
    public String getSubset() {
      return "ORACLE";
    }

    @Override
    public String getValue() {
      return "ORACLE";
    }
  },
  MYSQL {
    @Override
    public String getSubset() {
      return "MySQL";
    }

    @Override
    public String getValue() {
      return "MySQL";
    }
  };

  public abstract String getSubset();

  public abstract String getValue();

  
  public static String getCharSetEncoding(String charset) {
    String encoding = null;
    for (CharSetEncodingType ctype : values()) {
      StringTokenizer st = new StringTokenizer(ctype.getSubset(), ",");
      while (st.hasMoreElements()) {
        String tmpEncoding = (String) st.nextElement();
        if(tmpEncoding.equals(charset)){
          encoding = ctype.getValue();
          break ;
        }
      }
    }
    
    // 补充 列举字符的  不足
    if(encoding == null ){
      encoding = charset ;
    }
    
    return encoding;
  }

}
package com.kindustry.framework.jdbc.orm;

import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrmUtils {

  private static final Log logger = LogFactory.getLog(OrmUtils.class);

  public static String getORMTableName(Class<? extends Object> ormClass) {
    StringBuffer name = new StringBuffer();
    if (ormClass != null) {
      if (ormClass.isAnnotationPresent(Table.class)) { // 获得类是否有注解
        Table table = ormClass.getAnnotation(Table.class);
        String schema = table.schema(); // 获得schema
        String tablename = table.name(); // 获得表名

        if (schema != null && schema.trim().isEmpty()) {
          // throw new RuntimeException("@schema 为空");
        } else {
          name.append(schema.trim());
          name.append(".");
        }

        if (tablename != null && tablename.trim().isEmpty()) {
          // throw new RuntimeException("@tablename 为空");
        } else {
          name.append(tablename.trim());
        }

      }
    }
    return name.toString();
  }

}
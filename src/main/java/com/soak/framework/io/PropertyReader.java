package com.soak.framework.io;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyReader {

  private volatile static PropertyReader reader ;
  protected final Logger logger = LoggerFactory.getLogger(this.getClass());
  
  private PropertyReader(){
    
  }
  
  public static PropertyReader getInstance() {
    if (reader == null) {
      synchronized (PropertyReader.class) {
        if (reader == null) {
          reader = new PropertyReader();
        }
      }
    }
    return reader;
  }

  public Properties read(String propertyPath) {
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propertyPath);
    Properties properties = new Properties();
    try {
      properties.load(inputStream);
      inputStream.close();
    } catch (Exception e) {
      logger.error("exception:", e);
    }
    return properties;
  }
  
}

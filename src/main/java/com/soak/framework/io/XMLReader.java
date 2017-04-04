package com.soak.framework.io;

import java.io.*;
import java.util.*;
import org.dom4j.*;
import org.dom4j.io.*;

public class XMLReader {


  // 解析XML
  private void bbbb() {
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sys-config.xml");
    long lasting = System.currentTimeMillis();
    try {
      File f = new File("D:/workspace/epl-platform-framework/src/test/resources/sys-config.xml");
      Document doc = new SAXReader().read(inputStream);
      Element root = doc.getRootElement();
      Element foo;
      for (Iterator<Element> i = root.elementIterator("db-info"); i.hasNext();) {
        foo =  i.next();
        System.out.println("alias: " + foo.elementText("alias"));
        System.out.println("driverclass: " + foo.elementText("driverclass"));
        System.out.println("url: " + foo.elementText("url"));
        System.out.println("username: " + foo.elementText("username"));
        System.out.println("password: " + foo.elementText("password"));
        
      }
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }


  public static void main(String arge[]) {
    XMLReader reader = new XMLReader();
    reader.bbbb();
  }
}

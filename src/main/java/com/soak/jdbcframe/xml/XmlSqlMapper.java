package com.soak.jdbcframe.xml;

import java.io.InputStream;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlSqlMapper {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static XmlSqlMapper xmlSqlMapper = new XmlSqlMapper();

  private Hashtable<String, String> hashtable;

  private XmlSqlMapper() {
    loadXML();
  }

  public static XmlSqlMapper getInstance() {
    return xmlSqlMapper;
  }

  public Hashtable<String, String> loadXML() {
    hashtable = new Hashtable<String, String>();
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sql-config.xml");
      Document doc = db.parse(inputStream);
      Element root = doc.getDocumentElement();
      NodeList list = root.getElementsByTagName("sql-info");
      for (int i = 0; i < list.getLength(); i++) {
        // 得到第i个sql-info节点元素
        Element item = (Element) list.item(i);
        // 在当前的sql-info节点中 取出所有的sql-name对应的节点
        NodeList info = item.getElementsByTagName("sql-name");
        // 在当前当前的sql-info节点中 得到sql-name的多个节点，始终只取其中的第一个节点
        Element nameelement = (Element) info.item(0);
        // 在当前当前的sql-info节点中 得到sql-name的多个节点，始终只取其中的第一个节点sql-name对应的文本的值
        String namestr = nameelement.getFirstChild().getNodeValue();

        // 在当前的sql-info节点中 取出所有的sql-value对应的节点
        NodeList sql = item.getElementsByTagName("sql-value");
        // 在当前当前的sql-info节点中 得到sql-value的多个节点，始终只取其中的第一个节点
        Element sqlelement = (Element) sql.item(0);
        // 在当前当前的sql-info节点中 得到sql-value的多个节点，始终只取其中的第一个节点sql-value对应的文本的值
        String sqlstr = sqlelement.getFirstChild().getNodeValue();
        // System.out.println("sql语句为:"+sqlstr);
        hashtable.put(namestr, sqlstr); // 放入到Hashtable中
      }
    } catch (Exception e) {
      logger.error("读取sql-config.xml " + e.getMessage());
    }
    return hashtable;
  }

  public String getPreparedSQL(String key) {
    return hashtable.get(key);
  }

}
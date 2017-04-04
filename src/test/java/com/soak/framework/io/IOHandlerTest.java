package com.soak.framework.io;

import static org.junit.Assert.*;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.junit.Test;

public class IOHandlerTest {

  @Test
  public void testGetCharSetEncoding() {
    String path = "E:/ftpdata/P_063_CBOD_CMEMPEMP_20160616.del";
    String  code = IOHandler.getCharSetEncoding(path);
    System.out.println(code);
    
  }
  

//  @Test
  public void testGetCharSetEncodingByYRL() {
    String path = "E:/ftpdata/P_063_CBOD_CMEMPEMP_20160616.del";
//    String  code = IOHandler.codeString(path);
//    URL url = CreateStationTreeModel.class.getResource("/resource/" + "配置文件");  
//    URLConnection urlConnection = url.openConnection();  
//    InputStream inputStream = urlConnection.getInputStream();  
//    String charsetName = getFileEncode(url);  
//    System.out.println(charsetName);  
  }

}

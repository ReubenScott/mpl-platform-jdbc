package com.soak.common.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

/**
 * CSV操作(导出和导入)
 * 
 * @author 林计钦
 * @version 1.0 Jan 27, 2014 4:30:58 PM
 */
public class CSVUtils {
  
  
  public void importCsvFile(String filepath) {
    
    CSVReader csvReader = null;
    
    try {
      //importFile为要导入的文本格式逗号分隔的csv文件，提供getXX/setXX方法
      csvReader = new CSVReader(new FileReader(filepath),',');
      
      if(csvReader != null){
        //first row is title, so past
        csvReader.readNext();
        String[] csvRow = null;//row
        
        while ((csvRow = csvReader.readNext()) != null){
          for (int i =0; i<csvRow.length; i++){
            String temp = csvRow[i];
            System.out.println(temp);
            
          }
          
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } 
    
  }

  /**
   * 导出
   * 
   * @param file
   *          csv文件(路径+文件名)，csv文件不存在会自动创建
   * @param dataList
   *          数据
   * @return
   */
  public static boolean exportCsv(File file, List<String> dataList) {
    boolean isSucess = false;

    FileOutputStream out = null;
    OutputStreamWriter osw = null;
    BufferedWriter bw = null;
    try {
      out = new FileOutputStream(file);
      osw = new OutputStreamWriter(out);
      bw = new BufferedWriter(osw);
      if (dataList != null && !dataList.isEmpty()) {
        for (String data : dataList) {
          bw.append(data).append("\r");
        }
      }
      isSucess = true;
    } catch (Exception e) {
      isSucess = false;
    } finally {
      if (bw != null) {
        try {
          bw.close();
          bw = null;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (osw != null) {
        try {
          osw.close();
          osw = null;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (out != null) {
        try {
          out.close();
          out = null;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return isSucess;
  }

  /**
   * 导入
   * 
   * @param file
   *          csv文件(路径+文件)
   * @return
   */
  public static List<String> importCsv(File file) {
    List<String> dataList = new ArrayList<String>();

    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));
      String line = "";
      while ((line = br.readLine()) != null) {
        dataList.add(line);
      }
    } catch (Exception e) {
    } finally {
      if (br != null) {
        try {
          br.close();
          br = null;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return dataList;
  }

  public static void main(String[] args) {
    String[] str = { "省", "市", "区", "街", "路", "里", "幢", "村", "室", "园", "苑", "巷", "号" };
    File inFile = new File("C://in.csv"); // 读取的CSV文件
    File outFile = new File("C://out.csv");// 写出的CSV文件
    String inString = "";
    String tmpString = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(inFile));
      BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
      while ((inString = reader.readLine()) != null) {
        for (int i = 0; i < str.length; i++) {
          tmpString = inString.replace(str[i], "," + str[i] + ",");
          inString = tmpString;
        }
        writer.write(inString);
        writer.newLine();
      }
      reader.close();
      writer.close();
    } catch (FileNotFoundException ex) {
      System.out.println("没找到文件！");
    } catch (IOException ex) {
      System.out.println("读写文件出错！");
    }
  }
}

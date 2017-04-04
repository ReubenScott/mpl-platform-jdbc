package com.soak.common.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtil {

  /**
   * 新建目录
   * 
   * @param folderPath
   *          String 如 c:/fqf
   * @return boolean
   */
  public static void newFolder(String folderPath) {
    try {
      String filePath = folderPath;
      filePath = filePath.toString();
      File myFilePath = new File(filePath);
      if (!myFilePath.exists()) {
        myFilePath.mkdir();
      }
    } catch (Exception e) {
      System.out.println("新建目录操作出错");
      e.printStackTrace();
    }
  }

  /**
   * 新建文件
   * 
   * @param filePathAndName
   *          String 文件路径及名称 如c:/fqf.txt
   * @param fileContent
   *          String 文件内容
   * @return boolean
   */
  public static void newFile(String filePathAndName, String fileContent) {

    try {
      String filePath = filePathAndName;
      filePath = filePath.toString();
      File myFilePath = new File(filePath);
      if (!myFilePath.exists()) {
        myFilePath.createNewFile();
      }
      FileWriter resultFile = new FileWriter(myFilePath);
      PrintWriter myFile = new PrintWriter(resultFile);
      String strContent = fileContent;
      myFile.println(strContent);
      resultFile.close();

    } catch (Exception e) {
      System.out.println("新建目录操作出错");
      e.printStackTrace();

    }

  }

  /**
   * 删除文件
   * 
   * @param filePathAndName
   *          String 文件路径及名称 如c:/fqf.txt
   * @param fileContent
   *          String
   * @return boolean
   */
  public static void delFile(String filePathAndName) {
    try {
      String filePath = filePathAndName;
      filePath = filePath.toString();
      File myDelFile = new File(filePath);
      myDelFile.delete();

    } catch (Exception e) {
      System.out.println("删除文件操作出错");
      e.printStackTrace();

    }

  }

  /**
   * 删除文件夹
   * 
   * @param filePathAndName
   *          String 文件夹路径及名称 如c:/fqf
   * @param fileContent
   *          String
   * @return boolean
   */
  public static void delFolder(String folderPath) {
    try {
      delAllFile(folderPath); // 删除完里面所有内容
      String filePath = folderPath;
      filePath = filePath.toString();
      File myFilePath = new File(filePath);
      myFilePath.delete(); // 删除空文件夹

    } catch (Exception e) {
      System.out.println("删除文件夹操作出错");
      e.printStackTrace();

    }

  }

  /**
   * 删除文件夹里面的所有文件
   * 
   * @param path
   *          String 文件夹路径 如 c:/fqf
   */
  public static void delAllFile(String path) {
    File file = new File(path);
    if (!file.exists()) {
      return;
    }
    if (!file.isDirectory()) {
      return;
    }
    String[] tempList = file.list();
    File temp = null;
    for (int i = 0; i < tempList.length; i++) {
      if (path.endsWith(File.separator)) {
        temp = new File(path + tempList[i]);
      } else {
        temp = new File(path + File.separator + tempList[i]);
      }
      if (temp.isFile()) {
        temp.delete();
      }
      if (temp.isDirectory()) {
        delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
        delFolder(path + "/" + tempList[i]);// 再删除空文件夹
      }
    }
  }

  /**
   * 复制单个文件
   * 
   * @param oldPath
   *          String 原文件路径 如：c:/fqf.txt
   * @param newPath
   *          String 复制后路径 如：f:/fqf.txt
   * @return boolean
   */
  public static void copyFile(String oldPath, String newPath) {
    try {
      int bytesum = 0;
      int byteread = 0;
      File oldfile = new File(oldPath);
      if (oldfile.exists()) { // 文件存在时
        InputStream inStream = new FileInputStream(oldPath); // 读入原文件
        FileOutputStream fs = new FileOutputStream(newPath);
        byte[] buffer = new byte[1444];
        int length;
        while ((byteread = inStream.read(buffer)) != -1) {
          bytesum += byteread; // 字节数 文件大小
          System.out.println(bytesum);
          fs.write(buffer, 0, byteread);
        }
        inStream.close();
      }
    } catch (Exception e) {
      System.out.println("复制单个文件操作出错");
      e.printStackTrace();

    }

  }

  /**
   * 复制整个文件夹内容
   * 
   * @param oldPath
   *          String 原文件路径 如：c:/fqf
   * @param newPath
   *          String 复制后路径 如：f:/fqf/ff
   * @return boolean
   */
  public static void copyFolder(String oldPath, String newPath) {

    try {
      (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
      File a = new File(oldPath);
      String[] file = a.list();
      File temp = null;
      for (int i = 0; i < file.length; i++) {
        if (oldPath.endsWith(File.separator)) {
          temp = new File(oldPath + file[i]);
        } else {
          temp = new File(oldPath + File.separator + file[i]);
        }

        if (temp.isFile()) {
          FileInputStream input = new FileInputStream(temp);
          FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
          byte[] b = new byte[1024 * 5];
          int len;
          while ((len = input.read(b)) != -1) {
            output.write(b, 0, len);
          }
          output.flush();
          output.close();
          input.close();
        }
        if (temp.isDirectory()) {// 如果是子文件夹
          copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
        }
      }
    } catch (Exception e) {
      System.out.println("复制整个文件夹内容操作出错");
      e.printStackTrace();

    }

  }

  /**
   * Moving a File to Another Directory
   * 
   * @param srcFile
   *          eg: c:\windows\abc.txt
   * @param destPath
   *          eg: c:\temp
   * @return success
   */
  public static boolean moveFile(String srcFile, String destPath) {
    // File (or directory) to be moved
    File file = new File(srcFile);
    // Destination directory
    File dir = new File(destPath);
    // Move file to new directory
    boolean success = file.renameTo(new File(dir, file.getName()));

    return success;
  }

  /**
   * 移动文件到指定目录
   * 
   * @param oldPath
   *          String 如：c:/fqf.txt
   * @param newPath
   *          String 如：d:/fqf.txt
   */
  public static void moveFolder(String oldPath, String newPath) {
    copyFolder(oldPath, newPath);
    delFolder(oldPath);
  }

  /***
   * 判断文件是否存在
   * 
   * @param filepath
   * @return
   */
  public static boolean isFileExits(String filepath) {
    File file = new File(filepath);
    if (file.exists() && file.isFile()) {
      return true;
    }
    return false;
  }

  /***
   * 判断目录是否存在
   * 
   * @param filepath
   * @return
   */
  public static boolean isDirectoryExits(String filepath) {
    File file = new File(filepath);
    if (file.exists() && file.isDirectory()) {
      return true;
    }
    return false;
  }

  /**
   * 解压缩
   * 
   * @param sZipPathFile
   *          要解压的文件 文件全路径名
   * @param sDestPath
   *          解压到某文件夹
   * @return
   */
  public static List<String> unZip(String sZipPathFile, String sDestPath) {
    List<String> allFileName = new ArrayList<String>();
    try {
      // 先指定压缩档的位置和档名，建立FileInputStream对象
      FileInputStream fins = new FileInputStream(sZipPathFile);
      // 将fins传入ZipInputStream中
      ZipInputStream zins = new ZipInputStream(fins);
      ZipEntry ze = null;
      byte[] ch = new byte[256];
      while ((ze = zins.getNextEntry()) != null) {
        File zfile = new File(sDestPath + "/" + ze.getName());
        File fpath = new File(zfile.getParentFile().getPath());
        if (ze.isDirectory()) {
          if (!zfile.exists())
            zfile.mkdirs();
          zins.closeEntry();
        } else {
          if (!fpath.exists())
            fpath.mkdirs();
          FileOutputStream fouts = new FileOutputStream(zfile);
          int i;
          allFileName.add(zfile.getAbsolutePath());
          while ((i = zins.read(ch)) != -1)
            fouts.write(ch, 0, i);
          zins.closeEntry();
          fouts.close();
        }
      }
      fins.close();
      zins.close();
    } catch (IOException e) {
      System.err.println("Extract error:" + e.getMessage());
    }
    return allFileName;
  }

  /**
   * 获取文件 目录 路径
   * 
   * @param filePath
   * @return
   */
  public static String getFileDirPath(String filePath) {
    String filename = getFileName(filePath);
    String dirPath = filePath.substring(0, filePath.length() - filename.length());
    return dirPath;
  }

  /**
   * 获取文件名
   * 
   * @param filePath
   * @return
   */
  public static String getFileName(String filePath) {
    String filename = "";
    StringTokenizer st = new StringTokenizer(filePath.replace("\\", "/"), "/");
    while (st.hasMoreElements()) {
      filename = (String) st.nextElement();
    }
    return filename;
  }

  /**
   * 获取文件 可以根据正则表达式查找
   * 
   * @param dir
   *          String 文件夹名称
   * @param regex
   *          String 查找文件名，可带*.?进行模糊查询
   * @return File[] 找到的文件
   */
  public static File[] fuzzyLookupFiles(String dir, String regex) {
    // 开始的文件夹
    File file = new File(dir);

    regex = regex.replace('.', '#');
    regex = regex.replaceAll("#", "\\\\.");
    regex = regex.replace('*', '#');
    regex = regex.replaceAll("#", ".*");
    regex = regex.replace('?', '#');
    regex = regex.replaceAll("#", ".?");
    regex = "^" + regex + "$";

    System.out.println(regex);
    List<File> fileList = new ArrayList<File>();
    filePattern(file, Pattern.compile(regex), fileList);
    return (File[]) fileList.toArray(new File[fileList.size()]);
  }

  /**
   * @param file
   *          File 起始文件夹
   * @param p
   *          Pattern 匹配类型
   * @return ArrayList 其文件夹下的文件夹
   */

  private static void filePattern(File file, Pattern p, List<File> list) {
    if (file == null) {
      // return null;
    } else if (file.isFile()) {
      Matcher fMatcher = p.matcher(file.getName());
      if (fMatcher.matches()) {
        list.add(file);
      }
    } else if (file.isDirectory()) {
      for (File tmpFile : file.listFiles()) {
        if (tmpFile.isDirectory()) {
          filePattern(tmpFile, p, list);
        } else {
          Matcher fMatcher = p.matcher(tmpFile.getName());
          if (fMatcher.matches()) {
            list.add(tmpFile);
          }
        }
      }
    }
  }

  // ********************* 以下未经测试 *****************************

  /**
   * 压缩文件列表到某ZIP文件
   * 
   * @param zipFilename
   *          要压缩到的ZIP文件
   * @param paths
   *          文件列表，多参数
   * @throws Exception
   */
  public static void compress(String zipFilename, String... paths) throws Exception {
    compress(new FileOutputStream(zipFilename), paths);
  }

  /**
   * 压缩文件列表到输出流
   * 
   * @param os
   *          要压缩到的流
   * @param paths
   *          文件列表，多参数
   * @throws Exception
   */
  public static void compress(OutputStream os, String... paths) throws Exception {
    ZipOutputStream zos = new ZipOutputStream(os);
    for (String path : paths) {
      if (path.equals(""))
        continue;
      java.io.File file = new java.io.File(path);
      if (file.exists()) {
        if (file.isDirectory()) {
          zipDirectory(zos, file.getPath(), file.getName() + File.separator);
        } else {
          zipFile(zos, file.getPath(), "");
        }
      }
    }
    zos.close();
  }

  private static void zipDirectory(ZipOutputStream zos, String dirName, String basePath) throws Exception {
    File dir = new File(dirName);
    if (dir.exists()) {
      File files[] = dir.listFiles();
      if (files.length > 0) {
        for (File file : files) {
          if (file.isDirectory()) {
            zipDirectory(zos, file.getPath(), basePath + file.getName().substring(file.getName().lastIndexOf(File.separator) + 1) + File.separator);
          } else
            zipFile(zos, file.getPath(), basePath);
        }
      } else {
        ZipEntry ze = new ZipEntry(basePath);
        zos.putNextEntry(ze);
      }
    }
  }

  private static void zipFile(ZipOutputStream zos, String filename, String basePath) throws Exception {
    File file = new File(filename);
    if (file.exists()) {
      FileInputStream fis = new FileInputStream(filename);
      ZipEntry ze = new ZipEntry(basePath + file.getName());
      zos.putNextEntry(ze);
      byte[] buffer = new byte[8192];
      int count = 0;
      while ((count = fis.read(buffer)) > 0) {
        zos.write(buffer, 0, count);
      }
      fis.close();
    }
  }

  /**
   * 文件的写入 (单行写入)
   * 
   * @param filePath
   *          (文件路径)
   * @param fileName
   *          (文件名)
   * @param args
   * @throws IOException
   */
  public void writeFile(String filePath, String fileName, String args) throws IOException {
    FileWriter fw = new FileWriter(filePath + fileName);
    fw.write(args);
    fw.close();
  }

  /**
   * 文件的写入(按数组逐行写入)
   * 
   * @param filePath
   *          (文件路径)
   * @param fileName
   *          (文件名)
   * @param args
   *          []
   * @throws IOException
   */
  public void writeFile(String filePath, String fileName, String[] args) throws IOException {
    FileWriter fw = new FileWriter(filePath + fileName);
    PrintWriter out = new PrintWriter(fw);
    for (int i = 0; i < args.length; i++) {
      out.write(args[i]);
      out.println();
      out.flush();
    }
    fw.close();
    out.close();
  }

  public void importLineData(String dbid, File f, double ddd) {

    if (f.exists() && f.isFile() && f.length() > 0) {
      try {
        RandomAccessFile raf = new RandomAccessFile(f, "r");
        int count = 0;
        while (raf.getFilePointer() < raf.length()) {
          String temp = raf.readLine();
          String d = new String(temp.getBytes("ISO-8859-1"), "UTF-8");
          while (!d.endsWith(");")) {
            temp = raf.readLine();
            d += new String(temp.getBytes("ISO-8859-1"), "UTF-8");
          }
          String sql = d.substring(0, d.length() - 1);
          count++;
        }
        raf.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * A方法追加文件：使用RandomAccessFile
   */
  public static void appendMethodA(String fileName, String content) {
    try {
      // 打开一个随机访问文件流，按读写方式
      RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
      // 文件长度，字节数
      long fileLength = randomFile.length();
      // 将写文件指针移到文件尾。
      randomFile.seek(fileLength);
      randomFile.writeBytes(content);
      randomFile.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * B方法追加文件：使用FileWriter
   */
  public static void appendMethodB(String fileName, String content) {
    try {
      // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
      FileWriter writer = new FileWriter(fileName, true);
      writer.write(content);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
   */
  public static void readFileByBytes(String fileName) {
    File file = new File(fileName);
    InputStream in = null;
    try {
      System.out.println("以字节为单位读取文件内容，一次读一个字节：");
      // 一次读一个字节
      in = new FileInputStream(file);
      int tempbyte;
      while ((tempbyte = in.read()) != -1) {
        System.out.write(tempbyte);
      }
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    try {
      System.out.println("以字节为单位读取文件内容，一次读多个字节：");
      // 一次读多个字节
      byte[] tempbytes = new byte[100];
      int byteread = 0;
      in = new FileInputStream(fileName);
      showAvailableBytes(in);
      // 读入多个字节到字节数组中，byteread为一次读入的字节数
      while ((byteread = in.read(tempbytes)) != -1) {
        System.out.write(tempbytes, 0, byteread);
      }
    } catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e1) {
        }
      }
    }
  }

  /**
   * 以字符为单位读取文件，常用于读文本，数字等类型的文件
   */
  public static void readFileByChars(String fileName) {
    File file = new File(fileName);
    Reader reader = null;
    try {
      System.out.println("以字符为单位读取文件内容，一次读一个字节：");
      // 一次读一个字符
      reader = new InputStreamReader(new FileInputStream(file));
      int tempchar;
      while ((tempchar = reader.read()) != -1) {
        // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
        // 但如果这两个字符分开显示时，会换两次行。
        // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
        if (((char) tempchar) != '\r') {
          System.out.print((char) tempchar);
        }
      }
      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      System.out.println("以字符为单位读取文件内容，一次读多个字节：");
      // 一次读多个字符
      char[] tempchars = new char[30];
      int charread = 0;
      reader = new InputStreamReader(new FileInputStream(fileName));
      // 读入多个字符到字符数组中，charread为一次读取字符数
      while ((charread = reader.read(tempchars)) != -1) {
        // 同样屏蔽掉\r不显示
        if ((charread == tempchars.length) && (tempchars[tempchars.length - 1] != '\r')) {
          System.out.print(tempchars);
        } else {
          for (int i = 0; i < charread; i++) {
            if (tempchars[i] == '\r') {
              continue;
            } else {
              System.out.print(tempchars[i]);
            }
          }
        }
      }

    } catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
        }
      }
    }
  }

  /**
   * 以行为单位读取文件，常用于读面向行的格式化文件
   */
  public static String readFileByLines(String fileName) {
    File file = new File(fileName);
    BufferedReader reader = null;
    StringBuffer content = new StringBuffer();
    try {
      reader = new BufferedReader(new FileReader(file));
      String tempString = null;
      int line = 1;
      // 一次读入一行，直到读入null为文件结束
      while ((tempString = reader.readLine()) != null) {
        content.append(tempString);
        // 显示行号
        line++;
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
        }
      }
    }
    return content.toString();
  }

  /**
   * 随机读取文件内容
   * 
   * @param os
   *          要压缩到的流
   * @param paths
   *          文件列表，多参数
   * 
   */
  public static void readFileByRandomAccess(String fileName) {
    RandomAccessFile randomFile = null;
    try {
      System.out.println("随机读取一段文件内容：");
      // 打开一个随机访问文件流，按只读方式
      randomFile = new RandomAccessFile(fileName, "r");
      // 文件长度，字节数
      long fileLength = randomFile.length();
      // 读文件的起始位置
      int beginIndex = (fileLength > 4) ? 4 : 0;
      // 将读文件的开始位置移到beginIndex位置。
      randomFile.seek(beginIndex);
      byte[] bytes = new byte[10];
      int byteread = 0;
      // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
      // 将一次读取的字节数赋给byteread
      while ((byteread = randomFile.read(bytes)) != -1) {
        System.out.write(bytes, 0, byteread);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (randomFile != null) {
        try {
          randomFile.close();
        } catch (IOException e1) {
        }
      }
    }
  }

  /**
   * 显示输入流中还剩的字节数
   */
  private static void showAvailableBytes(InputStream in) {
    try {
      System.out.println("当前字节输入流中的字节数为:" + in.available());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
package com.soak.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.codec.binary.Base64;

public class StringUtil {

  /**
   * 判断是否为空
   * 
   * @param source
   * @return
   */
  public static boolean isEmpty(String source) {
    if (null == source || source.trim().equals("")) {
      return true;
    }
    return false;
  }

  public static String filterStr(String str) {
    StringBuffer strs = new StringBuffer();
    if (str != null) {
      for (int i = 0; i < str.length(); i++) {
        char ch = str.charAt(i);
        if (ch == '\r') {
          strs.append("<br>");
        } else if (ch == ' ') {
          strs.append("&nbsp;");
        } else if (ch == '<') {
          strs.append("&lt;");
        } else if (ch == '>') {
          strs.append("&gt;");
        } else {
          strs.append(ch);
        }
      }
    }
    return strs.toString();
  }

  public static String encodeStr(String str) {
    if (str != null) {
      try {
        str = new String(str.getBytes("ISO-8859-1"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
    return str;
  }

  public static String arrayToString(String[] array) {
    StringBuffer sb = new StringBuffer();
    int length = array.length;
    for (int i = 0; i < length; i++) {
      if (i == length - 1) {
        sb.append(array[i]);
      } else {
        sb.append(array[i] + ",");
      }
    }
    return sb.toString();
  }

  public static String arrayToString(List<String> array) {
    StringBuffer sb = new StringBuffer();
    int length = array.size();
    for (int i = 0; i < length; i++) {
      if (i == length - 1) {
        sb.append(array.get(i));
      } else {
        sb.append(array.get(i) + ",");
      }
    }
    return sb.toString();
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
  public static String[] fuzzyLookup(String[] strArray, String regex) {
    // 开始的文件夹
    regex = regex.replace('.', '#');
    regex = regex.replaceAll("#", "\\\\.");
    regex = regex.replace('*', '#');
    regex = regex.replaceAll("#", ".*");
    regex = regex.replace('?', '#');
    regex = regex.replaceAll("#", ".?");
    regex = "^" + regex + "$";

    System.out.println(regex);
    Pattern p = Pattern.compile(regex);
    // Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
    List<String> result = new ArrayList<String>();

    for (String str : strArray) {
      Matcher fMatcher = p.matcher(str);
      if (fMatcher.matches()) {
        result.add(str);
      }
    }

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * 使用java.util.zip.*工具对字符串进行压缩
   * 
   * @param str
   *          压缩前的文本
   * @return 压缩后的文本字节数组
   */
  public static final byte[] compress(String str) throws IOException {
    byte[] compressed = null;
    ByteArrayOutputStream out = null;
    ZipOutputStream zout = null;

    try {
      out = new ByteArrayOutputStream();
      zout = new ZipOutputStream(out);
      zout.putNextEntry(new ZipEntry("0"));
      zout.write(str.getBytes());
      zout.closeEntry();
      compressed = out.toByteArray();
    } finally {
      if (zout != null) {
        try {
          zout.close();
        } catch (IOException e) {
        }
      }
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
        }
      }
    }
    return compressed;
  }

  /**
   * 压缩字符串
   * 
   * @param str
   *          压缩前的文本
   * @return 压缩后的文本
   */
  public static final String zip(String str) throws IOException {
    byte[] compressed = compress(str);
    compressed = Base64.encodeBase64(compressed);
    return new String(compressed);
  }

  /**
   * 使用java.util.zip.*工具对压缩后的 byte[]进行解压缩
   * 
   * @param compressed
   *          压缩后的 byte[] 数据
   * @return 解压后的字符串
   */
  public static final String decompress(byte[] compressed) throws IOException {
    ByteArrayOutputStream out = null;
    ByteArrayInputStream in = null;
    ZipInputStream zin = null;
    String decompressed = null;
    try {
      out = new ByteArrayOutputStream();
      in = new ByteArrayInputStream(compressed);
      zin = new ZipInputStream(in);
      zin.getNextEntry();
      byte[] buffer = new byte[1024];
      int offset = -1;
      while ((offset = zin.read(buffer)) != -1) {
        out.write(buffer, 0, offset);
      }
      decompressed = out.toString();
    } finally {
      if (zin != null) {
        try {
          zin.close();
        } catch (IOException e) {
        }
      }
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
        }
      }
    }

    return decompressed;
  }

  /**
   * 解压缩字符串
   * 
   * @param strCompressed
   *          压缩后的文本
   * @return 解压缩后的文本
   */
  public static final String unZip(String strCompressed) throws IOException {
    byte[] compressed = strCompressed.getBytes();
    compressed = Base64.decodeBase64(compressed);
    return decompress(compressed);
  }

}

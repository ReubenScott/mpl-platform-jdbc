package com.soak.common.terminal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpZilla {

  protected static final Logger logger = LoggerFactory.getLogger(FtpZilla.class);

  /** 本地字符编码 */
  private static final String LOCAL_CHARSET = System.getProperty("sun.jnu.encoding");

  // FTP协议里面，规定文件名编码为iso-8859-1
  private static final String SERVER_CHARSET = "ISO-8859-1";

  
  /**
   * 释放连接
   * @param ftpClient
   */
  private static void release(FTPClient ftpClient) {
    if (ftpClient != null) {
      try {
        if(ftpClient.isConnected()){
          ftpClient.logout();
          ftpClient.disconnect();
        }
      } catch (IOException ioe) {
        ioe.printStackTrace();
        logger.debug("关闭FTP连接发生异常！");
        // throw new RuntimeException("关闭FTP连接发生异常！", e);
      }
    }
  }

  /**
   * 登录FTP
   * 
   * @param ip
   * @param port
   * @param userName
   * @param password
   * @return
   */
  private static FTPClient openFtpClient(UserAuthInfo userAuthInfo) {
    FTPClient ftpClient = null;
    int reply; // FTP服务器响应代码
    try {
      ftpClient = new FTPClient();
      // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
      ftpClient.connect(userAuthInfo.getHost(), userAuthInfo.getPort());// 连接FTP服务器
      // 登录
      ftpClient.login(userAuthInfo.getUser(), userAuthInfo.getPasswd());
      /**
       * // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）. if
       * (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8",
       * "ON"))) { ftpClient.setControlEncoding("UTF-8"); } else {
       * ftpClient.setControlEncoding(LOCAL_CHARSET); }
       */

      ftpClient.enterLocalPassiveMode();// 设置被动模式
      ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件传输类型为二进制
      ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
      ftpClient.setBufferSize(1024);
      ftpClient.setDataTimeout(120000);

      // 检验是否连接成功
      reply = ftpClient.getReplyCode(); // 获取ftp登录应答代码
      if (!FTPReply.isPositiveCompletion(reply)) {
        ftpClient.disconnect();
        logger.debug("FTP server refused connection.");
        return null;
      }
    } catch (SocketException e) {
      e.printStackTrace();
      logger.debug("登录ftp服务器失败,连接超时！");
    } catch (IOException e) {
      e.printStackTrace();
      logger.debug("登录ftp服务器失败，FTP服务器无法打开！");
    }

    return ftpClient;

  }
  

  /**
   * Description: 向FTP服务器上传文件
   * 
   * @Version1.0
   * @param remotePath
   *          FTP服务器保存目录,如果是根目录则为“/”
   * @param destfilename
   *          上传到FTP服务器上的文件名
   * @param is
   *          本地文件输入流
   * @return 成功返回true，否则返回false
   */
  public static boolean uploadFile(UserAuthInfo userAuthInfo, InputStream is, String remoteDirPath, String destfilename) {
    boolean result = false;
    FTPClient ftpClient = openFtpClient(userAuthInfo);

    if (ftpClient != null) {
      try {
        remoteDirPath = remoteDirPath.replace("\\", "/");
        remoteDirPath = new String(remoteDirPath.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
        destfilename = new String(destfilename.getBytes(LOCAL_CHARSET), SERVER_CHARSET);

        // 转移工作目录至指定目录下
        if (ftpClient.changeWorkingDirectory(remoteDirPath)) {
          result = ftpClient.storeFile(destfilename, is);
        }
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        release(ftpClient);
      }

    }

    if (result) {
      System.out.println("上传成功!");
    }

    return result;
  }

  /**
   * ftp 上传文件夹
   * 
   * @param ftpUrl
   *          ftp地址
   * @param userName
   *          ftp的用户名
   * @param password
   *          ftp的密码
   * @param directory
   *          上传至ftp的路径名不包括ftp地址
   * @param localDirPath
   *          要上传的文件全路径名
   * @param destDir
   *          上传至ftp后存储的文件路径名
   * @throws IOException
   */
  public static boolean uploadDir(UserAuthInfo userAuthInfo, String localDirPath, String destDir) {
    boolean flag = false;
    File source = new File(localDirPath);
    String fileName = source.getName();
    FTPClient ftpClient = openFtpClient(userAuthInfo);

    if (ftpClient != null && source.exists()) {
      try {
        if (source.isDirectory()) {
          if (!isDirExist(userAuthInfo, destDir)) {
            createDir(userAuthInfo, destDir);
          }
          String localFullPath = source.getCanonicalPath(); // 本地 全路径
          File sourceFile[] = source.listFiles();
          for (int i = 0; i < sourceFile.length; i++) {
            if (sourceFile[i].exists()) {
              String remotePath = sourceFile[i].getCanonicalPath();
              remotePath = destDir + remotePath.substring(localFullPath.length()).replace('\\', '/'); // .replace("/",
                                                                                                      // "")
              if (sourceFile[i].isDirectory()) {
                 uploadDir(userAuthInfo, sourceFile[i].getCanonicalPath(), remotePath);
              } else {
                remotePath = remotePath.substring(0, remotePath.lastIndexOf('/'));
                InputStream input = new FileInputStream(sourceFile[i]);
                if (ftpClient.changeWorkingDirectory(remotePath)) {
                  flag = ftpClient.storeFile(new String(sourceFile[i].getName().getBytes(LOCAL_CHARSET), SERVER_CHARSET), input);
                }
                input.close();
                logger.debug("文件:" + sourceFile[i].getName() + "上传" + (flag == true ? "成功" : "失败"));
              }
            }
          }
        } else {
          // 转移工作目录至指定目录下
          InputStream input = new FileInputStream(source);
          if (ftpClient.changeWorkingDirectory(destDir)) {
            flag = ftpClient.storeFile(new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET), input);
          }
          input.close();
          logger.debug("文件:" + source.getName() + "上传" + (flag == true ? "成功" : "失败"));
        }
      } catch (IOException e) {
        e.printStackTrace();
        logger.debug("本地文件上传失败！");
      } finally {
        release(ftpClient);
      }
    }
    return flag;
  }

  /**
   * Description: 从FTP服务器 批量下载文件
   * 
   * @Version1.0
   * 
   * @param localDirPath
   *          下载后保存到本地的路径
   * 
   * @param remoteFilePaths
   *          FTP服务器上的相对路径
   * 
   * @return
   */
  public static boolean downFile(UserAuthInfo userAuthInfo, String localDirPath, String... remoteFilePaths) {
    boolean result = false;
    FTPClient ftpClient = openFtpClient(userAuthInfo);
    if ((remoteFilePaths != null) && ftpClient != null ) {
      try {
        for (String remoteFilePath : remoteFilePaths) {
          remoteFilePath = remoteFilePath.replace('\\', '/');
          String parentPath = remoteFilePath.substring(0, remoteFilePath.lastIndexOf("/"));
          String remoteFileName = remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1);

          // 转移到FTP服务器目录至指定的目录下
          ftpClient.changeWorkingDirectory(new String(parentPath.getBytes(LOCAL_CHARSET), SERVER_CHARSET));
          // 获取文件列表
          String[] fnames = ftpClient.listNames();
          for (String fname : fnames) {
            String serverfilename = new String(fname.getBytes(SERVER_CHARSET), LOCAL_CHARSET);
            if (serverfilename.equals(remoteFileName)) {
              File localFile = new File(localDirPath + "/" + serverfilename);
              OutputStream is = new FileOutputStream(localFile);
              ftpClient.retrieveFile(fname, is);
              is.close();
            }
          }
        }
        result = true;
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        release(ftpClient);
      }
    }
    return result;
  }

  /**
   * 
   * @param ftpUrl
   * @param userName
   * @param password
   * @param directory
   * @return
   * @throws IOException
   */
  public static List<String> listFiles(UserAuthInfo userAuthInfo, String directory, List<String> result) {
    FTPClient ftpClient = openFtpClient(userAuthInfo);
    if (ftpClient != null ) {
      try {
        String parentPath = new String(directory.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
        ftpClient.changeWorkingDirectory(parentPath);
        // 遍历目录
        for (String tmpfilename : ftpClient.listNames()) {
          String childPath = parentPath + "/" + tmpfilename; // new
                                                             // String(tmpfilename.getBytes(SERVER_CHARSET),LOCAL_CHARSET);
          System.out.println(" childPath : " + childPath);
          String serverpath = new String(childPath.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
          System.out.println(" serverpath : " + serverpath);
          if (ftpClient.changeWorkingDirectory(childPath)) {
            listFiles(userAuthInfo, childPath, result);
          } else {
            result.add(new String(childPath.getBytes(SERVER_CHARSET), LOCAL_CHARSET));
          }

        }
      } catch (NumberFormatException e) {
        throw e;
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        release(ftpClient);
      }
    }
    return result;
  }

  /**
   * // 重命名 文件 或 目录
   * 
   * @param directory
   *          要重命名的文件所在ftp的路径名 (不包含ftp地址)
   * @param oldFileName
   *          要重命名的文件名 目录
   * @param newFileName
   *          重命名后的文件名 目录
   * @throws IOException
   */
  public static boolean rename(UserAuthInfo userAuthInfo,String directory, String oldName, String newName) {
    /**
     * 判断远程文件是否重命名成功，如果成功返回true，否则返回false
     */
    boolean result = false;
    FTPClient ftpClient = openFtpClient(userAuthInfo);
    if (ftpClient != null) {
      try {
        ftpClient.changeWorkingDirectory(new String(directory.getBytes(LOCAL_CHARSET), SERVER_CHARSET));
        oldName = new String(oldName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
        newName = new String(newName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
        result = ftpClient.rename(oldName, newName); // 重命名远程 文件、目录
      } catch (NumberFormatException e) {
        throw e;
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        release(ftpClient);
      }
    }
    return result;
  }

  /**
   * 
   * @param directory
   *          要删除的文件所在ftp的路径名不包含ftp地址
   * @param fileName
   *          要删除的文件名
   * @return
   * @throws IOException
   */
  public boolean remove(UserAuthInfo userAuthInfo, String directory, String fileName) {
    /**
     * 判断远程文件是否移除成功，如果成功返回true，否则返回false
     */
    boolean result = false;
    FTPClient ftpClient = openFtpClient(userAuthInfo);
    if ( ftpClient != null ) {
      try {
        String isDirPath = directory + "/" + fileName;
        fileName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);

        if (ftpClient.changeWorkingDirectory(new String(isDirPath.getBytes(LOCAL_CHARSET), SERVER_CHARSET))) {
          String[] filenames = ftpClient.listNames();
          for (String tmpfilename : filenames) {
            // isDirPath = isDirPath + "/" + tmpfilename;
            remove(userAuthInfo, isDirPath, tmpfilename);
            // while (true){
            // isDirPath = isDirPath + "/" + tmpfilename;
            // }
          }
          if (filenames.length == 0) {
            ftpClient.changeWorkingDirectory("../");
            System.out.println("fileName : " + fileName);
            result = ftpClient.removeDirectory(new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET));// 删除目录
          }
        } else {
          // 转移到FTP服务器目录至指定的目录下
          ftpClient.changeWorkingDirectory(new String(directory.getBytes(LOCAL_CHARSET), SERVER_CHARSET));
          result = ftpClient.deleteFile(new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET));// 删除远程文件
        }

      } catch (NumberFormatException e) {
        throw e;
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        release(ftpClient);
      }
    }
    
    return result;
  }

  /**
   * 创建文件夹
   * 
   * @param dir
   * @param ftpClient
   * @throws Exception
   */
  private static boolean createDir(UserAuthInfo userAuthInfo, String dirPath) {
    boolean result = true;
    FTPClient ftpClient = openFtpClient(userAuthInfo);
    StringTokenizer s = new StringTokenizer(dirPath, "/");
    try {
      while (s.hasMoreElements()) {
        String pathName = (String) s.nextElement();
        ftpClient.makeDirectory(pathName);
        ftpClient.changeWorkingDirectory(pathName);
      }
    } catch (IOException e) {
      e.printStackTrace();
      result = false;
    } finally {
      release(ftpClient);
    }
    
    return result;
  }

  /**
   * 检查文件夹是否存在
   * 
   * @param dir
   * @param ftpClient
   * @return
   */
  private static boolean isDirExist(UserAuthInfo userAuthInfo, String dir ) {
    boolean isDirExist = false;
    FTPClient ftpClient = openFtpClient(userAuthInfo);
    try {
      isDirExist = ftpClient.changeWorkingDirectory(dir);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      release(ftpClient);
    }
    return isDirExist;
  }

}
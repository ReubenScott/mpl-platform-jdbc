/*
 * Created on 2009-9-14
 * Copyright 2009 by www.xfok.net. All Rights Reserved
 *
 */

package com.soak.common.terminal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 * @author YangHua 转载请注明出处：http://www.xfok.net/2009/10/124485.html
 */
public class SecureCRT {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static Session openSession(UserAuthInfo userAuthInfo) {
    // String host, String user, String passwd, int port
    try {
      JSch jsch = new JSch();
      Session session = jsch.getSession(userAuthInfo.getUser(), userAuthInfo.getHost(), userAuthInfo.getPort());
      // sshConfig.put("userauth.gssapi-with-mic", "no");
      session.setConfig("StrictHostKeyChecking", "no"); // 不验证host-key
      // session.setConfig("PreferredAuthentications",
      // "password,gssapi-with-mic,publickey,keyboard-interactive");
      session.setPassword(userAuthInfo.getPasswd());
      // session.connect();
      // 设置登陆超时时间
      session.connect(30000);
      return session;
    } catch (JSchException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 连接sftp服务器
   * 
   * @param host
   *          主机
   * @param port
   *          端口
   * @param username
   *          用户名
   * @param password
   *          密码
   * @return
   */
  private static ChannelSftp openSftpChannel(Session session) {
    ChannelSftp channelSftp = null;
    try {
      Channel channel = session.openChannel("sftp");
      channel.connect();
      channelSftp = (ChannelSftp) channel;
    } catch (JSchException e) {
      e.printStackTrace();
    }

    return channelSftp;
  }

  private static void clear(Session session, Channel channel) {
    if (channel != null) {
      channel.disconnect();
    }
    if (session != null) {
      session.disconnect();
    }
  }

  /**
   * 上传文件
   * 
   * @param directory
   *          上传的目录
   * @param uploadFile
   *          要上传的文件
   * @param sftp
   */
  public static boolean uploadFile(UserAuthInfo userAuthInfo, String directory, String localeFile) {
    Session session = null;
    ChannelSftp channel = null;
    try {
      session = openSession(userAuthInfo);
      channel = openSftpChannel(session);
      channel.cd(directory);
      File file = new File(localeFile);
      //TODO 中文文件名
      channel.put(localeFile, file.getName(), ChannelSftp.OVERWRITE);
      channel.quit();
    } catch (SftpException e) {
      e.printStackTrace();
      return false;
    } finally {
      clear(session, channel);
    }
    return true;
  }

  /**
   * 下载文件
   * 
   * @param directory
   *          下载目录
   * @param downloadFile
   *          下载的文件
   * @param saveFile
   *          存在本地的路径
   * @param sftp
   */
  public void download(UserAuthInfo userAuthInfo, String directory, String downloadFile, String saveFile) {
    Session session = null;
    ChannelSftp channel = null;
    try {
      session = openSession(userAuthInfo);
      channel = openSftpChannel(session);
      channel.cd(directory);
      File file = new File(saveFile);
      channel.get(downloadFile, new FileOutputStream(file));
    } catch (SftpException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      clear(session, channel);
    }
  }

  /**
   * 删除文件
   * 
   * @param directory
   *          要删除文件所在目录
   * @param deleteFile
   *          要删除的文件
   * @param sftp
   */
  public static void delete(UserAuthInfo userAuthInfo,String directory, String... deleteFileRegxs) {
    Session session = null;
    ChannelSftp channel = null;
    try {
      session = openSession(userAuthInfo);
      channel = openSftpChannel(session);
      channel.cd(directory);
      for(String regx : deleteFileRegxs){
        Vector<ChannelSftp.LsEntry> list = channel.ls(regx);
        for (int i = 0; i < list.size(); i++) {
          LsEntry item = list.get(i);
          if (item.getAttrs().isDir()) { 
            channel.rmdir(regx);
          } else {
            channel.rm(regx);
          }
        }
      }
    } catch (SftpException e) {
      e.printStackTrace();
    } finally {
      clear(session, channel);
    }
  }

  /**
   * 列出目录下的文件 （不包含目录 ）   ! no directories
   * 
   * @param directory
   *          要列出的目录
   * @param sftp
   * @return
   * @throws SftpException
   */
  public static String[] listFile(UserAuthInfo userAuthInfo, String directory, String regx) {
    List<String> fileNames = new ArrayList<String>();
    Session session = null;
    ChannelSftp channel = null;
    try {
      session = openSession(userAuthInfo);
      channel = openSftpChannel(session);
      // 进入服务器指定的文件夹
      channel.cd(directory);
      Vector<ChannelSftp.LsEntry> list = channel.ls(regx);
      for (int i = 0; i < list.size(); i++) {
        LsEntry item = list.get(i);
        if (!item.getAttrs().isDir()) { // display all files 
          String fileName = item.getFilename();
          fileNames.add(fileName);
        }
      }
    } catch (SftpException e) {
      e.printStackTrace();
    } finally {
      clear(session, channel);
    }

    return (String[]) fileNames.toArray(new String[fileNames.size()]);
  }

  /**
   * 列出目录下的目录
   * 
   * @param directory
   *          要列出的目录
   * @param sftp
   * @return
   * @throws SftpException
   */
  public String[] listDirectory(UserAuthInfo userAuthInfo, String directory, String regx) {
    List<String> dirNames = new ArrayList<String>();
    Session session = openSession(userAuthInfo);
    ChannelSftp channel = openSftpChannel(session);
    try {
      // 进入服务器指定的文件夹
      channel.cd(directory);
      Vector<ChannelSftp.LsEntry> list = channel.ls(regx); // "*.txt"
      for (int i = 0; i < list.size(); i++) {
        LsEntry item = list.get(i);
        if (item.getAttrs().isDir()) {
          String fileName = item.getFilename();
          dirNames.add(fileName);
          System.out.println(item.toString()); // display only directories
        }
      }
    } catch (SftpException e) {
      e.printStackTrace();
    } finally {
      clear(session, channel);
    }

    return (String[]) dirNames.toArray(new String[dirNames.size()]);
  }

  /**
   * 两个Sftp 服务器 同步 一个 文件
   * 
   * @param fromSftp
   * @param toSftp
   * @param fromFile
   * @param toFile
   */
  public static void syncFile(UserAuthInfo fromUserAuthInfo, UserAuthInfo toUserAuthInfo, String fromFile, String toFile) {
    Session fromSession = openSession(fromUserAuthInfo);
    Session toSession = openSession(toUserAuthInfo);
    ChannelSftp fromChannel = openSftpChannel(fromSession);
    ChannelSftp toChannel = openSftpChannel(toSession);
    // 以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
    try {
      InputStream instream = fromChannel.get(fromFile); // "1.txt"
      OutputStream outstream = toChannel.put(toFile, ChannelSftp.OVERWRITE); // "1.txt"

      // File file = new File(localeFile);
      // channel.put(localeFile, file.getName(), ChannelSftp.OVERWRITE);

      byte[] buffer = new byte[1024];
      int n;
      while ((n = instream.read(buffer)) != -1) {
        outstream.write(buffer, 0, n);
      }
      outstream.flush();
      outstream.close();
      instream.close();

      toChannel.quit();
      fromChannel.quit();
    } catch (SftpException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      clear(toSession, toChannel);
      clear(fromSession, fromChannel);
    }
  }

  /**
   * 两个Sftp 服务器 同步 一个 文件夹
   * 
   * @param fromSftp
   * @param toSftp
   * @param fromFile
   * @param toFile
   */
  public static void syncDirectory(UserAuthInfo fromUserAuthInfo, UserAuthInfo toUserAuthInfo, String fromDirectory, String toDirectory, String regx ) {
    Session fromSession = openSession(fromUserAuthInfo);
    Session toSession = openSession(toUserAuthInfo);
    ChannelSftp fromChannel = openSftpChannel(fromSession);
    ChannelSftp toChannel = openSftpChannel(toSession);
    
    try {
      // 进入服务器指定的文件夹
      fromChannel.cd(fromDirectory);
      toChannel.cd(toDirectory);
      Vector<ChannelSftp.LsEntry> list = fromChannel.ls(regx); // "*.txt"
      for (int i = 0; i < list.size(); i++) {
        LsEntry item = list.get(i);
        String fileName = item.getFilename();
        String fromFile  =  fromDirectory + "/" +  fileName ;
        String toFile  =  toDirectory + "/" + fileName ;
        if (!item.getAttrs().isDir()) { // 文件
          InputStream instream = fromChannel.get(fromFile); 
          OutputStream outstream = toChannel.put(toFile, ChannelSftp.OVERWRITE); // "1.txt"

          byte[] buffer = new byte[1024];
          int n;
          while ((n = instream.read(buffer)) != -1) {
            outstream.write(buffer, 0, n);
          }
          outstream.flush();
          outstream.close();
          instream.close();
        } else { // 目录
          //TODO 有子文件夹 
          syncDirectory(fromUserAuthInfo, toUserAuthInfo, fromFile, toFile, regx);
        }
      }
      toChannel.quit();
      fromChannel.quit();
    } catch (SftpException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      clear(toSession, toChannel);
      clear(fromSession, fromChannel);
    }
    
  }
  
  
  
  
}
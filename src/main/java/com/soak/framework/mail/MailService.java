package com.soak.framework.mail;

import java.util.Properties;

import javax.mail.internet.MimeUtility;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soak.framework.io.PropertyReader;



/**
 * 邮件小工具
 * */
public class MailService {

  private static final Logger logger = LoggerFactory.getLogger(MailService.class);

  /** 邮件配置文件名 */
  public static final String MAIL_CFG_PATH = "mail.properties";

  /** 邮件服务器配置名 */
  public static final String MAIL_SERVER = "mail.server";

  /** 邮件服务器 */
  public static final String MAIL_SERVER_HOST = "mail.server.host";

  /** 邮件服务器端口 */
  public static final String MAIL_SERVER_PORT = "mail.server.port";

  /** 邮件服务器SSL */
  public static final String MAIL_SERVER_SSL = "mail.server.ssl";

  /** 邮件发送地址 */
  public static final String MAIL_SENDER_ADDRESS = "mail.sender.address";

  /** 邮件发送用户名 */
  public static final String MAIL_SENDER_USER = "mail.sender.user";

  /** 邮件发送者名称 */
  public static final String MAIL_SENDER_NAME = "mail.sender.name";

  /** 邮件发送用户密码 */
  public static final String MAIL_SENDER_PWD = "mail.sender.pwd";

  /** 邮件发送选项配置名 */
  public static final String MAIL_CFG = "mail.cfg";

  /** 发送邮件 */
  public static boolean sendMail(MailBean mailBean) {

    final HtmlEmail email = new HtmlEmail();
    email.setCharset("UTF-8");      

    String tempDir = null;
    try {
      MailSender sender = new MailSender();
      email.setHostName(sender.hostName);
      email.setSmtpPort(sender.port);
      email.setSSL(sender.ssl);
      email.setFrom(sender.mailAddress, sender.senderName);
      email.setAuthentication(sender.userName, sender.password);
      email.setCharset("UTF-8");
      email.setSubject(mailBean.getTitle());
      email.setHtmlMsg(mailBean.getContent());
      email.addTo(mailBean.getTargetMail(), mailBean.getTargetName());
      
      // 添加附件
      EmailAttachment attach = new EmailAttachment();      
      // 解决附件名乱码  
      attach.setName(MimeUtility.encodeText(mailBean.getAttachName()));
      attach.setPath(mailBean.getAttachPath());
      attach.setDisposition(EmailAttachment.ATTACHMENT);         
      email.attach(attach);

      logger.info("Send mail.From <" + sender.mailAddress + ">,To <" + mailBean.getTargetMail() + ">,tempDir=" + tempDir + ",attachUrl=" + mailBean.getAttachUrl());
      email.send();
      logger.info("mail has been sent.");
      return true;
    } catch (Exception e) {
      logger.info(e.getMessage());
      return false;
    }

  }

  /** 邮件发送者 */
  private static class MailSender {

    /** SMTP服务器地址 */
    private String hostName;

    /** 服务器发送端口 */
    private int port;

    /** 是否SSL连接 */
    private boolean ssl;

    /** 用户名 */
    private String userName;

    /** 密码 */
    private String password;

    /** 发送邮箱 */
    private String mailAddress;

    /** 发送者名称 */
    private String senderName;

    /*
     * public String getHostName() { return hostName; }
     * 
     * public int getPort() { return port; }
     * 
     * public boolean getSsl() { return ssl; }
     * 
     * public String getUserName() { return userName; }
     * 
     * public String getPassword() { return password; }
     * 
     * public String getMailAddress() { return mailAddress; }
     * 
     * public String getSenderName() { return senderName; }
     */

    /** 设置参数 */
    public MailSender() {
      Properties properties = PropertyReader.getInstance().read(MAIL_CFG_PATH);
      hostName = properties.getProperty(MAIL_SERVER_HOST);
      try {
        port = Integer.valueOf(properties.getProperty(MAIL_SERVER_PORT));
      } catch (NumberFormatException e) {
        logger.info("Mail Config File Error.Port is a Number,but <" + properties.getProperty(MAIL_SERVER_PORT) + "> can't be parse to int.");
      }

      mailAddress = properties.getProperty(MAIL_SENDER_ADDRESS);
      userName = properties.getProperty(MAIL_SENDER_USER);
      password = properties.getProperty(MAIL_SENDER_PWD);

      /*
       * if (map.containsKey("port")) {
       * 
       * 
       * } if (map.containsKey("ssl")) { ssl = Boolean.valueOf(map.get("ssl")); }
       */
    }

    /**
     * 得到邮件发送服务器配置
     * */
    // public static MailServer getMailServer(){
    // return new MailCfgServerImpl().setCfgMap(properties.getProperty(MailConst.MAIL_SERVER));
    // }
    // /**
    // * 得到邮件发送配置的信息
    // * */
    // public static MailSendCfg getMailCfg(){
    // return new MailSendCfgImpl().setCfgMap(pr.getValues(MailConst.MAIL_CFG));
    // }
    // /**
    // * 得到邮件指定Key的参数值
    // * */
    // public static Map<String,String> getMailMap(String key){
    // return pr.getValues(key);
    // }

  }
}

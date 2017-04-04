package com.soak.framework.mail;

import java.io.Serializable;

/**
 * 邮件对象
 */
public class MailBean implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -3100720689076314509L;
  /**
   * 目标邮箱
   */
  private String targetMail;
  /**
   * 标题
   */
  private String title;
  /**
   * 内容
   */
  private String content;
  /**
   * 目标姓名
   */
  private String targetName;
  /**
   * 附件路径
   */
  private String attachPath;
  /**
   * 附件URL
   */
  private String attachUrl;
  /**
   * 文件重命名
   */
  private String attachName;

  /**
   * 目标邮箱
   */
  public String getTargetMail() {
    return targetMail;
  }

  /**
   * 目标邮箱
   */
  public void setTargetMail(String targetMail) {
    this.targetMail = targetMail;
  }

  /**
   * 标题
   */
  public String getTitle() {
    return title;
  }

  /**
   * 标题
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * 内容
   */
  public String getContent() {
    return content;
  }

  /**
   * 内容
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * 目标姓名
   */
  public String getTargetName() {
    return targetName;
  }

  /**
   * 目标姓名
   */
  public void setTargetName(String targetName) {
    this.targetName = targetName;
  }

  public String getAttachPath() {
    return attachPath;
  }

  public void setAttachPath(String attachPath) {
    this.attachPath = attachPath;
  }

  public String getAttachUrl() {
    return attachUrl;
  }

  public void setAttachUrl(String attachUrl) {
    this.attachUrl = attachUrl;
  }

  public String getAttachName() {
    return attachName;
  }

  public void setAttachName(String attachName) {
    this.attachName = attachName;
  }

}

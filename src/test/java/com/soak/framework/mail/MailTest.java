package com.soak.framework.mail;


import org.junit.Test;



public class MailTest {

  @Test
  public void testSendMail() {
    MailBean mail = new MailBean();
    mail.setTitle("测试标题");
    // bean.setTargetMail("265583513@qq.com");
    mail.setTargetMail("chenjun12@beyondsoft.com");
    mail.setContent("测试内容");
    mail.setAttachPath("E:/wsdl.html");
    
    // bean.setTargetName("收件人");
    MailService.sendMail(mail);
  }
  
}

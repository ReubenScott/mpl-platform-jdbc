package com.soak.framework.security;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

public class StandardEncryptorTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testbyte2hex() {
    byte[] b = new byte [29];
    try {
      String s = new String(b, "GB2312");
      System.out.println(s);
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    
  }

}

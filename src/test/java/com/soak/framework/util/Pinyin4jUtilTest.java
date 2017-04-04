package com.soak.framework.util;

import org.junit.Test;


public class Pinyin4jUtilTest {
  
  
  @Test
  public void testPu(){
    String str = "长沙市长";  
    String pinyin = Pinyin4jUtil.converterToSpell(str);  
    System.out.println(str+" pin yin ："+pinyin);  
    pinyin = Pinyin4jUtil.converterToFirstSpell(str);  
    System.out.println(str+" short pin yin ："+pinyin);  
  }

}

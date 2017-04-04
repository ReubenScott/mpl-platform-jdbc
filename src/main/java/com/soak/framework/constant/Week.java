package com.soak.framework.constant;


public enum Week {

  MONDAY     ( 2, "周一" , "星期一" , "Mon."  , "Monday"    ), 
  TUESDAY    ( 3, "周二" , "星期二" , "Tues." , "Tuesday"   ), 
  WEDNESDAY  ( 4, "周三" , "星期三" , "Wed."  , "Wednesday" ), 
  THURSDAY   ( 5, "周四" , "星期四" , "Thur." , "Thursday"  ), 
  FRIDAY     ( 6, "周五" , "星期五" , "Fri."  , "Friday"    ), 
  SATURDAY   ( 7, "周六" , "星期六" , "Sat."  , "Saturday"  ), 
  SUNDAY     ( 1, "周日" , "星期日" , "Sun."  , "Sunday"    );

  // 成员变量
  private int number;
  private String cnShortName;
  private String cnName;   
  private String enShortName;
  private String enName;
  

  // 构造方法，注意：构造方法不能为public，因为enum并不可以被实例化
  private Week(int number , String cnShortName, String cnName, String enShortName, String enName) {
    this.number = number;
    this.cnName = cnName;
    this.cnShortName = cnShortName;
    this.enShortName = enShortName;
    this.enName = enName;
  }

  public String getCnName() {
    return cnName;
  }

  public String getEnName() {
    return enName;
  }

  public String getShortCnName() {
    return cnShortName;
  }

  public String getShortEnName() {
    return enShortName;
  }
  
  public int getNumber() {
    return number;
  }

  
  public static Week getWeek(int number){
    for (Week week : values()) {
      if (week.getNumber() == number) {
        return week;
      }
    }
    return null;
  }
  
  
}
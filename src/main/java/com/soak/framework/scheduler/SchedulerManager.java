package com.soak.framework.scheduler;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 任务调度管理器
 * </p>
 * 
 * 2010-5-28 下午11:30:20
 */
public class SchedulerManager { 
  
  private volatile static ScheduledExecutorService scheduExec;

  private volatile static SchedulerManager instance;
  
  
  /**
   * 根据线程池中线程数量构造调度管理器
   * 
   */
  private SchedulerManager() {
    int cpuNums = Runtime.getRuntime().availableProcessors(); // 获取当前系统的CPU 数目
    scheduExec = Executors.newScheduledThreadPool(cpuNums);
  }


  /***
   * 获取实例
   * 
   * @return
   */
  public static SchedulerManager getInstance() {
    if (instance == null) {
      synchronized (SchedulerManager.class) {
        if (instance == null) {
          instance = new SchedulerManager();
        }
      }
    }
    return instance;
  }
  
  /***
   * <p>
   * 添加需要计划的任务
   * </p>
   * 
   * @param command
   * @param time  //  定时
   */
  public void putSchedule(Runnable command , Date... startTimes ){
    long current = System.currentTimeMillis() ;
    if((startTimes != null) && (startTimes.length > 0) ){
      for(Date time : startTimes){
        long delay =  time.getTime() - current ;
        scheduExec.schedule(command , delay , TimeUnit.MILLISECONDS);
      }
    } else {
      scheduExec.schedule(command , 0 , TimeUnit.MILLISECONDS);
    }
  }

  /***
   * 
   * 添加需要计划的任务
   * 
   * 周期性 定时任务
   * 
   */
  public void putFixedRateSchedule(Runnable command , Date startTime , long period){
    long current = System.currentTimeMillis() ;
    if(startTime != null){
      long initialDelay =  startTime.getTime() - current ;
      scheduExec.scheduleAtFixedRate(command , initialDelay , period , TimeUnit.MILLISECONDS);
    } else {
      scheduExec.scheduleAtFixedRate(command , 0 , period , TimeUnit.MILLISECONDS);
    }
  }
  
  
   /***
    *  停止任务调度器
    */
  public void stop() {
    scheduExec.shutdown();
  }


}
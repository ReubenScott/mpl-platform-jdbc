package com.soak.framework.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo描述:
 * 线程池(ThreadPoolExecutor)及其拒绝策略(RejectedExecutionHandler)使用示例
 * 
 * 工作原理:
 * 线程池的工作中主要涉及到:corePoolSize,workQueue,maximumPoolSize,RejectedExecutionHandler
 * 它们的调用原理:
 * 1 当线程池中线程数量小于corePoolSize则创建线程,并处理请求
 * 2 当线程池中线程数量等于corePoolSize则把请求放入workQueue中,线程池中的的空闲线程就从workQueue中取任务并处理
 * 3 当workQueue已满存放不下新入的任务时则新建线程入池,并处理请求;
 * 如果线程池中线程数大于maximumPoolSize则用RejectedExecutionHandler使用一定的策略来做拒绝处理
 * 
 * 在该机制中还有一个keepAliveTime,文档描述如下:
 * when the number of threads is greater than the core,
 * this is the maximum time that excess idle threads will wait for new tasks before terminating.
 * 它是什么意思呢？
 * 比如线程池中一共有5个线程,其中3个为核心线程(core)其余两个为非核心线程.
 * 当超过一定时间(keepAliveTime)非核心线程仍然闲置(即没有执行任务或者说没有任务可执行)那么该非核心线程就会被终止.
 * 即线程池中的非核心且空闲线程所能持续的最长时间,超过该时间后该线程被终止.
 * 
 * 
 * RejectedExecutionHandler的四种拒绝策略
 * 
 * hreadPoolExecutor.AbortPolicy:
 * 当线程池中的数量等于最大线程数时抛出java.util.concurrent.RejectedExecutionException异常.
 * 涉及到该异常的任务也不会被执行.
 * 
 * ThreadPoolExecutor.CallerRunsPolicy():
 * 当线程池中的数量等于最大线程数时,重试添加当前的任务;它会自动重复调用execute()方法
 * 
 * ThreadPoolExecutor.DiscardOldestPolicy():
 * 当线程池中的数量等于最大线程数时,抛弃线程池中工作队列头部的任务(即等待时间最久Oldest的任务),并执行新传入的任务
 * 
 * ThreadPoolExecutor.DiscardPolicy():
 * 当线程池中的数量等于最大线程数时,丢弃不能执行的新加任务
 * 
 * 参考资料:
 * http://blog.csdn.net/cutesource/article/details/6061229
 * http://blog.csdn.net/longeremmy/article/details/8231184
 * http://blog.163.com/among_1985/blog/static/275005232012618849266/
 * http://blog.csdn.net/longeremmy/article/details/8231184
 * http://ifeve.com/java-threadpool/
 * http://www.blogjava.net/xylz/archive/2010/07/08/325587.html
 * http://blog.csdn.net/ns_code/article/details/17465497
 * 
 */
public class ThreadPool {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final int POOLSIZE = 4; // 线程池维护线程的最大线程数

  private ExecutorService executorService;

  private volatile static ThreadPool instance;

  // 构造一个线程池 固定大小
  private ThreadPool() {
    executorService = Executors.newFixedThreadPool(POOLSIZE);
  }

  /***
   * 获取实例
   * 
   * @return
   */
  public static ThreadPool getInstance() {
    if (instance == null) {
      synchronized (ThreadPool.class) {
        if (instance == null) {
          instance = new ThreadPool();
        }
      }
    }
    return instance;
  }

  /**
   * 添加任务调度工作
   */
  public void push(Runnable thread) {
    executorService.execute(thread);
  }

  /**
   * 添加任务调度工作
   */
  public Future<?> push(Callable<?> thread) {
    return executorService.submit(thread);
  }

  /**
   * <p>
   * 停止该调度器
   * </p>
   */
  public void stop() {
    executorService.shutdown();
  }

}

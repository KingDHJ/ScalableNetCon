package com.dhj.scalablenetcon.http;

/**
 * Created by duanhuangjun on 17/2/27.
 */

import android.util.Log;

import com.dhj.scalablenetcon.http.util.CPUutils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的管理类,负责处理线程的执行,多线程的缓存队列存放待执行的线程
 * */
public class ThreadPoolManager {

    private static final String TAG = "KING_DHJ";
    //处理任务的缓存队列,它是堵塞式的队列,如果它里面是空的,就是堵塞
    private LinkedBlockingDeque<FutureTask<?>> taskQueue = new LinkedBlockingDeque<FutureTask<?>>();

    //线程池   第1个参数CPU的核数,2是执行线程最大的队列长度,3是执行一个线程的空闲时间,
    // 4是空闲时间的参数,5堵塞队列的长度,6任务执行被拒绝的处理器
    private final ThreadPoolExecutor threadPoolExecutor;

    private static ThreadPoolManager instance = new ThreadPoolManager();

    /**
     * 全局保证只有一个线程池对象
     * */
    public static ThreadPoolManager getInstance(){
        return instance;
    }

    /**
     * 在构造方法中初始化线程池
     * */
    private ThreadPoolManager() {
        threadPoolExecutor = new ThreadPoolExecutor(CPUutils.getNumCores(),
                10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(4), handler);
        //在创建线程池的时候,就去执行轮询任务队列的线程
        threadPoolExecutor.execute(runnable);
        Log.i(TAG,"创建了线程池");
    }

    /**
     *开启一个线程去轮询堵塞式的任务存放队列
     * */
    private Runnable runnable = new Runnable(){

        @Override
        public void run() {
            while (true){
                FutureTask take = null;
                try {
                    take = (FutureTask)taskQueue.take();
                    if(take!=null){
                        threadPoolExecutor.execute(take);
                        Log.i(TAG,"执行任务");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 暴露一个方法,给创建的HttpTask,添加到任务缓存队列
     * */
    public <T> void excute(FutureTask<T> futureTask) throws InterruptedException {
        taskQueue.put(futureTask);
    }

    private RejectedExecutionHandler handler=new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                taskQueue.put(new FutureTask<Object>(r,null) {
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

}

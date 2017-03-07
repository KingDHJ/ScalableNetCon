package com.dhj.scalablenetcon.http;

/**
 * Created by duanhuangjun on 17/2/27.
 */

import android.util.Log;

import com.dhj.scalablenetcon.http.util.CPUutils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的管理类,负责处理线程的执行,多线程的缓存队列存放待执行的线程
 * */
public class ThreadPoolManager {
    private static final String TAG ="dongnao" ;
    private static  ThreadPoolManager instance=new ThreadPoolManager();

    private LinkedBlockingQueue<Future<?>> taskQuene=new LinkedBlockingQueue<>();

    private ThreadPoolExecutor threadPoolExecutor;

    public static ThreadPoolManager getInstance() {

        return instance;
    }

    private ThreadPoolManager()
    {
        threadPoolExecutor=new ThreadPoolExecutor(4,10,10, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(4), handler);
        threadPoolExecutor.execute(runable);
    }

    private Runnable runable =new Runnable() {
        @Override
        public void run() {
            while (true)
            {
                FutureTask futrueTask=null;

                try {
                    /**
                     * 阻塞式函数
                     */
                    Log.i(TAG,"等待队列     "+taskQuene.size());
                    futrueTask= (FutureTask) taskQuene.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(futrueTask!=null)
                {
                    threadPoolExecutor.execute(futrueTask);
                }
                Log.i(TAG,"线程池大小      "+threadPoolExecutor.getPoolSize());
            }
        }
    };
    public <T> void execte(FutureTask<T> futureTask) throws InterruptedException {
        taskQuene.put(futureTask);
    }

    private RejectedExecutionHandler handler=new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                taskQuene.put(new FutureTask<Object>(r,null) {
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}

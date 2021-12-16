package com.yulin.config;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * @Auther:LinuxTYL
 * @Date:2021/12/16
 * @Description:
 */
@Configuration
@EnableAsync
public class ThreadPoolTaskConfig {

    @Bean("threadPoolExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        ///线程池创建的核⼼线程数，线程池维护线程的最少数ᰁ，即使没有任务需要执⾏，也会⼀直存活
        //如果设置allowCoreThreadTimeout=true（默认false）时，核⼼线程会超时关闭
        executor.setCorePoolSize(8);
        //executor.setAllowCoreThreadTimeOut();


        //缓存队列（阻塞队列）当核⼼线程数达到最⼤时，新任务会放在队列中排队等待执⾏
        executor.setQueueCapacity(124);
        //最⼤线程池数ᰁ，当线程数>=corePoolSize，且任务队列已满时。线程池会创建新线程来处理任务
        //当线程数=maxPoolSize，且任务队列已满时，线程池会拒绝处理任务⽽抛出异常
        executor.setMaxPoolSize(64);

        //当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数ᰁ=corePoolSize
        //如果allowCoreThreadTimeout=true，则会直到线程数ᰁ=0
        executor.setKeepAliveSeconds(30);

        //spring 提供的 ThreadPoolTaskExecutor 线程池，是有setThreadNamePrefix() ⽅法的。
        //jdk 提供的ThreadPoolExecutor 线程池是没有setThreadNamePrefix() ⽅法的
        executor.setThreadNamePrefix("yulin线程池-");

        //rejection-policy：当pool已经达到max size的时候，如何处理新任务
        //CallerRunsPolicy()：交由调⽤⽅线程运⾏，⽐如main 线程；如果添加到线程池失败，那么主线程会⾃⼰去执⾏该任务，不会等待线程池中的线程去执⾏
        //AbortPolicy()：该策略是线程池的默认策略，如果线程池队列满了丢掉这个任务并且抛出RejectedExecutionException异常。
        //DiscardPolicy()：如果线程池队列满了，会直接丢掉这个任务并且不会有任何异常
        //DiscardOldestPolicy()：丢弃队列中最⽼的任务，队列满了，会将最早进⼊队列的任务删掉腾出空间，再尝试加⼊队列
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

}

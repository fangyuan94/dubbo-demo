package com.fc.dubbo.demo.consumer.filter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 性能监控
 */
@Activate(order = 0)
@Slf4j
public class TPMonitorFilter implements Filter {

    private final ScheduledExecutorService monitorScheduler;

    //总共耗时
    private final   Map<String, List<Long>> timers = new HashMap<>(16);

    //每多少秒执行
    private final long renewalIntervalInSecs = 5l;

    //初始时间
    private final long initialDelay = 2l;

    //tp90
    private final Integer TP90= 90;

    private final Integer TP99= 99;

    public TPMonitorFilter(){
        this.monitorScheduler =  Executors.newScheduledThreadPool(2, (new ThreadFactoryBuilder()).setNameFormat("MonitorScheduler-%d").setDaemon(true).build());
        //启动监听器
        this.monitorScheduler.scheduleWithFixedDelay(new MonitorThread(timers),initialDelay, renewalIntervalInSecs, TimeUnit.SECONDS);
    }

    class MonitorThread implements Runnable{


        public final Map<String, List<Long>> timers;

        //队列记录 30个bucket 每一个桶记录5s的数据
        public final Queue<Bucket> bucketQueue = new LinkedList<Bucket>();
        //排序比较器
        public final Comparator<Long> comparator;

        MonitorThread(Map<String, List<Long>> timers) {
            this.timers = timers;
            this.comparator = (l1,l2)-> {
                return l1.compareTo(l2);
            };
        }

        @Override
        public void run() {
            log.info("monitor监控==============");
            Bucket bucket = new Bucket();
            //copy新数据
            Map<String, List<Long>> newTimers = new HashMap<>(timers.size());
            newTimers.putAll(timers);
            //清除之前数据
            timers.clear();
            bucket.setTimer(newTimers);

            //对这5s内的数据进行循环排序
            newTimers.forEach((k,v)->{
                v.sort(comparator);
            });
            bucketQueue.add(bucket);
            //写进队列
            if(bucketQueue.size()>12){
                //保证队列只有1min钟数据 这个可以动态变更 超过移除header
                bucketQueue.poll();
            }
            //记录
            Map<String,List<Long>> result = new HashMap<>();

            //打印统计这1min内对应的 top
            bucketQueue.forEach(bucket1 -> {

                bucket1.getTimer().forEach((k,timers)->{
                    List<Long> newTime = result.get(k);
                    if(newTime == null){
                        newTime = new ArrayList<>();
                    }
                    newTime = sort(newTime,timers);
                    result.put(k,newTime);
                });

            });

            //统计这1min内对应的 top
            result.forEach((k,v)->{
                int length = v.size();
                //计算对应的最低位数
                Integer tp90 = (length * TP90)/100;
                Integer tp99 = (length * TP99)/100;

                log.info("==========={}服务对应TP90:{}ms,TP99:{}ms============",k,v.get(tp90),v.get(tp99));
            });

        }
    }

    /**
     * 两个有序list 通过归并排序来进行排序 时间复杂度为O(nlogn)
     * @param newTime
     * @param timers
     * @return
     */
    private  List<Long> sort(List<Long> newTime, List<Long> timers) {

        int aLength = newTime.size(), bLength = timers.size();
        List<Long> mergeList = new ArrayList();
        int i = 0, j = 0;
        while (aLength > i && bLength > j) {
            if (newTime.get(i) > timers.get(j)) {
                mergeList.add(i + j, timers.get(j));
                j++;
            } else {
                mergeList.add(i + j, newTime.get(i));
                i++;
            }
        }
        // blist元素已排好序， alist还有剩余元素
        while (aLength > i) {
            mergeList.add(i + j, newTime.get(i));
            i++;
        }
        // alist元素已排好序， blist还有剩余元素
        while (bLength > j) {
            mergeList.add(i + j, timers.get(j));
            j++;
        }
        return mergeList;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {


        long startTime = System.currentTimeMillis();
        //记录开始时间
        Result invokeRs = invoker.invoke(invocation);
        //记录结束时间
        long endTime = System.currentTimeMillis();
        //接口耗时
        long currentTime = endTime - startTime;
        //获取方法名
        String key = invocation.getServiceName() +"_"+invocation.getMethodName();

        List<Long> timer = timers.get(key);
        if(timer == null){
            timer = new ArrayList<>();
        }
        timer.add(currentTime);
        timers.put(key,timer);

        return invokeRs;
    }


    /**
     * 记录5s内访问的数据
     */
    @Data
    class Bucket{
        //还可以记录其它属性
        private  Map<String, List<Long>> timer;
    }
}

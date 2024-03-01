package com.yupi.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@RestController
@RequestMapping(value = "/queue",produces = "application/json" )
@Slf4j
@Profile({"dev", "local" , "test"})
public class QueueController {

    @Resource
    ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String name) {
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            log.info("任务执行中" + name + "执行人:" + Thread.currentThread().getName());
            try {
                // 让线程休眠 10 分钟，模拟长时间运行的项目
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, threadPoolExecutor);
    }

    @GetMapping("/get")
    public String get(){
        // 创建一个 HashMap 存储线程池的状态信息
        HashMap<String, Object> map = new HashMap<>();
        // 获取线程池的队列长度
        int size = threadPoolExecutor.getQueue().size();
        map.put("线程长度", size);
        // 线程池中的任务个数
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("任务个数", taskCount);
        // 获取线程池中已完成的任务书
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已完成任务数" , completedTaskCount);
        // 获取线程池中正在执行的任务书
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("正在工作的线程数", activeCount);
        return JSONUtil.toJsonStr(map);
    }
}

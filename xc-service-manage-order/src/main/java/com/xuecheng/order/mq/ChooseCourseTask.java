package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.dao.XcTaskRepository;
import com.xuecheng.order.service.TaskService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: likui
 * @Date: 2019/7/20 14:43
 * @Description:
 */
@Component
public class ChooseCourseTask {
    @Autowired
    private TaskService taskService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private XcTaskRepository taskRepository;

    //@Scheduled(cron = "0/10 * * * * *")
    public void sendChoosecourseTask() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();
        Date date = new Date();
        List<XcTask> taskList = taskService.findTaskList(time, 1000);
        for (XcTask xcTask : taskList) {
            int count = taskService.getTask(xcTask.getId(), xcTask.getVersion());
            if (count > 0) {
                publish(xcTask, xcTask.getMqExchange(), xcTask.getMqRoutingkey());
            }
        }
    }

    public void publish(XcTask xcTask, String ex, String routingKey) {
        //把最新的更新时间传给学习服务
        xcTask.setUpdateTime(new Date());
        rabbitTemplate.convertAndSend(ex, routingKey, xcTask);
        taskRepository.save(xcTask);

    }

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChoosecourseTask(XcTask xcTask){
        String id = xcTask.getId();
        taskService.finishTask(id);
    }

    //    @Scheduled(cron = "0/3 * * ? * ?")
//    @Scheduled(fixedDelay = 3000)
//    @Scheduled(fixedRate = 3000)
    public void task1() {
        long l = System.currentTimeMillis();
        System.out.println("start task1...0");
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long l1 = System.currentTimeMillis();
        long l2 = (l1 - l) / 1000;
        System.out.println("end task1..." + l2);
    }

    //    @Scheduled(cron="0/3 * * * * *")
//    @Scheduled(fixedDelay = 3000)
    public void task2() {
        long l = System.currentTimeMillis();
        System.out.println("start task2...0");
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long l1 = System.currentTimeMillis();
        long l2 = (l1 - l) / 1000;
        System.out.println("end task2..." + l2);
    }
}

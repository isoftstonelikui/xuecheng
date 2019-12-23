package com.xuecheng.learning.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Auther: likui
 * @Date: 2019/7/21 09:34
 * @Description:
 */
@Component
public class ChooseCourseTask {
    @Autowired
    private CourseLearningService courseLearningService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 接收选课任务
     * @param xcTask
     */
    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE)
    public void receiveChoosecourseTask(XcTask xcTask) {
        //String userId, String courseId, String valid, Date startTime, Date endTime, XcTask xcTask
        String requestBody = xcTask.getRequestBody();
        Map map = JSON.parseObject(requestBody, Map.class);
        String userId = (String) map.get("userId");
        String courseId = (String) map.get("courseId");
        String valid = (String) map.get("valid");
        Date startTime = null;
        Date endTime = null;
        SimpleDateFormat dateFormat=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        if(map.get("startTime")!=null){
            try {
                startTime =dateFormat.parse((String) map.get("startTime"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(map.get("endTime")!=null){
            try {
                endTime =dateFormat.parse((String) map.get("endTime"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        ResponseResult responseResult = courseLearningService.addcourse(userId, courseId, valid, startTime, endTime, xcTask);
        if(responseResult.isSuccess()){
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE,
                    RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY, xcTask);
        }
    }
}

package com.xuecheng.learning.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Auther: likui
 * @Date: 2019/6/25 22:01
 * @Description: 此处只需定义接口，Feign会自动生成代理对象
 */
@FeignClient(XcServiceList.XC_SERVICE_MANAGE_COURSE)
public interface CourseSearchClient {

    @GetMapping("/course/getmedia/{teachplanId}")
    TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId);
}

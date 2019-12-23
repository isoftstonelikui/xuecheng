package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @Auther: likui
 * @Date: 2019/4/11 20:53
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFeign {
    @Autowired
    private CmsPageClient cmsPageClient; //接口代理对象，由feign生成

    @Test
    public void testFeign() {
        for (int i = 0; i < 10; i++) {
            CmsPage cmsPage = cmsPageClient.findCmsPageById("5a795ac7dd573c04508f3a56");
            System.out.println(cmsPage);
        }
    }
}

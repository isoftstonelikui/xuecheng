package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: likui
 * @Date: 2019/7/10 21:32
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedis() {
        //指定key
        String key = "009274ad-d6ad-44d3-91cc-f55f1882a4e9";
        //定义value
        Map value=new HashMap();
        value.put("jwt", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU2MjgwOTI5MiwianRpIjoiMDA5Mjc0YWQtZDZhZC00NGQzLTkxY2MtZjU1ZjE4ODJhNGU5IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.gh4Y8vvVaydhrpnYO7b0jUPoerhuMKGvvO9uuj_LUuSOPdSxModgLLNzBMvZk9SXzCXNUxuWl-KoHRGs-MNXCfiMMD11NqIpBj8fUhv5QsmAY2dSSAxVlh-7SrcO34TWpHD_XgXNEMDq-bGfWvXGIQyZtkx2e55Pxyx3JTkbp3QhXMzRJ1vD8j_RJpB6ksM30jSc10DLNWiJieIZzNHsX1_Nw6vX0XxqatSrL_0oeyKM5M6Nl--2LGUWmhUVYnHnD3cMNj7kl3A8NVlBvahOIvdwrBPRsbA-Fqo4aQOBKLB18Csf3SJ37jm7e-UGGfEhNrmcy0L_GgLNmi5hcqtWjg");
        value.put("refresh_token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiIwMDkyNzRhZC1kNmFkLTQ0ZDMtOTFjYy1mNTVmMTg4MmE0ZTkiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU2MjgwOTI5MiwianRpIjoiMjhlNWU2MjUtY2EyYy00NThkLWIyZWYtZDNmNmRmNDJkODc3IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.ClbR6Ci_VIvZ9ontpTyRZJzFEadlW_ZxqUJNIS8WQ78c4UCiLg5ctUWJlY_izSqa2z2q67wsbMEMnc6VLf9Ow0BbuIf7PV_4WeI7is6evKnc1zO3olA0q7OCoBb9ruvfWKmOHdUBR0n3sBzSWav-5dNg6_c_E8lZ8lng6uWsMJ1TJlW1azO5LckoNA5QAtFAvANXjBvpYcOrnnHu51WQiShpoO-kYjIwFEIiQ7zSzV8zSgjy2w65IbBrBTdCFjJN_QFKarBs64-q0fktKo9Ak_-7XnnFCNulRh5qP2ltuviBVO1PPADTv5b7JCMZNVDJDWSt9PM6UmMaN7yWwPeiow");
        String jsonString = JSON.toJSONString(value);
        //存储数据
        redisTemplate.boundValueOps(key).set(jsonString, 300, TimeUnit.SECONDS);
        //获取数据
        String string = redisTemplate.opsForValue().get(key);
        System.out.println(string);
    }

    @Test
    public void testSave(){
        String key = "user_token:" + "aaa";
        redisTemplate.boundValueOps(key).set("bbb", 100, TimeUnit.SECONDS);
    }
}

package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Auther: likui
 * @Date: 2019/7/11 12:31
 * @Description: 测试申请JWT令牌
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Test
    public void testClient() {
        //从eureka中获取地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        //令牌申请地址
        String authUrl = uri + "/auth/oauth/token";

        //远程调用
        //定义headers
        HttpHeaders headers = new HttpHeaders();
        String basic = getBasic("XcWebApp", "XcWebApp");
        headers.add("Authorization", basic);
        //定义body
        MultiValueMap body = new LinkedMultiValueMap();
        body.add("grant_type", "password");
        body.add("username", "itcast");
        body.add("password", "123");
        HttpEntity httpBody = new HttpEntity<>(body, headers);
        //restTemplate设置
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpBody, Map.class);
        Map map = exchange.getBody();
        System.out.println(map);
    }

    //获取httpbasic串
    private String getBasic(String clientId, String clientSecret) {
        String str = clientId + ":" + clientSecret;
        //base64编码
        byte[] encode = Base64Utils.encode(str.getBytes());
        return "Basic " + new String(encode);
    }
}

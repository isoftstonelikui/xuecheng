package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: likui
 * @Date: 2019/7/12 18:53
 * @Description:
 */
@Service
public class AuthService {
    @Value("${auth.tokenValiditySeconds}")
    private long expireTime;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private Logger logger= LoggerFactory.getLogger(AuthToken.class);


    //用户申请认证，存储令牌到redis
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        String access_token = authToken.getAccess_token();
        String content = JSON.toJSONString(authToken);
        this.saveToken(access_token, content, expireTime);
        return authToken;
    }

    //申请令牌
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        //从eureka中获取地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        //令牌申请地址
        String authUrl = uri + "/auth/oauth/token";

        //远程调用
        //定义headers
        HttpHeaders headers = new HttpHeaders();
        String basic = getBasic(clientId, clientSecret);
        headers.add("Authorization", basic);
        //定义body
        MultiValueMap body = new LinkedMultiValueMap();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
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
        Map bodyMap = exchange.getBody();
        //解析拿到的凭证
        if (Objects.isNull(bodyMap)
                || Objects.isNull(bodyMap.get("jti"))
                || Objects.isNull(bodyMap.get("access_token"))
                || Objects.isNull(bodyMap.get("refresh_token"))) {
            String error = (String) bodyMap.get("error");
            if(error.equals("unauthorized")||error.equals("invalid_grant")){
                ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
            }

            return null;

        }

        //构造返回值
        AuthToken authToken = new AuthToken();
        authToken.setAccess_token((String) bodyMap.get("jti"));
        authToken.setJwt_token((String) bodyMap.get("access_token"));
        authToken.setRefresh_token((String) bodyMap.get("refresh_token"));
        return authToken;
    }

    //令牌存入redis
    private boolean saveToken(String access_token, String content, long ttl) {
        String key = "user_token:" + access_token;
        redisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (java.util.Objects.isNull(expire) || expire < 0) {
            return false;
        }
        return true;
    }

    //获取httpbasic串
    private String getBasic(String clientId, String clientSecret) {
        String str = clientId + ":" + clientSecret;
        //base64编码
        byte[] encode = Base64Utils.encode(str.getBytes());
        return "Basic " + new String(encode);
    }

    //从redis查询用户jwt令牌信息
    public AuthToken getUserToken(String access_token) {
        String key="user_token:"+access_token;
        String jwt = redisTemplate.opsForValue().get(key);
        AuthToken authToken=null;
        if(!StringUtils.isEmpty(jwt)){
            try {
                authToken=JSON.parseObject(jwt, AuthToken.class);
            }catch (Exception e){
                logger.error("parse json to AuthToken failed,{}",e.getMessage());
            }

        }
        return authToken;
    }

    //删除redis中数据
    public void delToken(String access_token) {
     String key="user_token:"+access_token;
     redisTemplate.delete(key);
    }
}

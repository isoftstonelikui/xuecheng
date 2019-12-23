package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: likui
 * @Date: 2019/7/15 21:27
 * @Description:
 */
@Service
public class AuthService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    //从cookie中获取access_token
    public String getTokenFromCookie(HttpServletRequest request) {
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        String access_token = map.get("uid");
        if (StringUtils.isEmpty(access_token)) {
            return null;
        }
        return access_token;
    }

    //从header中查询jwt令牌
    public String getJwtFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)
                || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String jwt = authorization.substring(7);
        return jwt;
    }

    //查询redis中令牌是否存在
    public boolean isExpire(String access_token) {
        String user_token="user_token:"+access_token;
        Long expire = redisTemplate.getExpire(user_token, TimeUnit.SECONDS);
        if (Objects.isNull(expire) || expire < 0) {
            return false;
        }
        return true;
    }
}

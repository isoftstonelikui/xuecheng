package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: likui
 * @Date: 2019/7/15 20:15
 * @Description: 自定义过滤器
 */
@Component
public class LoginFilter extends ZuulFilter {
    @Autowired
    private AuthService authService;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String tokenFromCookie = authService.getTokenFromCookie(request);
        if(StringUtils.isEmpty(tokenFromCookie)){
            accessDenied(requestContext);
            return null;
        }
        String jwtFromHeader = authService.getJwtFromHeader(request);
        if(StringUtils.isEmpty(jwtFromHeader)){
            accessDenied(requestContext);
            return null;
        }
        boolean expire = authService.isExpire(tokenFromCookie);
        if(!expire){
            accessDenied(requestContext);
            return null;
        }
        return null;
    }

    //拒绝访问方法
    private void accessDenied(RequestContext requestContext){
        HttpServletResponse response = requestContext.getResponse();
        requestContext.setSendZuulResponse(false);
        requestContext.setResponseStatusCode(200);
        ResponseResult responseResult=new ResponseResult(CommonCode.UNAUTHENTICATED);
        requestContext.setResponseBody(JSON.toJSONString(responseResult));
        response.setContentType("application/json;charset=utf-8");
    }
}

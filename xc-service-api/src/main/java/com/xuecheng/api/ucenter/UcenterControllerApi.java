package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Auther: likui
 * @Date: 2019/7/13 09:07
 * @Description: 用户中心接口
 */
@Api(value = "用户中心", description = "用户中心管理")
public interface UcenterControllerApi {
    @ApiOperation("根据用户名查询用户信息")
    XcUserExt getUserExt(String username);
}

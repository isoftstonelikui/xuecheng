package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "数据字典接口", description = "提供数据字典的管理、查询功能")
public interface SysDictionaryControllerApi {

    @ApiOperation("数据字典查询接口")
    SysDictionary findByType(String type);
}

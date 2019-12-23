package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Auther: likui
 * @Date: 2019/6/22 08:35
 * @Description: 媒资文件管理
 */
@Api(value = "媒资文件管理", description = "媒资文件管理接口")
public interface MediaFileControllerApi {

    @ApiOperation("查询文件列表")
    public QueryResponseResult findList(int page,
                                        int size,
                                        QueryMediaFileRequest queryMediaFileRequest);
}

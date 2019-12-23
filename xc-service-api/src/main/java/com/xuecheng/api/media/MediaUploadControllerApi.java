package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: likui
 * @Date: 2019/6/16 07:00
 * @Description: 媒资管理接口
 */
@Api(value = "媒资管理接口", description = "媒资管理接口，提供文件上传、处理等接口")
public interface MediaUploadControllerApi {
    @ApiOperation("文件上传注册")
    ResponseResult register(String fileMd5,
                            String fileName,
                            Long fileSize,
                            String mimetype,
                            String fileExt);

    @ApiOperation("校验分块文件是否存在")
    CheckChunkResult checkChunk(String fileMd5,
                                Integer chunk,
                                Long chunkSize);

    @ApiOperation("上传分块")
    ResponseResult uploadChunk(MultipartFile file,
                               String fileMd5,
                               Integer chunk);

    @ApiOperation("合并分块")
    ResponseResult mergeChunks(String fileMd5,
                               String fileName,
                               Long fileSize,
                               String mimetype,
                               String fileExt);

}

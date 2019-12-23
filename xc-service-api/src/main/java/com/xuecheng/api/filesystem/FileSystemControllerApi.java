package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Auther: likui
 * @Date: 2019/4/3 19:10
 * @Description:
 */
@Api(value = "文件系统管理接口", description = "文件管理接口，提供增删改查")
public interface FileSystemControllerApi {
    /**
     * @param multipartFile
     * @param businesskey
     * @param filetag
     * @param metadata,json数据
     * @return
     * @description 上传文件
     */
    @ApiOperation("上传文件接口")
    UploadFileResult upload(MultipartFile multipartFile,
                            String businesskey, String filetag, String metadata);


}

package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.utils.MongoUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @Auther: likui
 * @Date: 2019/4/3 19:27
 * @Description:
 */
@Service
public class FileSystemService {
    @Autowired
    private FileSystemRepository fileSystemRepository;

    public UploadFileResult upload(MultipartFile multipartFile,
                                   String businesskey, String filetag, String metadata) {

        //将文件存入fastDFS中，返回文件id
        if (Objects.isNull(multipartFile)) {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        String fileId = null;
        try {
            fileId = fast_upload(multipartFile);
        } catch (MyException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(fileId)) {
            ExceptionCast.cast(FileSystemCode.FS_INIT_FAST_FAILED);
        }
        //将文件id和其他信息存入mongodb中
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setFiletag(filetag);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileType(multipartFile.getContentType());
        if (StringUtils.isNotEmpty(metadata)) {
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        }

        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS, fileSystem);

    }

    //上传文件到fastDFS
    private String fast_upload(MultipartFile multipartFile) throws MyException {

        try {
            StorageClient1 storageClient1 = MongoUtils.getStorageClient1();
            if (Objects.isNull(storageClient1)) {
                return null;
            }
            //获取参数
            byte[] bytes = multipartFile.getBytes();
            String originalFilename = multipartFile.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileId = storageClient1.upload_file1(bytes, extName, null);
            return fileId;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

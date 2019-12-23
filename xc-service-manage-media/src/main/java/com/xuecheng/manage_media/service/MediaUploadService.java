package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Auther: likui
 * @Date: 2019/6/16 07:54
 * @Description:
 */
@Service
public class MediaUploadService {
    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.upload-location}")
    private String upload_location;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    private String routingkey_media_video;

    /**
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     * @description 检查文件是否存在
     */
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //服务器路径
        //磁盘文件目录
        String floderPath = upload_location + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        //文件路径
        String filePath = floderPath + fileMd5 + "." + fileExt;
        File file = new File(filePath);
        //文件是否存在，此处逻辑待完善
        boolean exists = file.exists();
        Optional<MediaFile> mediaFile = mediaFileRepository.findById(fileMd5);
        if (exists && mediaFile.isPresent()) {
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //检查文件目录是否存在
        File fileFloder = new File(floderPath);
        if (!fileFloder.exists()) {
            file.mkdirs();
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @param fileMd5
     * @param chunk
     * @param chunkSize
     * @return
     * @description 检查块文件是否存在
     */
    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Long chunkSize) {
        //块文件目录
        String chunkFloderPath = upload_location + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunks/";
        //块文件
        File chunkFile = new File(chunkFloderPath + chunk);
        if (chunkFile.exists()) {
            return new CheckChunkResult(CommonCode.SUCCESS, true);
        }
        return new CheckChunkResult(CommonCode.SUCCESS, false);

    }

    /**
     * @param file
     * @param fileMd5
     * @param chunk
     * @return
     * @description 上传块文件
     */
    public ResponseResult uploadChunk(MultipartFile file, String fileMd5, Integer chunk) {
        //块文件目录
        String chunkFloderPath = upload_location + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunks/";
        File chunkFolder = new File(chunkFloderPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(new File(chunkFloderPath + chunk));
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     * @description 合并文件
     */
    public ResponseResult mergeChunk(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //合并所有分块
        String chunkFloderPath = upload_location + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/chunks/";
        File chunkFloder = new File(chunkFloderPath);
        if (!chunkFloder.exists()) {
            chunkFloder.mkdirs();
        }
        File mergeFile = new File(upload_location + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + "." + fileExt);
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //获取排好序的文件列表
        List<File> fileList = getChunkFiles(chunkFloder);
        //合并块文件列表
        mergeFile = mergeFile(mergeFile, fileList);
        //校验md5和前端传入值是否一致
        boolean checkFileMd5 = checkFileMd5(mergeFile, fileMd5);
        if (!checkFileMd5) {
            return new ResponseResult(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //文件信息写入mongodb
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5 + "." + fileExt);
        mediaFile.setFileOriginalName(fileName);
        mediaFile.setFilePath(fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/");//文件相对路径
        mediaFile.setFileSize(fileSize);
        mediaFile.setFileStatus("301002");//表示上传成功
        mediaFile.setFileType(fileExt);
        mediaFile.setMimeType(mimetype);
        //保存
        MediaFile save = mediaFileRepository.save(mediaFile);
        //向MQ发送视频处理消息
        sendProcessVideoMsg(mediaFile.getFileId());
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @param mediaId
     * @description 向MQ发送视频处理消息
     */
    public ResponseResult sendProcessVideoMsg(String mediaId) {
        Map<String,String> msgMap=new HashMap();
        msgMap.put("mediaId", mediaId);
        String msg = JSON.toJSONString(msgMap);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK, routingkey_media_video, msg);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    private List<File> getChunkFiles(File chunkFolder) {
        File[] files = chunkFolder.listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                    return 1;
                }
                return -1;
            }
        });
        return fileList;
    }

    private File mergeFile(File mergeFile, List<File> fileList) {
        try {
            RandomAccessFile write_file = new RandomAccessFile(mergeFile, "rw");
            byte[] bytes = new byte[1024];
            for (File file : fileList) {
                RandomAccessFile read_file = new RandomAccessFile(file, "r");
                int len = -1;
                while ((len = read_file.read(bytes)) != -1) {
                    write_file.write(bytes, 0, len);
                }
                read_file.close();
            }
            write_file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mergeFile;
    }

    private boolean checkFileMd5(File mergeFile, String md5) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(mergeFile);
            String md5Hex = DigestUtils.md5Hex(inputStream);
            if (md5.equalsIgnoreCase(md5Hex)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

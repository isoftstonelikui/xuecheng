package com.xuecheng.framework.utils;

import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import java.io.IOException;

/**
 * @Auther: likui
 * @Date: 2019/4/4 21:32
 * @Description:
 */
public class MongoUtils {
    //此处直接赋值
    private static final int CONNECT_TIMEOUT_IN_SECONDS = 5000;
    private static final int NETWORK_TIMEOUT_IN_SECONDS = 300000;
    private static final String CHARSET = "UTF-8";
    private static final String TRACKER_SERVERS = "192.168.25.133:22122";

    //获取storageClient
    public static StorageClient1 getStorageClient1() {
        //初始化fastDFS环境
        try {
            ClientGlobal.initByTrackers(TRACKER_SERVERS);
            ClientGlobal.setG_connect_timeout(CONNECT_TIMEOUT_IN_SECONDS);
            ClientGlobal.setG_network_timeout(NETWORK_TIMEOUT_IN_SECONDS);
            ClientGlobal.setG_charset(CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(CommonCode.FS_INIT_FAST_FAILED);
        }
        //创建trackerclient
        TrackerClient trackerClient = new TrackerClient();
        try {
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
            return storageClient1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

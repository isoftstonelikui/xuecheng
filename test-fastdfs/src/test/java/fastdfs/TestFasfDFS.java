package fastdfs;

import fasfDFS.TestFastDFSApplication;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Auther: likui
 * @Date: 2019/4/1 20:43
 * @Description:
 */
@SpringBootTest(classes = TestFastDFSApplication.class)
@RunWith(SpringRunner.class)
public class TestFasfDFS {

    //上传测试
    @Test
    public void testUpload() {
        //加载配置文件
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建tracker客户端
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //创建storageClie
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
            String file_path = "C:\\Users\\Li Kui\\Pictures\\Saved Pictures\\pic1.jpg";
            String fileId = storageClient1.upload_file1(file_path, "jpg", null);
            System.out.println(fileId);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //下载测试
    @Test
    public void testDownload() {
        //加载配置文件
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建tracker客户端
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //创建storageClie
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
            byte[] bytes = storageClient1.download_file1("group1/M00/00/00/wKgZhVyi516ASFUxAAIsupMrrB8573.jpg");
            FileOutputStream fileOutputStream=new FileOutputStream(new File("C:\\Users\\Li Kui\\Pictures\\Saved Pictures\\pic1_download.jpg"));
            fileOutputStream.write(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

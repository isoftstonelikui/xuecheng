package fastdfs;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: likui
 * @Date: 2019/5/3 17:18
 * @Description:
 */
public class JavaDemo {
    public static void main(String[] args) {
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println();
            }
        });
        t1.start();
        System.out.println("main end");
    }

}

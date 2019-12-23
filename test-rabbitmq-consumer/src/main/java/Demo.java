import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @Auther: likui
 * @Date: 2019/9/21 10:48
 * @Description:
 */
public class Demo {
    public static void main(String[] args) {
        String url="http://192.3/ss/g1/0/5.234/qos";
        try {
            String encode = URLEncoder.encode("g1/0/5.234", "UTF-8");
            System.out.println(encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
}

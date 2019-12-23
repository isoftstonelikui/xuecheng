package freemarker;

import com.xuecheng.test.freemarker.FreemarkerApplication;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = FreemarkerApplication.class)
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    //测试静态化，基于ftl模板生成html文件
    @Test
    public void testGenerateHtml() throws IOException, TemplateException {
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板所在的路径
        String classPath = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classPath + "/templates/"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板文件
        Template template = configuration.getTemplate("test1.ftl");
        //创建数据模型
        Map map = new HashMap();
        map.put("name", "张三");
        map.put("age", 20);
        //创建输出流对象
        Writer out = new FileWriter(new File("E:\\freemarker_test\\test.html"));
        //合并输出
        template.process(map, out);
        //关闭输出流对象
        out.close();
    }
}

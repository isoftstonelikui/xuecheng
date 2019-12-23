package freemarker;

import org.junit.Test;

/**
 * @Auther: likui
 * @Date: 2019/4/4 12:58
 * @Description:
 */
public class Demo {

    @Test
    public void demo1() {
        int i = get(-1);
        System.out.println(i);
    }

    private int get(int i) {
        return i > 0 ? i : 0;
    }
}

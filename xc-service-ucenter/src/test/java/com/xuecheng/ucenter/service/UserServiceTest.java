package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Auther: likui
 * @Date: 2019/7/17 08:16
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @Autowired
    private XcMenuMapper xcMenuMapper;
    @Test
    public void getPermission(){
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId("49");
    }
}

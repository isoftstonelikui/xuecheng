package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Auther: likui
 * @Date: 2019/7/13 09:52
 * @Description:
 */
public interface XcUserRepository extends JpaRepository<XcUser,String> {
    //根据用户名查询用户信息
    XcUser findByUsername(String username);
}

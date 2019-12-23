package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Auther: likui
 * @Date: 2019/7/13 09:54
 * @Description:
 */
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser ,String> {
    //根据用户id查询用户公司信息
    XcCompanyUser findByUserId(String userId);
}

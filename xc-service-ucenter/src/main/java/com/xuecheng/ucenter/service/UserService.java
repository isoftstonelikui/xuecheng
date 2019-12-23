package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: likui
 * @Date: 2019/7/13 09:56
 * @Description:
 */
@Service
public class UserService {
    @Autowired
    private XcUserRepository xcUserRepository;
    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;
    @Autowired
    private XcMenuMapper xcMenuMapper;

    public XcUser findXcUserByUsername(String username) {
        return xcUserRepository.findByUsername(username);
    }

    public XcUserExt getUserExt(String username) {
        XcUser xcUser = findXcUserByUsername(username);
        if (Objects.isNull(xcUser)) {
            return null;
        }
        //构造返回值
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        String userId = xcUser.getId();

        XcCompanyUser companyUser = xcCompanyUserRepository.findByUserId(userId);
        if (Objects.nonNull(companyUser)) {
            xcUserExt.setCompanyId(companyUser.getCompanyId());
        }
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);
        xcUserExt.setPermissions(xcMenus);
        return xcUserExt;
    }

}

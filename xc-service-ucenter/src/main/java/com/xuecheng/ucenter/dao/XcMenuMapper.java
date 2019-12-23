package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Auther: likui
 * @Date: 2019/7/17 07:19
 * @Description:
 */
@Mapper
public interface XcMenuMapper {
    List<XcMenu> selectPermissionByUserId(String userId);
}

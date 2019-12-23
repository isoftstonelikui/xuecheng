package com.xuecheng.manage_course.service;


import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: likui
 * @Date: 2019/3/30 12:23
 * @Description:
 */
@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    //课程分类查询
    public CategoryNode findList() {
        return categoryMapper.findList();
    }

}

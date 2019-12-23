package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    @ApiOperation("课程分类查询")
    CategoryNode findList();
}

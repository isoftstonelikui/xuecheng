package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeachPlanMapper {
    @ApiOperation("课程计划查询")
    TeachplanNode selectList(String courseId);
}

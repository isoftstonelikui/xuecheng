package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Auther: likui
 * @Date: 2019/7/21 09:21
 * @Description:
 */
public interface XcLearningCourseRepository extends JpaRepository<XcLearningCourse, String> {
    //根据课程id和用户id查询
    XcLearningCourse findByCourseIdAndUserId(String courseId, String userId);
}

package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Auther: likui
 * @Date: 2019/6/23 09:14
 * @Description:
 */
public interface TeachPlanMediaRepository extends JpaRepository<TeachplanMedia,String> {

    List<TeachplanMedia> findByCourseId(String courseId);
}

package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Auther: likui
 * @Date: 2019/6/23 09:14
 * @Description:
 */
public interface TeachPlanMediaPubRepository extends JpaRepository<TeachplanMediaPub, String> {

    long deleteByCourseId(String courseId);
}

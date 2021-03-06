package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator.
 */
public interface TeachPlanRepository extends JpaRepository<Teachplan, String> {
    List<Teachplan> findByCourseidAndParentid(String couseId, String parentId);
}

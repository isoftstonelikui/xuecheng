package com.xuecheng.order.dao;


import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Auther: likui
 * @Date: 2019/7/20 17:31
 * @Description:
 */
public interface XcTaskRepository extends JpaRepository<XcTask, String> {
    Page<XcTask> findByUpdateTimeBefore(Date updateTime, Pageable pageable);

    @Modifying
    @Transactional //@Query方式默认为只读，故需要添加@Modifying和transactional注解
    @Query("update XcTask t set t.version=:version+1 where t.id=:id and t.version=:version")
    int updateTaskVersion(@Param("id") String id, @Param("version") int version);
}

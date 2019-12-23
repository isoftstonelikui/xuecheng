package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Administrator.
 */
public interface CoursePicMongoRepository extends MongoRepository<FileSystem, String> {

}

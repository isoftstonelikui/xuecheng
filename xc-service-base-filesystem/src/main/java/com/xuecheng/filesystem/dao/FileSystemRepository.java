package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Auther: likui
 * @Date: 2019/4/3 19:26
 * @Description:
 */
public interface FileSystemRepository extends MongoRepository<FileSystem, String> {
}

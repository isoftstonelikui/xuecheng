package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmsSiteRepository extends MongoRepository<CmsSite, String> {


}

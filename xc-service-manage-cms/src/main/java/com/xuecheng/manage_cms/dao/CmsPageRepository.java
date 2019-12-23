package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmsPageRepository extends MongoRepository<CmsPage, String> {

    List<CmsPage> findByPageAliase(String pageAliase);

    CmsPage findBySiteIdAndPageNameAndPageWebPath(String siteId, String pageName, String pageWebPath);
}

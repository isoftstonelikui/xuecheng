package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;

@Service
public class PageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    //保存html页面到服务器物理路径
    public void savePageToServePath(String pageId) {
        //查询页面信息cmsPage
        CmsPage cmsPage = getCmsPageById(pageId);
        //获取文件输入流
        String htmlFileId = cmsPage.getHtmlFileId();
        InputStream inputStream = getFileById(htmlFileId);
        if (Objects.isNull(inputStream)) {
            LOGGER.error("inputStream is null,htmlFileId:{}",htmlFileId);
            return;
        }
        //页面保存路径
        CmsSite cmsSite = getCmsSiteById(cmsPage.getSiteId());
        String pagePath = cmsSite.getPagePhysicalPath() + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        //保存到服务器
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    //查询页面信息cmsPage
    private CmsPage getCmsPageById(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    //查询站点信息cmsSite
    private CmsSite getCmsSiteById(String fieldId) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(fieldId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    //获取文件信息输入流
    private InputStream getFileById(String fieldId) {
        GridFSFile gridFSFile = gridFsTemplate
                .findOne(Query.query(Criteria.where("_id").is(fieldId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket
                .openDownloadStream(gridFSFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            LOGGER.info("getInputStream failed");
        }
        return null;
    }
}

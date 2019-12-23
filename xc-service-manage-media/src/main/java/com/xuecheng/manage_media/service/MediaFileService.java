package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: likui
 * @Date: 2019/6/22 08:44
 * @Description:
 */
@Service
public class MediaFileService {
    @Autowired
    private MediaFileRepository mediaFileRepository;

    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        if (Objects.isNull(queryMediaFileRequest)) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        //构建查询匹配器
        MediaFile mediaFile = new MediaFile();
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())) {
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())) {
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())) {
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        //.withIncludeNullValues()无效，待查证
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<MediaFile> example = Example.of(mediaFile, matcher);
        //构建分页参数
        page = page - 1;
        Pageable pageable = new PageRequest(page, size);
        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);
        //构建返回对象
        long totalElements = all.getTotalElements();
        List<MediaFile> content = all.getContent();
        QueryResult<MediaFile> queryResult = new QueryResult<MediaFile>();
        queryResult.setTotal(totalElements);
        queryResult.setList(content);
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }
}

package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.MongoUtils;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CourseMapper;
import com.xuecheng.manage_course.dao.CourseMarketRepository;
import com.xuecheng.manage_course.dao.CoursePicMongoRepository;
import com.xuecheng.manage_course.dao.CoursePicRepository;
import com.xuecheng.manage_course.dao.CoursePubRepository;
import com.xuecheng.manage_course.dao.TeachPlanMapper;
import com.xuecheng.manage_course.dao.TeachPlanMediaPubRepository;
import com.xuecheng.manage_course.dao.TeachPlanMediaRepository;
import com.xuecheng.manage_course.dao.TeachPlanRepository;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @Auther: likui
 * @Date: 2019/3/30 12:23
 * @Description:
 */
@Service
public class CourseService {
    @Autowired
    private TeachPlanMapper teachPlanMapper;
    @Autowired
    private TeachPlanRepository teachPlanRepository;
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseMarketRepository courseMarketRepository;
    @Autowired
    private CoursePicRepository coursePicRepository;
    @Autowired
    private CoursePicMongoRepository coursePicMongoRepository;
    @Autowired
    private CmsPageClient cmsPageClient;
    @Autowired
    private TeachPlanMediaRepository teachPlanMediaRepository;
    @Autowired
    private CoursePubRepository coursePubRepository;
    @Autowired
    private TeachPlanMediaPubRepository teachPlanMediaPubRepository;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    //课程计划查询
    public TeachplanNode findTeachPlanList(String coursrId) {
        return teachPlanMapper.selectList(coursrId);
    }

    //添加课程计划
    @Transactional
    public ResponseResult addTeachPlan(Teachplan teachplan) {
        //参数校验
        if (Objects.isNull(teachplan)
                || StringUtils.isEmpty(teachplan.getCourseid())
                || StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //获取参数
        String courseid = teachplan.getCourseid();
        String parentid = teachplan.getParentid();
        teachplan.setGrade("3");
        //如果parentId为空，需要进行处理
        if (StringUtils.isEmpty(parentid)) {
            //取出课程根节点
            parentid = getTeachplanRoot(courseid);
            teachplan.setGrade("2");
        }
        teachplan.setParentid(parentid);
        teachPlanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //查询课程根节点，如果查询不到需要添加
    private String getTeachplanRoot(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.NOT_EXIST);
        }
        List<Teachplan> teachplanList = teachPlanRepository.findByCourseidAndParentid(courseId, "0");
        if (Objects.isNull(teachplanList) || teachplanList.size() <= 0) {
            //查询不到，自动添加根节点
            Teachplan teachplan = new Teachplan();
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            teachplan.setGrade("1");
            teachplan.setParentid("0");
            teachplan.setPname(optional.get().getName());
            teachPlanRepository.save(teachplan);
            return teachplan.getId();
        }
        //查询到，直接返回
        return teachplanList.get(0).getId();
    }

    //查询我的课程
    public QueryResponseResult<CourseInfo> findCourseList(String companyId, int page, int size,
                                                          CourseListRequest courseListRequest) {

        if (Objects.isNull(courseListRequest)){
            courseListRequest=new CourseListRequest();
        }
        courseListRequest.setCompanyId(companyId);
        //查询数据库
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseList = courseMapper.findCourseList(courseListRequest);
        //设置返回值
        QueryResult queryResult = new QueryResult();
        queryResult.setTotal(courseList.getTotal());
        queryResult.setList(courseList.getResult());
        QueryResponseResult<CourseInfo> queryResponseResult
                = new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;

    }

    //添加课程
    @Transactional
    public ResponseResult addCourse(CourseBase courseBase) {
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //查询课程基础信息
    @Transactional
    public CourseBase getCourseBaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        return optional.get();
    }

    //更新课程基础信息
    @Transactional
    public ResponseResult updateCourse(String id, CourseBase courseBase) {
        courseBase.setId(id);
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取课程营销信息
    @Transactional
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    //更新课程营销信息
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        courseMarket.setId(id);
        courseMarketRepository.save(courseMarket);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @param courseId
     * @param pic
     * @return
     * @description 向课程管理数据库添加课程与图片的关联信息
     */
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        CoursePic coursePic = new CoursePic();
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Transactional
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        CoursePic coursePic = findCoursePic(courseId);
        if (Objects.nonNull(coursePic)) {
            //从mongodb删除
            coursePicMongoRepository.deleteById(coursePic.getPic());
            //从fasfDFS删除
            String pic = coursePic.getPic();
            if (Objects.nonNull(pic)) {
                fast_delete(pic);
            }
        }
        //从mysql删除
        coursePicRepository.deleteById(courseId);
        return new ResponseResult(CommonCode.SUCCESS);

        /*long result = coursePicRepository.deleteByCourseid(courseId);
        return result > 0 ? new ResponseResult(CommonCode.SUCCESS) : new ResponseResult(CommonCode.FAIL);*/
    }

    //从fastDFS删除文件
    private void fast_delete(String fileId) {
        try {
            StorageClient1 storageClient1 = MongoUtils.getStorageClient1();
            storageClient1.delete_file1(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //查询课程视图，包括基本信息、图片、营销信息、课程计划
    public CourseView getCourseView(String id) {
        //定义返回对象
        CourseView courseView = new CourseView();
        //查询课程基本信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()) {
            CourseBase courseBase = courseBaseOptional.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程图片信息
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(id);
        if (coursePicOptional.isPresent()) {
            CoursePic coursePic = coursePicOptional.get();
            courseView.setCoursePic(coursePic);
        }
        //查询课程营销计划信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if (courseMarketOptional.isPresent()) {
            CourseMarket courseMarket = courseMarketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        //查询课程计划信息
        TeachplanNode teachplanNode = teachPlanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }

    //课程预览
    public CoursePublishResult preview(String id) {
        CourseBase courseBase = this.findCourseBaseById(id);

        //发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(id + ".html");
        //页面别名
        cmsPage.setPageAliase(courseBase.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre + id);
        //远程请求cms保存页面信息
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        //页面id
        String pageId = cmsPageResult.getCmsPage().getPageId();
        //页面url
        String pageUrl = previewUrl + pageId;
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);

    }

    //根据id查询课程基本信息
    private CourseBase findCourseBaseById(String courseId) {
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if (baseOptional.isPresent()) {
            CourseBase courseBase = baseOptional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }

    //课程发布
    @Transactional
    public CoursePublishResult publish(String id) {
        //查询课程
        CourseBase courseBase = this.findCourseBaseById(id);
        //构造页面
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(id + ".html");
        //页面别名
        cmsPage.setPageAliase(courseBase.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre + id);
        //将课程详情页面发布到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //更改课程状态为已发布
        CourseBase saveCoursePubState = saveCoursePubState(id, "202002");
        //页面url
        String pageUrl = cmsPostPageResult.getPageUrl();

        saveTeachplanMediaPub(id);
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    //保存课程计划媒资信息
    private void saveTeachplanMediaPub(String courseId) {
        teachPlanMediaPubRepository.deleteByCourseId(courseId);
        List<TeachplanMedia> teachplanMediaList = teachPlanMediaRepository.findByCourseId(courseId);
        List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMediaList) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia, teachplanMediaPub);
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubs.add(teachplanMediaPub);
        }
        teachPlanMediaPubRepository.saveAll(teachplanMediaPubs);
    }

    //更改课程状态
    private CourseBase saveCoursePubState(String courseId, String courseStatus) {
        //查询课程
        CourseBase courseBase = this.findCourseBaseById(courseId);
        courseBase.setStatus(courseStatus);
        CourseBase save = courseBaseRepository.save(courseBase);
        return save;
    }

    //保存课程计划和媒资关系
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia) {
        //参数校验
        if (Objects.isNull(teachplanMedia) || StringUtils.isEmpty(teachplanMedia.getTeachplanId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //查询课程计划
        String teachplanId = teachplanMedia.getTeachplanId();
        Optional<Teachplan> optionalTeachplan = teachPlanRepository.findById(teachplanId);
        if (!optionalTeachplan.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_ISNULL);
        }
        Teachplan teachplan = optionalTeachplan.get();
        if (!"3".equals(teachplan.getGrade())) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        //保存
        TeachplanMedia one = null;
        Optional<TeachplanMedia> optionalTeachplanMedia = teachPlanMediaRepository.findById(teachplanId);
        if (optionalTeachplanMedia.isPresent()) {
            one = optionalTeachplanMedia.get();
        } else {
            one = new TeachplanMedia();
        }
        one.setCourseId(teachplanMedia.getCourseId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        one.setTeachplanId(teachplanId);
        teachPlanMediaRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据课程id查询课程信息
    public Map<String, CoursePub> getAll(String id) {
        Optional<CoursePub> optional = coursePubRepository.findById(id);
        if (optional.isPresent()) {
            Map<String, CoursePub> map = new HashMap<>();
            CoursePub coursePub = optional.get();
            map.put(id, coursePub);
            return map;
        }
        return null;
    }

    //根据课程计划查询媒资信息
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {
        List<TeachplanMediaPub> teachplanMediaPubs = teachPlanMediaPubRepository.findAllById(Arrays.asList(teachplanIds));
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        queryResult.setList(teachplanMediaPubs);
        queryResult.setTotal(teachplanMediaPubs.size());
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }
}

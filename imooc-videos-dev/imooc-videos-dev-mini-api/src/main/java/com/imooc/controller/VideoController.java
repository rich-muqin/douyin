package com.imooc.controller;

import com.imooc.enums.VideoStatusEnum;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Videos;
import com.imooc.service.BgmService;
import com.imooc.service.VideoService;
import com.imooc.utils.FetchVideoCover;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MergeVideoMp3;
import com.imooc.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@Api(value = "Video的接口",tags = {"Video的controller"})
@RequestMapping("/video")
public class VideoController extends BaseController{

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;


    @ApiOperation(value = "用户上传视频", notes = "用户上传视频的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "描述", required = false, dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/upload",headers="content-type=multipart/form-data")
    public IMoocJSONResult uploadVideo(String userId,String bgmId,double videoSeconds,
                                       int videoWidth,int videoHeight,String desc,
                                       @ApiParam(value = "video",required = true) MultipartFile file) throws Exception{

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }

        //保存到数据库中的相对路径
        String uploadPathDB = userId + "/video";
        String coverPath = userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        String finalVideoPath = "";
        try {
            if (file != null ){
                String fileName = file.getOriginalFilename();
                String fileNamePrefix = fileName.split("\\.")[0];

                if(StringUtils.isNoneBlank(fileName)){
                    // 文件上传的最终保存路径
                    finalVideoPath = fileSpace + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    coverPath = coverPath + "/" +fileNamePrefix + ".jpg";
                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                } else {
                    return IMoocJSONResult.errorMsg("上传出错");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");
        }finally {
            if (fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        if (StringUtils.isNotBlank(bgmId)){
            Bgm bgm = bgmService.queryBgmByBgmId(bgmId);
            String mp3InputPath = fileSpace.concat(bgm.getPath());

            MergeVideoMp3 tool = new MergeVideoMp3(ffmpegEXE);
            String videoInputPath = finalVideoPath;
            String videoOutputName = UUID.randomUUID().toString().concat(".mp4");
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            finalVideoPath = fileSpace + uploadPathDB;
            tool.convertor( videoInputPath,  mp3InputPath,videoSeconds,  finalVideoPath);
        }
        System.out.println("uploadPathDB:"+uploadPathDB);
        System.out.println("finalVideoPath:"+finalVideoPath);

        //对视频进行截图
        FetchVideoCover videoInfo = new FetchVideoCover(ffmpegEXE);
        videoInfo.getCover(finalVideoPath,fileSpace+coverPath);


        //保存视频信息
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float)videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());
        video.setCoverPath(coverPath);

        String videoId = videoService.saveVideo(video);
        return IMoocJSONResult.ok(videoId);
    }


    @ApiOperation(value = "用户上传封面", notes = "用户上传封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form")})
    @PostMapping(value = "/uploadCover",headers="content-type=multipart/form-data")
    public IMoocJSONResult uploadCover(String videoId,String userId,
                                       @ApiParam(value = "封面",required = true) MultipartFile file) throws Exception{

        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId) ) {
            return IMoocJSONResult.errorMsg("视频id不能为空和用户ID...");
        }

        //保存到数据库中的相对路径
        String uploadPathDB ="/"+ userId + "/video";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        String finalConverPath = "";
        try {
            if (file != null ){
                String fileName = file.getOriginalFilename();
                if(StringUtils.isNoneBlank(fileName)){
                    // 文件上传的最终保存路径
                    finalConverPath = fileSpace + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalConverPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                } else {
                    return IMoocJSONResult.errorMsg("上传出错");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");
        }finally {
            if (fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        videoService.updateVideo(videoId,uploadPathDB);

        return IMoocJSONResult.ok();
    }


    /**
     *
     * @param video
     * @param isSaveRecord    1 -需要保存  0 - 不需要保存 ，或者为空的时候
     * @param page
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "查询视频", notes = "查询视频的接口")
    @PostMapping(value = "/showAll")
    public IMoocJSONResult showAll(@RequestBody Videos video, Integer isSaveRecord,
                                   Integer page, Integer pageSize) throws Exception {

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedResult result = videoService.getAllVideos(video, isSaveRecord, page, pageSize);
        return IMoocJSONResult.ok(result);
    }


    @ApiOperation(value = "查询热搜", notes = "查询热搜的接口")
    @PostMapping(value = "/hot")
    public IMoocJSONResult hot() throws Exception{
        return  IMoocJSONResult.ok(videoService.getHotwords());
    }




}

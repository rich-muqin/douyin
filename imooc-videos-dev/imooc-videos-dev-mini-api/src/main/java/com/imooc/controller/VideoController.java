package com.imooc.controller;

import com.imooc.service.BgmService;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@RestController
@Api(value = "Video的接口",tags = {"Video的controller"})
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private BgmService bgmService;

    //文件保存的命名空间
    @Value("${fileSpace}")
    private String fileSpace;

    @ApiOperation(value = "用户上传视频", notes = "用户上传视频的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "desc", value = "描述", required = false, dataType = "String", paramType = "query")
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
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (file != null ){
                String fileName = file.getOriginalFilename();
                if(StringUtils.isNoneBlank(fileName)){
                    // 文件上传的最终保存路径
                    String finalVideoPath = fileSpace + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

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
        return IMoocJSONResult.ok(uploadPathDB);
    }

}

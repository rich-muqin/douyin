package com.imooc.service;

import com.imooc.pojo.Videos;

public interface VideoService {

    /**
     * 保存视频
     * @return
     */
    String saveVideo(Videos video);

    void updateVideo(String videoId,String converPath);

}

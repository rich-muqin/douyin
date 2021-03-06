package com.imooc.service;

import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

import java.util.List;

public interface VideoService {

    /**
     * 保存视频
     * @return
     */
    String saveVideo(Videos video);

    void updateVideo(String videoId,String converPath);

    PagedResult getAllVideos(Videos video,Integer isSaveRecord,Integer page,Integer pageSize);

    /**
     * 热搜
     * @return
     */
    List<String> getHotwords();

    /**
     * 用户喜欢
     * @param userId
     * @param videoId
     * @param videoCreateId
     */
    void  userLikeVideo(String userId,String videoId,String videoCreateId);

    /**
     * 用户不喜欢
     * @param userId
     * @param videoId
     * @param videoCreateId
     */
    void  userUnLikeVideo(String userId,String videoId,String videoCreateId);

}

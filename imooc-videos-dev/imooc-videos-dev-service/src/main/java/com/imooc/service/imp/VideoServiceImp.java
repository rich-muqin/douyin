package com.imooc.service.imp;

import com.imooc.mapper.VideosMapper;
import com.imooc.pojo.Videos;
import com.imooc.service.VideoService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class VideoServiceImp implements VideoService {


    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private Sid sid;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String saveVideo(Videos video) {
        String id = sid.nextShort();
        video.setId(id);
         videosMapper.insertSelective(video);
         return id;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVideo(String videoId, String converPath) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(converPath);
        videosMapper.updateByPrimaryKeySelective(videos);
    }
}

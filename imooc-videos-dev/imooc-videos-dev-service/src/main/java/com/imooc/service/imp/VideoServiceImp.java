package com.imooc.service.imp;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.SearchRecordsMapper;
import com.imooc.mapper.VideosMapper;
import com.imooc.mapper.VideosMapperCustom;
import com.imooc.pojo.SearchRecords;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.service.VideoService;
import com.imooc.utils.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class VideoServiceImp implements VideoService {


    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

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
    public PagedResult getAllVideos(Videos video,Integer isSaveRecord,Integer page, Integer pageSize) {

        String desc = video.getVideoDesc();

        //保存热搜词
        if (isSaveRecord != null && isSaveRecord == 1){
            SearchRecords record = new SearchRecords();
            String recordId = sid.nextShort();
            record.setId(recordId);
            record.setContent(desc);
            searchRecordsMapper.insert(record);
        }

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc,null);

        PageInfo<VideosVO> pageList = new PageInfo<>();

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRecords(pageList.getTotal());
        pagedResult.setRows(list);

        return pagedResult;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> getHotwords() {
        return searchRecordsMapper.getHotwords();
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

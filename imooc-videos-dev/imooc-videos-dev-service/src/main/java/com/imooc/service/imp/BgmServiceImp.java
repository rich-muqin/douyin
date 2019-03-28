package com.imooc.service.imp;

import com.imooc.mapper.BgmMapper;
import com.imooc.pojo.Bgm;
import com.imooc.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BgmServiceImp implements BgmService {

    @Autowired
    private BgmMapper BgmMapper;

    @Override
    public List<Bgm> queryBgmList() {
        return BgmMapper.selectAll();
    }
}

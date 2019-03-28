package com.imooc.service;

import com.imooc.pojo.Bgm;

import java.util.List;

public interface BgmService {

    /**
     * 查询bgmlist
     * @return
     */
    List<Bgm> queryBgmList();


    /**
     * 查询bgm
     * @param bgmId
     * @return
     */
    Bgm queryBgmByBgmId(String bgmId);

}

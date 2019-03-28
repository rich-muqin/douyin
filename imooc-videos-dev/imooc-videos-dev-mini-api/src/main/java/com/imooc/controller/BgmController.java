package com.imooc.controller;

import com.imooc.service.BgmService;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "BGN的接口",tags = {"BGM的controller"})
@RequestMapping("/bgm")
public class BgmController {

    @Autowired
    private BgmService bgmService;

    @ApiOperation(value="获取BGM列表", notes="获取BGM的接口")
    @PostMapping("/list")
    public IMoocJSONResult list(){
        return IMoocJSONResult.ok(bgmService.queryBgmList());
    }


}

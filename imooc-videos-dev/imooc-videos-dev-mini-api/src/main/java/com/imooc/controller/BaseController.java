package com.imooc.controller;

import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @Autowired
    public RedisOperator redis;

    //文件保存的命名空间
    @Value("${fileSpace}")
    public String fileSpace;

    @Value("${ffmpegEXE}")
    public String ffmpegEXE;

    public static final String USER_REDIS_SESSION = "user-redis-session";
}

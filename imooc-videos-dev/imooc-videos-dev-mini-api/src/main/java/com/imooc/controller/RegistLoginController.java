package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Api(value = "用户注册登录的接口",tags = {"注册登录的controller"})
@RestController
public class RegistLoginController extends BaseController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册",notes = "用户注册的接口")
    @PostMapping("/regist")
    public IMoocJSONResult regist(@RequestBody Users user){

        //1.判空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }
        //2.判断是否存在
        boolean userNameIsExist = userService.queryUsernameIsExist(user.getUsername());
        try{
            if (!userNameIsExist){
                user.setNickname(user.getUsername());
                user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
                user.setFansCounts(0);
                user.setReceiveLikeCounts(0);
                user.setFollowCounts(0);

            }else {
                return IMoocJSONResult.errorMsg("用户名已存在，请换一个试试！");
            }
        }catch (Exception e ){
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("注册异常!");
        }
        //3.保存用户，注册
        userService.saveUser(user);
//        String uniqueToken = UUID.randomUUID().toString();
//        redis.set(USER_REDIS_SESSION.concat(":").concat(user.getId()),uniqueToken,1000*60*30);
//
//        UsersVO usersVO = new UsersVO();
//        BeanUtils.copyProperties(user,usersVO);
//        usersVO.setUserToken(uniqueToken);
        UsersVO usersVO = setUserRedisSession(user);
        return IMoocJSONResult.ok(usersVO);
    }

    public UsersVO setUserRedisSession(Users user){
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION.concat(":").concat(user.getId()),uniqueToken,1000*60*30);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }

    @ApiOperation(value = "用户登录",notes = "用户登录的接口")
    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users user){
        //1.判空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }
        Users result;
        boolean userNameIsExist = userService.queryUsernameIsExist(user.getUsername());
        if (!userNameIsExist){
            return IMoocJSONResult.errorMsg("用户不存在");
        }
        //2.判断是否存在
        try {
            result = userService.queryUserForLogin(user.getUsername(),MD5Utils.getMD5Str(user.getPassword()));
        }catch (Exception e){
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("登录异常！");
        }
        if (result ==null){
            return IMoocJSONResult.errorMsg("用户名或密码错误！");
        }
        UsersVO usersVO = setUserRedisSession(result);
        return IMoocJSONResult.ok(usersVO);
    }

    @ApiOperation(value = "用户注销",notes = "用户注销的接口")
    @ApiImplicitParam(name="userId",value = "用户id",required = true,dataType = "String",paramType = "query")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId){
       redis.del(USER_REDIS_SESSION.concat(":").concat(userId));
       return IMoocJSONResult.ok();
    }

}

package com.imooc.service;

import com.imooc.pojo.Users;

public interface UserService  {

    /**
     * 判断是否存在
     * @param userName
     * @return
     */
    boolean queryUsernameIsExist(String userName);

    /**
     * 保存
     * @param User
     */
    void saveUser(Users User);

    /**
     * 校验用户登录
     * @param userName ,password
     * @return
     */
    Users queryUserForLogin(String userName,String password);

    /**
     * 更改用户信息
     * @param user
     */
    void updateUserInfo(Users user);

    /**
     * 查询用户详情
     * @param userId
     * @return
     */
    Users queryUserInfo(String userId);
}

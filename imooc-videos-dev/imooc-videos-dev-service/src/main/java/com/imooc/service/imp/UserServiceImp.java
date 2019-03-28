package com.imooc.service.imp;

import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;
@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUsernameIsExist(String userName) {
        Users user = new Users();
        user.setUsername(userName);
        Users result = usersMapper.selectOne(user);
        return result == null ? false : true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(Users user) {
        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserForLogin(String userName,String password){
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username",userName);
        criteria.andEqualTo("password",password);
        Users result = usersMapper.selectOneByExample(userExample);
        return  result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserInfo(Users user){

        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",user.getId());
        usersMapper.updateByExampleSelective(user,userExample);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserInfo(String userId) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",userId);
        Users result = usersMapper.selectOneByExample(userExample);

        return result;
    }
}

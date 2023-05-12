package cn.yiport.service.impl;

import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.User;
import cn.yiport.domain.vo.UserInfoVo;
import cn.yiport.mapper.UserMapper;
import cn.yiport.service.UserService;
import cn.yiport.utils.BeanCopyUtils;
import cn.yiport.utils.SecurityUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户表(User)表服务实现类
 *
 * @author yiport
 * @since 2023-05-01 13:50:17
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
        User user = getById(userId);
        //封装成UserInfoVo
        UserInfoVo vo = BeanCopyUtils.copyBean(user,UserInfoVo.class);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }


}


package com.yiport.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiport.domain.entity.User;


/**
 * 用户表(User)表数据库访问层
 *
 * @author YiPort
 * @since 2023-04-06 20:23:46
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 通过uid区间标识（usedFor）分配一个 UID。
     * @param usedFor
     * @return
     */
    Long getUidForRegister(String usedFor);

}

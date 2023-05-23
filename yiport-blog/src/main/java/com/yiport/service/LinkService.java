package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.Link;

/**
 * 友链(Link)表服务接口
 *
 * @author YiPort
 * @since 2023-04-06 12:14:21
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();
}
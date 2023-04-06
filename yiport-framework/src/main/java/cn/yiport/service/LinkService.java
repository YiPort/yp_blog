package cn.yiport.service;

import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.Link;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 友链(Link)表服务接口
 *
 * @author YiPort
 * @since 2023-04-06 12:14:21
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();
}
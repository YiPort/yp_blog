package com.yiport.service.impl;

import com.yiport.constants.SystemConstants;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Link;
import com.yiport.domain.vo.LinkVO;
import com.yiport.mapper.LinkMapper;
import com.yiport.service.LinkService;
import com.yiport.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author YiPort
 * @since 2023-04-06 12:14:21
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    /**
     * 友联查询
     *
     * @return
     */
    @Override
    public ResponseResult getAllLink() {
        //查询所有审核通过的
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> links = list(queryWrapper);
        //转换成vo
        List<LinkVO> linkVOS = BeanCopyUtils.copyBeanList(links, LinkVO.class);
        //封装返回
        return ResponseResult.okResult(linkVOS);
    }
}
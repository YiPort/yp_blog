package com.yiport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Index;
import com.yiport.domain.vo.IndexVO;
import com.yiport.exception.SystemException;
import com.yiport.mapper.IndexMapper;
import com.yiport.service.IndexService;
import com.yiport.utils.BeanCopyUtils;
import com.yiport.utils.LoginUtils;
import com.yiport.utils.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.yiport.constants.BusinessConstants.BLOG_TOKEN;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;


@Service
public class IndexServiceImpl extends ServiceImpl<IndexMapper, Index> implements IndexService {
    @Resource
    private IndexMapper indexMapper;

    @Autowired
    private RedisCache redisCache;

    @Resource
    private  HttpServletRequest httpServletRequest;


    /**
     * 提交文章目录索引
     *
     * @param directoryIndex
     * @return
     */
    @Override
    public ResponseResult postArticleIndex(List<Index> directoryIndex) {
        LoginUtils.checkRole(httpServletRequest);
        if (directoryIndex.isEmpty()) {
            throw new SystemException(PARAMETER_ERROR, "索引为空");
        }
        // 删除该文章已有索引
        LambdaQueryWrapper<Index> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Index::getArticleId, directoryIndex.get(0).getArticleId());
        remove(queryWrapper);
        // 批量保存
        saveBatch(directoryIndex);
        return ResponseResult.okResult();
    }

    /**
     * 获取文章目录索引
     *
     * @return
     */
    @Override
    public ResponseResult getArticleIndex() {
        List<Index> indices = indexMapper.selectList(new LambdaQueryWrapper<Index>()
                .orderByDesc(Index::getArticleId));
        List<IndexVO> indexVOS = BeanCopyUtils.copyBeanList(indices, IndexVO.class);
        return ResponseResult.okResult(indexVOS);
    }


    /**
     * 删除文章目录索引
     */
    @Override
    public ResponseResult deleteArticleIndex(Long indexId) {
        LoginUtils.checkRole(httpServletRequest);
        // 删除该文章已有索引
        LambdaQueryWrapper<Index> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Index::getArticleId, indexId);
        remove(queryWrapper);
        return ResponseResult.okResult();
    }
}





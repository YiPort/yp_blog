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

    /**
     * 提交文章目录索引
     *
     * @param directoryIndex
     * @param userId
     * @return
     */
    @Override
    public ResponseResult postArticleIndex(List<Index> directoryIndex, Long userId) {
        // 校验登录状态
        checkLogin(userId);
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
        List<Index> indices = indexMapper.selectList(new QueryWrapper<>());
        List<IndexVO> indexVOS = BeanCopyUtils.copyBeanList(indices, IndexVO.class);
        return ResponseResult.okResult(indexVOS);
    }

    /**
     * 校验登录状态
     *
     * @param userId
     */
    private void checkLogin(Long userId) {
        // id校验
        if (userId <= 0) {
            throw new SystemException(PARAMETER_ERROR);
        }
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
        }
        // token校验
        String token = request.getHeader("Token");
        if (StringUtils.isAnyBlank(token)) {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后重试");
        }
        String tokenKey = BLOG_TOKEN + userId;
        Object cacheObject = redisCache.getCacheObject(tokenKey);
        if (cacheObject == null) {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后重试");
        }
        if (!token.equals(String.valueOf(cacheObject))) {
            throw new SystemException(NEED_LOGIN, "登录过期，请重新登录");
        }
    }
}





package com.yiport.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiport.domain.entity.Collection;
import com.yiport.domain.vo.ArticleListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CollectionMapper extends BaseMapper<Collection> {

    /**
     * 获取收藏文章列表
     *
     * @param userId
     * @return
     */
    List<ArticleListVO> getCollectList(@Param("userId") Long userId);

}





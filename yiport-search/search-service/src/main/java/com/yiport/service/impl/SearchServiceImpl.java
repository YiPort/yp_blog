package com.yiport.service.impl;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.ArticleDoc;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.SearchQuery;
import com.yiport.esmapper.ArticleRepository;
import com.yiport.service.SearchService;
import com.yiport.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    ArticleRepository articleRepository;

    @Resource
    private RedisCache redisCache;

    /**
     * 文章访问量
     */
    public static final String ARTICLE_VIEWCOUNT = "ypblog:article:viewCount";

    @Override
    public ResponseResult searchArticle(SearchQuery searchQuery) {



        log.debug("searchQuery:{}", searchQuery);
        // 1. 获取数据
        SearchPage<ArticleDoc> searchPage = articleRepository.findByDescriptiveContent(
                // 1.1 设置key
                searchQuery.getSearchKey(),
                // 1.2 设置分页，这里是从第0页开始的
                PageRequest.of(searchQuery.getPageNum() - 1, searchQuery.getPageSize()));
        log.debug("result number:{}", searchPage.getTotalElements());
        // 2. 数据解析
        List<SearchHit<ArticleDoc>> searchHitList = searchPage.getContent();
        ArrayList<ArticleDoc> articleDocList = new ArrayList<>(searchHitList.size());
        for (SearchHit<ArticleDoc> ArticleHit : searchHitList) {
            // 2.1 获取博客数据
            ArticleDoc articleDoc = ArticleHit.getContent();
            // 2.2 获取高亮数据
            Map<String, List<String>> fields = ArticleHit.getHighlightFields();
            if (fields.size() > 0) {
                // 2.3 通过反射，将高亮数据替换到原来的博客数据中
                BeanMap beanMap = BeanMap.create(articleDoc);
                for (String name : fields.keySet()) {
                    beanMap.put(name, fields.get(name).get(0));
                }
            }

            // 2.4 从 redis中获取浏览量
            String redisKey = ARTICLE_VIEWCOUNT;
            Integer viewCount = redisCache.getCacheMapValue(redisKey, articleDoc.getId().toString());
            if (viewCount != null) {
                articleDoc.setViewCount(Long.valueOf(viewCount));
            };

            // 2.5 博客数据插入列表
            articleDocList.add(articleDoc);
        }
        // 3. 设置返回值信息
        PageVO pageVO = new PageVO();
        pageVO.setRows(articleDocList);
        pageVO.setTotal(searchPage.getTotalElements());

        return ResponseResult.okResult(pageVO);

    }
}

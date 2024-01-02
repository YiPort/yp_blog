package com.yiport.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.domain.entity.Article;
import com.yiport.mapper.ArticleMapper;
import com.yiport.service.ArticleService;
import com.yiport.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.yiport.constants.BlogBusinessConstants.ARTICLE_VIEWCOUNT;
import static com.yiport.constants.BlogConstants.RELEASE;

@Component
@Slf4j
public class UpdateViewCountJob {


    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleService articleService;


    /**
     * 持久化浏览量定时任务，每隔十分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void updateViewCount() {
        log.info("=======开始持久化=======");
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, RELEASE);
        List<Article> articles = articleService.list(queryWrapper);

        articles.forEach(article ->
        {
            String id = article.getId().toString();
            String redisKey = ARTICLE_VIEWCOUNT +id;
            String viewCount = redisCache.getCacheObject(redisKey).toString();
            log.info(redisKey + "\t" + viewCount);
            article.setViewCount(Long.valueOf(viewCount));
            articleMapper.updateById(article);
        });

        articleService.updateBatchById(articles);

        log.info("=======持久化完成=======");

    }

}
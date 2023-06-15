package com.yiport.job;

import com.yiport.domain.entity.Article;
import com.yiport.service.ArticleService;
import com.yiport.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yiport.constants.BlogBusinessConstants.ARTICLE_VIEWCOUNT;

@Component
public class UpdateViewCountJob {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleService articleService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void updateViewCount() {
        Map<String, Integer> viewCountMap = redisCache.getCacheMap(ARTICLE_VIEWCOUNT);

        List<Article> articles = viewCountMap.entrySet()
                .stream().map(entry -> (new
                        Article(Long.valueOf(entry.getKey()),entry.getValue().longValue())))
                .collect(Collectors.toList());

        articleService.updateBatchById(articles);


    }

}
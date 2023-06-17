package cn.yiport.job;

import cn.yiport.domain.entity.Article;
import cn.yiport.service.ArticleService;
import cn.yiport.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpdateViewCountJob {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleService articleService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void updateViewCount() {
        Map<String, Integer> viewCountMap = redisCache.getCacheMap("article:viewCount");

        List<Article> articles = viewCountMap.entrySet()
                .stream().map(entry -> (new
                        Article(Long.valueOf(entry.getKey()),entry.getValue().longValue())))
                .collect(Collectors.toList());

        articleService.updateBatchById(articles);


    }

}
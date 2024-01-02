package com.yiport.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.domain.entity.Article;
import com.yiport.mapper.ArticleMapper;
import com.yiport.mapper.SensitiveWordsMapper;
import com.yiport.utils.RedisCache;
import com.yiport.utils.SensitiveWordsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.yiport.constants.BlogBusinessConstants.ARTICLE_VIEWCOUNT;
import static com.yiport.constants.BlogConstants.RELEASE;

@Component
public class ViewCountRunner implements CommandLineRunner {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisCache redisCache;

    @Resource
    private SensitiveWordsMapper sensitiveWordsMapper;

    /**
     * 启动时，将数据库的浏览量存入redis
     *
     * @param args incoming main method arguments
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        //查询博客信息  id  viewCount
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, RELEASE);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        // 将浏览量存入 redis
        String redisKey = ARTICLE_VIEWCOUNT;
        articles.forEach(article ->
                redisCache.setCacheObject(redisKey + article.getId().toString(),
                        BigInteger.valueOf(article.getViewCount()))
        );
        // 将敏感词存入 redis
        Set<String> sensitiveWords = sensitiveWordsMapper.getAllWords();
        Map<Object, Object> sensitiveWordsMap = SensitiveWordsUtils.initSensitiveWordsMap(sensitiveWords);
        redisCache.setCacheObject("sensitiveWordsMap", sensitiveWordsMap);

    }
}
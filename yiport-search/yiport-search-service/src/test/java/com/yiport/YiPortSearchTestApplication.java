package com.yiport;


import com.yiport.domain.entity.ArticleDoc;
import com.yiport.esmapper.ArticleRepository;
import com.yiport.utils.BeanCopyUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {YiPortSearchApplication.class})
public class YiPortSearchTestApplication {
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private ArticleRepository articleRepository;

    /**
     * 创建索引
     *
     */
    @Test
    public void createIndex(){
        boolean exists = elasticsearchRestTemplate.indexOps(ArticleDoc.class).exists();
        System.out.println(exists);
        if(exists)elasticsearchRestTemplate.indexOps(ArticleDoc.class).delete();
        elasticsearchRestTemplate.indexOps(ArticleDoc.class).create();
        elasticsearchRestTemplate.indexOps(ArticleDoc.class).putMapping();
    }

}

package cn.yiport.service;

import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();
}

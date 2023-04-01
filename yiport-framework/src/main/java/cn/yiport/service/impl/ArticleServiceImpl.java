package cn.yiport.service.impl;

import cn.yiport.domain.entity.Article;
import cn.yiport.mapper.ArticleMapper;
import cn.yiport.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper,Article> implements ArticleService {

}

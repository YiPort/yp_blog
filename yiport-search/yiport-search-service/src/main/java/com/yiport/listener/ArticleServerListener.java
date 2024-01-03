package com.yiport.listener;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yiport.domain.entity.ArticleDoc;
import com.yiport.esmapper.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static com.yiport.constants.MQConstants.BLOG_DELETE_KEY;
import static com.yiport.constants.MQConstants.BLOG_DELETE_QUEUE;
import static com.yiport.constants.MQConstants.BLOG_INSERT_KEY;
import static com.yiport.constants.MQConstants.BLOG_REFRESH_ES_KEY;
import static com.yiport.constants.MQConstants.BLOG_REFRESH_QUEUE;
import static com.yiport.constants.MQConstants.BLOG_SAVE_QUEUE;
import static com.yiport.constants.MQConstants.BLOG_TOPIC_EXCHANGE;
import static com.yiport.constants.MQConstants.BLOG_UPDATE_KEY;

/**
 * 博客服务消息队列监听器
 *
 */
@Slf4j
@Component
public class ArticleServerListener {

	@Resource
	private ArticleRepository articleRepository;

	/**
	 * 保存搜索文档
	 *
	 * @param articleDoc
	 */
	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(name = BLOG_TOPIC_EXCHANGE, type = ExchangeTypes.TOPIC),
			value = @Queue(name = BLOG_SAVE_QUEUE),
			key = {BLOG_INSERT_KEY, BLOG_UPDATE_KEY}
	))
	public void saveListener(String  articleDoc) {
		ArticleDoc articleDoc1 = JSON.parseObject(articleDoc, ArticleDoc.class);
		log.debug("save ArticleDoc，{}", articleDoc1);
		articleRepository.save(articleDoc1);
	}

	/**
	 * 保存搜索文档队列,刷新ES数据
	 *
	 * @param articleDocs
	 */
	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(name = BLOG_TOPIC_EXCHANGE, type = ExchangeTypes.TOPIC),
			value = @Queue(name = BLOG_REFRESH_QUEUE),
			key = BLOG_REFRESH_ES_KEY
	))
	public void refreshArticle(String articleDocs) {
		log.debug("导入数量：{}", articleDocs.length());
		List<ArticleDoc> articleDocs1 = JSON.parseObject(articleDocs, new TypeReference<List<ArticleDoc>>() {
		});
		articleRepository.saveAll(articleDocs1);
	}

	/**
	 * 根据文章id删除搜索文档
	 *
	 * @param articleId
	 */
	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(name = BLOG_TOPIC_EXCHANGE, type = ExchangeTypes.TOPIC),
			value = @Queue(name = BLOG_DELETE_QUEUE),
			key = BLOG_DELETE_KEY
	))
	public void deleteListener(Long articleId) {
		// Use findById to obtain an Optional<ArticleDoc>
		Optional<ArticleDoc> optionalArticle = articleRepository.findById(articleId);

		// Check if the Optional contains a value before proceeding
		if (optionalArticle.isPresent()) {
			log.debug("Deleting article, id -> {}", articleId);
			articleRepository.deleteById(articleId);
		} else {
			// Optionally log if the article was not found
			log.warn("Article not found, id -> {}", articleId);
		}
	}

}

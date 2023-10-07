package com.yiport.esmapper;
import com.yiport.domain.entity.ArticleDoc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ArticleRepository操作类
 * 提供save、findById、findAll、count、delete、exists等接口
 *
 */
public interface ArticleRepository extends ElasticsearchRepository<ArticleDoc, Long> {

	/**
	 * 通过描述内容来搜索博客
	 * <br><br>
	 * 这里通过 @Highlight 完成了对高亮的需求，
	 * 其中 requireFieldMatch 参数是取消了只有字段匹配才有高亮的规则
	 * <br>
	 * 并通过 Pageable 和 SearchPage 完成了对分页的需求
	 * <br>
	 * 得到结果后仅需将分页的内容替换掉实体类的内容即可，已经是封装好的了
	 *
	 * @param descriptiveContent 描述语句
	 * @param pageable           分页
	 * @return 博客列表
	 */
	@Highlight(fields = {
			@HighlightField(name = "title", parameters = @HighlightParameters(preTags = "<span style=\"color: red\">", postTags = "</span>", requireFieldMatch = false)),
			@HighlightField(name = "summary", parameters = @HighlightParameters(preTags = "<span style=\"color: red\">", postTags = "</span>", requireFieldMatch = false)),
	})
	SearchPage<ArticleDoc> findByDescriptiveContent(String descriptiveContent, Pageable pageable);

}

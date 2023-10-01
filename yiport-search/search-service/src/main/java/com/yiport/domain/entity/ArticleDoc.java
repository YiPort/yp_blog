package com.yiport.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 文章表(Article)表实体类
 *
 * @author YiPort
 * @since 2023-04-01 19:19:34
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "yp_blog")
public class ArticleDoc {

    /**
     * 博客id
     */
    @Id
    private Long id;

    /**
     * 标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", copyTo = "descriptiveContent")
    private String title;

    /**
     * 文章摘要
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", copyTo = "descriptiveContent")
    private String summary;

    /**
     * 所属分类id
     */
    @Field(type = FieldType.Long, index = false)

    private Long categoryId;

    /**
     * 所属分类名
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /**
     * 缩略图
     */
    @Field(type = FieldType.Keyword, index = false)
    private String thumbnail;

    /**
     * 是否置顶（0否，1是）
     */
    @Field(type = FieldType.Keyword, index = false)
    private String isTop;

    /**
     * 状态（0已发布，1草稿）
     */
    @Field(type = FieldType.Keyword,index = false)
    private String status;

    /**
     * 访问量
     */
    @Field(type = FieldType.Long)
    private Long viewCount;

    /**
     * 是否允许评论 1是，0否
     */
    @Field(type = FieldType.Keyword,index = false)
    private String isComment;

    /**
     * 创建人的用户id
     */
    @Field(type = FieldType.Long)
    private Long createBy;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date,pattern = "uuuu-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 更新人的用户id
     */
    @Field(type = FieldType.Long)
    private Long updateBy;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date,pattern = "uuuu-MM-dd HH:mm:ss")
    private String updateTime;

    /**
     * 删除标志（0代表未删除，1代表已删除）
     */
    @Field(type = FieldType.Integer,index = false)
    private Integer delFlag;

    /**
     * 由其他属性copy而来，主要用于搜索功能，并非该实体类中的成员
     */
    @JsonIgnore
    @Field(type = FieldType.Text, analyzer = "ik_max_word", ignoreFields = "descriptiveContent", excludeFromSource = true)
    String descriptiveContent;

    public ArticleDoc(Long id, long viewCount) {
        this.id = id;
        this.viewCount = viewCount;
    }

    public ArticleDoc(Long id, String title, String summary, Long categoryId, String categoryName, String thumbnail, String isTop, String status, Long viewCount, String isComment, Long createBy, String createTime, Long updateBy, String updateTime, Integer delFlag) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.thumbnail = thumbnail;
        this.isTop = isTop;
        this.status = status;
        this.viewCount = viewCount;
        this.isComment = isComment;
        this.createBy = createBy;
        this.createTime = createTime;
        this.updateBy = updateBy;
        this.updateTime = updateTime;
        this.delFlag = delFlag;
    }

}


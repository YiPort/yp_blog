package com.yiport.constants;

public class MQConstants {


    /**
     * 用户浏览博客
     */
    public static final String BLOG_OPERATE_READ_KEY = "blog.read";

    /**
     * 博客服务交互机
     */
    public static final String BLOG_TOPIC_EXCHANGE = "blog.topic";

    /**
     * 博客插入
     */
    public static final String BLOG_INSERT_KEY = "blog.insert";

    /**
     * 刷新ES数据，也可以用于批量导入博客数据到ES
     */
    public static final String BLOG_REFRESH_ES_KEY = "blog.refresh.es";

    /**
     * 博客更新
     */
    public static final String BLOG_UPDATE_KEY = "blog.update";

    /**
     * 博客删除
     */
    public static final String BLOG_SAVE_KEY = "blog.save";

    /**
     * 博客删除
     */
    public static final String BLOG_DELETE_KEY = "blog.delete";

    /**
     * 博客删除
     */
    public static final String BLOG_REFRESH_KEY = "blog.refresh";

    /**
     * 保存博客队列
     */
    public static final String BLOG_SAVE_QUEUE = BLOG_SAVE_KEY+".es.queue";

    /**
     * 重置ES博客队列
     */
    public static final String BLOG_REFRESH_QUEUE = BLOG_REFRESH_KEY+".es.queue";

    /**
     * 删除博客队列
     */
    public static final String BLOG_DELETE_QUEUE = BLOG_DELETE_KEY + ".es.queue";

}

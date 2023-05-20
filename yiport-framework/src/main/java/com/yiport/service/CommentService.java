package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CommentService extends IService<Comment> {

    ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize);

    ResponseResult addComment(Comment comment);
}
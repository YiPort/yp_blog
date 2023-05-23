package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.Comment;

public interface CommentService extends IService<Comment> {

    ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize);

    ResponseResult addComment(Comment comment);
}
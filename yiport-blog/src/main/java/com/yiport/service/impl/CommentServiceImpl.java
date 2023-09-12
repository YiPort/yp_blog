package com.yiport.service.impl;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Article;
import com.yiport.domain.entity.Comment;
import com.yiport.domain.vo.CommentVO;
import com.yiport.domain.vo.PageVO;
import com.yiport.handler.exception.SystemException;
import com.yiport.mapper.ArticleMapper;
import com.yiport.mapper.CommentMapper;
import com.yiport.service.CommentService;
import com.yiport.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static com.yiport.constants.SystemConstants.ARTICLE_COMMENT;
import static com.yiport.constants.SystemConstants.IS_COMMENT;
import static com.yiport.constants.SystemConstants.NORMAL_COMMENT;
import static com.yiport.constants.SystemConstants.ROOT_COMMENT;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.NO_OPERATOR_AUTH;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author YiPort
 * @since 2023-04-25 07:42:00
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Resource
    private ArticleMapper articleMapper;



    /**
     * 查询评论列表
     *
     * @param articleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        //查询对应文章的根评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //对articleId进行判断
        queryWrapper.eq(ARTICLE_COMMENT.equals(commentType), Comment::getArticleId, articleId);
        queryWrapper.eq(Comment::getStatus, NORMAL_COMMENT);
        //根评论 rootId为-1
        queryWrapper.eq(Comment::getRootId, ROOT_COMMENT);
        //评论类型
        queryWrapper.eq(Comment::getType,commentType);
        queryWrapper.orderByDesc(Comment::getLabel);
        queryWrapper.orderByAsc(Comment::getCreateTime);

        //分页查询
        Page<Comment> page = new Page(pageNum,pageSize);
        page(page,queryWrapper);

        List<CommentVO> commentVOList = BeanCopyUtils.copyBeanList(page.getRecords(), CommentVO.class);

        //查询所有根评论对应的子评论集合，并且赋值给对应的属性
        for (CommentVO commentVo : commentVOList) {
            //查询对应的子评论
            List<CommentVO> children = getChildren(commentVo.getId());
            //赋值
            commentVo.setChildren(children);
        }

        return ResponseResult.okResult(new PageVO(commentVOList, page.getTotal()));
    }


    /**
     * 根据根评论的id查询所对应的子评论的集合
     *
     * @param id 根评论的id
     * @return
     */
    private List<CommentVO> getChildren(Long id) {

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, id);
        queryWrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper);
        // 根评论 id对应的子评论请求体列表
        List<CommentVO> commentVOS = BeanCopyUtils.copyBeanList(comments, CommentVO.class);
        return commentVOS;
    }

    /**
     * 发表评论
     *
     * @param comment
     * @return
     */
    @Override
    public ResponseResult addComment(Comment comment) {
        String type = comment.getType();
        String content = comment.getContent();
        Long createBy = comment.getCreateBy();

        //参数不为空
        if (StringUtils.isAnyBlank(type, content) || createBy == -1) {
            throw new SystemException(PARAMETER_ERROR);
        }
        //限制长度
        if (content.length() > 300)
        {
            throw new SystemException(PARAMETER_ERROR, "评论内容不能超过300字符");
        }
        // Token解析
        String userId = checkToken();
        if (!userId.equals(createBy.toString()))
        {
            throw new SystemException(NO_OPERATOR_AUTH);
        }
        //评论内容不能为空
        if (type.equals(ARTICLE_COMMENT))
        {
            LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Article::getId, comment.getArticleId())
                    .eq(Article::getIsComment, IS_COMMENT);
            if (Objects.isNull(articleMapper.selectOne(queryWrapper)))
            {
                throw new SystemException(PARAMETER_ERROR, "该文章不允许评论");
            }
        }
        //设置保存时间
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化创建时间
        String createTime = currentTime.format(formatter);
        comment.setCreateTime(createTime);
        boolean result = save(comment);
        return ResponseResult.okResult(result);
    }

    /**
     * 解析Token
     *
     * @return
     */
    public String checkToken ()
    {
        String token = httpServletRequest.getHeader("Token");
        if (org.apache.commons.lang3.StringUtils.isBlank(token)) {
            throw new SystemException(NO_OPERATOR_AUTH);
        }
        Claims claims;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            throw new SystemException(NEED_LOGIN);
        }
        return claims.getId();
    }

}



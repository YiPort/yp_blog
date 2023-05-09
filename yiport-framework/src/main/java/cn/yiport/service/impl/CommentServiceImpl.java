package cn.yiport.service.impl;

import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.Comment;
import cn.yiport.domain.vo.CommentVo;
import cn.yiport.domain.vo.PageVo;
import cn.yiport.mapper.CommentMapper;
import cn.yiport.service.CommentService;
import cn.yiport.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author YiPort
 * @since 2023-04-25 07:42:00
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {


    @Override
    public ResponseResult commentList(Long articleId, Integer pageNum, Integer pageSize) {
        //查询对应文章的根评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //对articleId进行判断
        queryWrapper.eq(Comment::getArticleId,articleId);
        //根评论 rootId为-1
        queryWrapper.eq(Comment::getRootId,-1);

        //分页查询
        Page<Comment> page = new Page(pageNum,pageSize);
        page(page,queryWrapper);

       List<CommentVo> commentVoList = BeanCopyUtils.copyBeanList(page.getRecords(),CommentVo.class);

        return ResponseResult.okResult(new PageVo(commentVoList,page.getTotal()));
    }

}



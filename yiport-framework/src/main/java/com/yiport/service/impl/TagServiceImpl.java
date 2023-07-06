package com.yiport.service.impl;

import com.yiport.domain.entity.Tag;
import com.yiport.mapper.TagMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.service.TagService;
import org.springframework.stereotype.Service;

/**
 * 标签(Tag)表服务实现类
 *
 * @author yiport
 * @since 2023-06-28 21:28:19
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}


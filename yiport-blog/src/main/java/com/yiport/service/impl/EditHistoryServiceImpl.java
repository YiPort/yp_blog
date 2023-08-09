package com.yiport.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.domain.entity.EditHistory;
import com.yiport.mapper.EditHistoryMapper;
import com.yiport.service.EditHistoryService;
import org.springframework.stereotype.Service;

/**
 * 编辑历史(EditHistory)表服务接口
 *
 * @author YiPort
 * @since 2023-04-06 12:14:21
 */
@Service("editHistoryService")
public class EditHistoryServiceImpl extends ServiceImpl<EditHistoryMapper, EditHistory> implements EditHistoryService {

}
package com.yiport.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiport.domain.entity.SensitiveWords;

import java.util.Set;

/**
 * @Entity generator.domain.SensitiveWords
 */
public interface SensitiveWordsMapper extends BaseMapper<SensitiveWords>
{
    Set<String> getAllWords();
}





package com.yiport.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 敏感词
 */
@TableName(value = "sensitive_words")
@Data
public class SensitiveWords implements Serializable
{
    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 敏感词
     */
    private String word;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that)
    {
        if (this == that)
        {
            return true;
        }
        if (that == null)
        {
            return false;
        }
        if (getClass() != that.getClass())
        {
            return false;
        }
        SensitiveWords other = (SensitiveWords) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getWord() == null ? other.getWord() == null : this.getWord().equals(other.getWord()));
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getWord() == null) ? 0 : getWord().hashCode());
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", word=").append(word);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
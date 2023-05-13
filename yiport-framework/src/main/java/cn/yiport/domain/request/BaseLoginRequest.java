package cn.yiport.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * LoginRequest 基类
 */
@Data
public class BaseLoginRequest implements Serializable
{
    private static final long serialVersionUID = -8721779617506927498L;

    /**
     * 登录类型
     */
    @NotBlank(message = "类型不能为空")
    private String type;
}

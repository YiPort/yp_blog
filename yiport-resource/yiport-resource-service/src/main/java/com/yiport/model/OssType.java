package com.yiport.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OssType
{
    /**
     * Minio 对象存储
     */
    MINIO("minio", 1),

    /**
     * 腾讯 COS
     */
    TxCOS("tencent", 2),
    /**
     * 七牛云 COS
     */
    QnCOS("qiniu", 3),
    ;

    /**
     * 名称
     */
    final String name;
    /**
     * 类型
     */
    final int type;

}

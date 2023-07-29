DROP TABLE IF EXISTS `yp_index`;

CREATE TABLE `yp_index`
(
    `id`             BIGINT(200)                                                        NOT NULL AUTO_INCREMENT,
    `article_id`     BIGINT(20)   DEFAULT NULL COMMENT '文章id',
    `index_type`     CHAR(1)      DEFAULT '1' COMMENT '索引类型',
    `index_value`    VARCHAR(125) DEFAULT NULL COMMENT '索引内容',
    `index_position` VARCHAR(125) DEFAULT NULL COMMENT '索引位置',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP                             NULL COMMENT '提交时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '最后编辑时间',
    `del_flag`       INT(11)      DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`)
) ENGINE = INNODB
  AUTO_INCREMENT = 15
  DEFAULT CHARSET = utf8mb4 COMMENT ='索引表';

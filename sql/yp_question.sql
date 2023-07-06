DROP TABLE IF EXISTS `yp_question`;

CREATE TABLE `yp_question`
(
    `id`                   BIGINT ( 200 ) NOT NULL AUTO_INCREMENT,
    `article_id`           BIGINT ( 20 ) DEFAULT NULL COMMENT '文章id',
    `question_description` VARCHAR(512) DEFAULT NULL COMMENT '问题描述',
    `create_by`            BIGINT ( 20 ) DEFAULT NULL COMMENT '提交问题的用户id',
    `create_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '提交时间',
    `update_by`            BIGINT ( 20 ) DEFAULT NULL COMMENT '更新人',
    `update_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '最后编辑时间',
    `del_flag`             INT ( 1 ) DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`)
) ENGINE = INNODB AUTO_INCREMENT = 15 DEFAULT CHARSET = utf8mb4 COMMENT = '反馈表';

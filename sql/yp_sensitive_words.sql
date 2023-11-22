DROP TABLE IF EXISTS `sensitive_words`;

CREATE TABLE `yp_sensitive_words` (
                                      id BIGINT(200) NOT NULL COMMENT '主键ID',
                                      word VARCHAR(30) NULL COMMENT '敏感词',
                                      PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';
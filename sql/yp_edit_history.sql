DROP TABLE IF EXISTS `yp_edit_history`;

CREATE TABLE `yp_edit_history`
(
    `id`          int    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint NOT NULL COMMENT '用户id',
    `content`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '事件内容',
    `timestamp`   datetime NULL DEFAULT NULL COMMENT '时间戳（发生时间）',
    `color`       varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标颜色',
    `icon`       varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
    `size`       varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标尺寸',
    `create_by`   bigint(20) DEFAULT NULL COMMENT '创建人的用户id',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by`   bigint(20) DEFAULT NULL COMMENT '更新人的用户id',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `del_flag`    int NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic  COMMENT='编辑记录表';

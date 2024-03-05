CREATE DATABASE /*!32312 IF NOT EXISTS*/`yp_blog` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `yp_blog`;

-- ----------------------------
-- Table structure for yp_article_record
-- ----------------------------
DROP TABLE IF EXISTS `yp_article_record`;
CREATE TABLE `yp_article_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT  '文章记录id',
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文章内容',
  `summary` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章摘要',
  `category_id` bigint NULL DEFAULT NULL COMMENT '所属分类id',
  `thumbnail` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缩略图',
  `is_comment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否允许评论 1是，0否',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人的用户id\r\n',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '最后编辑时间',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  PRIMARY KEY (`record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

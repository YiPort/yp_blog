/*
SQLyog v10.2 
MySQL - 5.7.36 : Database - yp_blog
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`yp_blog` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `yp_blog`;

/*Table structure for table `yp_comment` */

DROP TABLE IF EXISTS `yp_comment`;

CREATE TABLE `yp_comment`(
                             `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
                             `type`               char(1)      DEFAULT '0' COMMENT '评论类型（0代表文章评论，1代表友链评论）',
                             `article_id`         bigint(20) DEFAULT NULL COMMENT '文章id',
                             `root_id`            bigint(20) DEFAULT '-1' COMMENT '根评论id',
                             `content`            varchar(512) DEFAULT NULL COMMENT '评论内容',
                             `to_comment_user_id` bigint(20) DEFAULT '-1' COMMENT '所回复的目标评论的userid',
                             `to_comment_id`      bigint(20) DEFAULT '-1' COMMENT '回复目标评论id',
                             `create_nick`        VARCHAR(125) NULL comment '创建人昵称',
                             `avatar`             VARCHAR(225) NULL comment '创建人头像',
                             `label`              CHAR DEFAULT '0' NULL comment '标签 (0-普通评论，1-置顶评论)',
                             `status`             CHAR(1)      DEFAULT '0' COMMENT '评论状态（0-未精选，1-精选评论）',
                             `create_by`          bigint(20) DEFAULT NULL COMMENT '创建人的用户id',
                             `create_time`        datetime     DEFAULT NULL COMMENT '创建时间',
                             `update_by`          bigint(20) DEFAULT NULL COMMENT '更新人的用户id',
                             `update_time`        datetime     DEFAULT NULL COMMENT '更新时间',
                             `del_flag`           int(1) DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COMMENT='评论表';


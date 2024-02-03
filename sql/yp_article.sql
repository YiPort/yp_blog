/*
SQLyog v10.2 
MySQL - 5.5.40 : Database - yp_blog
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

/*Table structure for table `yp_article` */

DROP TABLE IF EXISTS `yp_article`;

CREATE TABLE `yp_article` (
                              `id`          bigint(200) NOT NULL AUTO_INCREMENT,
                              `title`       varchar(256) DEFAULT NULL COMMENT '标题',
                              `content`     longtext COMMENT '文章内容',
                              `summary`     varchar(1024) DEFAULT NULL COMMENT '文章摘要',
                              `category_id` bigint(20) DEFAULT NULL COMMENT '所属分类id',
                              `thumbnail`   varchar(256) DEFAULT NULL COMMENT '缩略图',
                              `is_top`      char(1) DEFAULT '0' COMMENT '是否置顶（0否，1是）',
                              `status`      char(1) DEFAULT '1' COMMENT '状态（0已发布，1草稿）',
                              `article_examine`      char(1) DEFAULT '0' COMMENT '文章审核状态（0待审核，1审核通过，2驳回）',
                              `view_count`  bigint(200) DEFAULT '0' COMMENT '访问量',
                              `is_comment`  char(1) DEFAULT '1' COMMENT '是否允许评论 1是，0否',
                              `create_by`   bigint(20) DEFAULT NULL COMMENT '创建人的用户id',
                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                              `update_by`   bigint(20) DEFAULT NULL COMMENT '更新人的用户id',
                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                              `del_flag`    int(1) DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='文章表';


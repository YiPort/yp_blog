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

/*Table structure for table `yp_link` */

DROP TABLE IF EXISTS `yp_link`;

CREATE TABLE `yp_link`(
                          `id`          bigint(20) NOT NULL AUTO_INCREMENT,
                          `name`        varchar(256) DEFAULT NULL,
                          `logo`        varchar(256) DEFAULT NULL,
                          `description` varchar(512) DEFAULT NULL,
                          `address`     varchar(128) DEFAULT NULL COMMENT '网站地址',
                          `status`      char(1)      DEFAULT '2' COMMENT '审核状态 (0代表审核通过，1代表审核未通过，2代表未审核)',
                          `create_by`   bigint(20) DEFAULT NULL COMMENT '创建人的用户id',
                          `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
                          `update_by`   bigint(20) DEFAULT NULL COMMENT '更新人的用户id',
                          `update_time` datetime     DEFAULT NULL COMMENT '更新时间',
                          `del_flag`    int(1) DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='友链';


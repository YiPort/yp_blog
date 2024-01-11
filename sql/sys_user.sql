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

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user`(
                           `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                           `uid`         bigint NOT NULL comment '用户UID',
                           `user_name`   varchar(64) NOT NULL DEFAULT 'NULL' COMMENT '用户名',
                           `nick_name`   varchar(64) NOT NULL DEFAULT 'NULL' COMMENT '昵称',
                           `password`    varchar(64) NOT NULL DEFAULT 'NULL' COMMENT '密码',
                           `user_role`   char(1)              DEFAULT '0' COMMENT '用户类型：0代表普通用户，1代表管理员',
                           `status`      char(1)              DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
                           `email`       varchar(64)          DEFAULT NULL COMMENT '邮箱',
                           `phonenumber` varchar(32)          DEFAULT NULL COMMENT '手机号',
                           `sex`         char(1)              DEFAULT NULL COMMENT '用户性别（0男，1女，2未知）',
                           `avatar`      varchar(128)         DEFAULT NULL COMMENT '头像',
                           `create_by`   bigint(20) DEFAULT NULL COMMENT '创建人的用户id',
                           `create_time` datetime             DEFAULT NULL COMMENT '创建时间',
                           `update_by`   bigint(20) DEFAULT NULL COMMENT '更新人的用户id',
                           `update_time` datetime             DEFAULT NULL COMMENT '更新时间',
                           `del_flag`    int(1) DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


DROP TABLE uid_common_generator;
-- 可用UID表
create table uid_common_generator
(
    random_value bigint auto_increment
        primary key,
    uid          int not null
);


DROP TABLE uid_common_not_available;
-- 不可用UID表
create table uid_common_not_available
(
    random_value bigint not null
        primary key,
    uid          int    not null
);


DROP TABLE uid_generate_range;
-- UID类型及范围表
create table uid_generate_range
(
    id           int auto_increment
        primary key,
    min_uid      int                                not null,
    max_uid      int                                not null,
    create_date  datetime default CURRENT_TIMESTAMP not null,
    using_status smallint default 0                 not null,
    user_for     varchar(50)                        not null
);


-- UID分配事务
create
definer = root@localhost procedure GET_UID_FOR_REGISTER(IN p_used_for varchar(50))
BEGIN
	DECLARE userUid INT DEFAULT 0;
    DECLARE randomValue BIGINT DEFAULT 0;
CASE p_used_for
		WHEN 'common' THEN
		START TRANSACTION;
SELECT uid, random_value INTO userUid, randomValue FROM uid_common_generator LIMIT 1 FOR UPDATE;
IF userUid > 0 THEN
DELETE FROM uid_common_generator WHERE random_value = randomValue;
INSERT INTO `uid_common_not_available` VALUES (randomValue, userUid);
END IF;
COMMIT;
END CASE;
SELECT userUid FROM DUAL;
END;

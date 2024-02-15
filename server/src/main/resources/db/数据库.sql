drop table if exists `application`;
create table `application`
(
    id bigint unsigned primary key auto_increment comment 'id',
    name varchar(255) not null comment '应用名称',
    profile varchar(255) not null comment '应用环境',
    path_prefix varchar(255) not null comment '路径前缀',
    version bigint unsigned default 0 comment '乐观锁',
    deleted bigint unsigned default 0 comment '逻辑删除（0-未删除，id号-已删除）',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '上次修改时间'
) engine=innodb, character set utf8mb4, comment '应用';
create unique index idx_name_profile on `application` (deleted,name,profile);

drop table if exists `object`;
CREATE TABLE `object` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `url` varchar(255) NOT NULL COMMENT '图像URL',
  `used` tinyint(1) NOT NULL COMMENT '是否已使用',
  `check_status` tinyint unsigned NOT NULL COMMENT '审核状态（0-未审核,1-审核中,2-审核未通过,3-审核通过,4-无需审核）',
  `used_time` datetime DEFAULT NULL COMMENT '使用时间',
  `application_id` bigint unsigned NOT NULL COMMENT '应用id',
  `version` bigint unsigned DEFAULT '0' COMMENT '乐观锁',
  `deleted` bigint unsigned DEFAULT '0' COMMENT '逻辑删除（逻辑删除(0-未删除，id号-已删除)）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（创建时间）',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '上次修改时间（修改时间）',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '存储类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_url` (`deleted`,`url`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图像';

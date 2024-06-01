drop table if exists `application`;
create table `application`
(
    id           bigint unsigned primary key auto_increment comment 'id',
    project_name varchar(255) not null comment '项目名',
    service_name varchar(255) not null comment '服务名',
    profile      varchar(255) not null comment '环境',
    path_prefix  varchar(255) not null comment '路径前缀（保存到OSS时的路径前缀。当OSS为七牛云时，不能以斜杠开头，但必须以斜杠结尾）',
    version      bigint unsigned default 0 comment '乐观锁',
    deleted      bigint unsigned default 0 comment '逻辑删除（0-未删除，id号-已删除）',
    create_time  datetime        default current_timestamp comment '创建时间',
    update_time  datetime        default current_timestamp on update current_timestamp comment '上次修改时间'
) engine = innodb,
  character set utf8mb4, comment '应用';
create unique index idx_projectname_servicename_profile on `application` (deleted, project_name, service_name, profile);

drop table if exists `object`;
create table `object`
(
    id                   bigint unsigned primary key auto_increment comment 'id',
    type                 tinyint unsigned not null comment '对象类型（0-图像）',
    url                  varchar(255)     not null comment '对象URL',
    usage_status         tinyint unsigned not null comment '使用状态（0-已生成,1-已上传,2-已使用,3-已标记删除）',
    generated_time       datetime         not null comment '生成时间',
    uploaded_time        datetime        default null comment '使用时间',
    used_time            datetime        default null comment '上传时间',
    marked_deletion_time datetime        default null comment '标记删除时间',
    deletion_reason      varchar(255)    default null comment '删除原因',
    application_id       bigint unsigned  not null comment '应用id',
    version              bigint unsigned default 0 comment '乐观锁',
    deleted              bigint unsigned default 0 comment '逻辑删除（逻辑删除(0-未删除，id号-已删除)）',
    create_time          datetime        default current_timestamp comment '创建时间（创建时间）',
    update_time          datetime        default current_timestamp on update current_timestamp comment '上次修改时间（修改时间）'
) engine = innodb,
  character set utf8mb4, comment '对象';
create unique index idx_url on `object` (deleted, url);


drop table if exists `undo_log`;
CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT          NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME     NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME     NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
ALTER TABLE `undo_log`
    ADD INDEX `ix_log_created` (`log_created`);

drop table if exists `application`;
create table `application`
(
    id bigint unsigned primary key auto_increment comment 'id',
    name varchar(255) not null comment '应用名称',
    profile varchar(255) not null comment '应用环境',
    path_prefix varchar(255) not null comment '路径前缀（保存到OSS时的路径前缀）',
    version bigint unsigned default 0 comment '乐观锁',
    deleted bigint unsigned default 0 comment '逻辑删除（0-未删除，id号-已删除）',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '上次修改时间'
) engine=innodb, character set utf8mb4, comment '应用';
create unique index idx_name_profile on `application` (deleted,name,profile);

drop table if exists `object`;
create table `object`
(
    id bigint unsigned primary key auto_increment comment 'id',
    type tinyint unsigned not null comment '对象类型（0-图像）',
    url varchar(255) not null comment '对象URL',
    usage_status tinyint unsigned not null comment '使用状态（0-已生成,1-已上传,2-已使用,3待删除）',
    check_status tinyint unsigned not null comment '审核状态（0-未审核,1-审核中,2-审核未通过,3-审核通过,4-无需审核）',
    generated_time datetime not null comment '生成时间',
    used_time datetime default null comment '上传时间',
    uploaded_time datetime default null comment '使用时间',
    pre_deleted_time datetime default null comment '删除时间',
    application_id bigint unsigned not null comment '应用id',
    version bigint unsigned default 0 comment '乐观锁',
    deleted bigint unsigned default 0 comment '逻辑删除（逻辑删除(0-未删除，id号-已删除)）',
    create_time datetime default current_timestamp comment '创建时间（创建时间）',
    update_time datetime default current_timestamp on update current_timestamp comment '上次修改时间（修改时间）'
) engine=innodb, character set utf8mb4, comment '图像';
create unique index idx_url on `object` (deleted,url);


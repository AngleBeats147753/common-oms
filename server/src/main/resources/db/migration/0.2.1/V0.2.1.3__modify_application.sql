alter table `application`
    add column `share_level` tinyint unsigned not null comment '共享等级' after `path_prefix`;

alter table `application`
    drop column `service_name`;
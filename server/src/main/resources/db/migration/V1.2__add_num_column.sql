alter table `object`
    add column `usage_num` int unsigned not null comment '使用数' after `usage_status`;

# 将已使用的对象的 usage_num 设置为 1
update `object`
set `usage_num` = 1
where `usage_status` = 2;
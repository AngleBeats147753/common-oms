drop table if exists sys_user;
CREATE TABLE sys_user (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    images varchar(500),
    age int,
    email varchar(50),
    deleted bigint DEFAULT 0,
    PRIMARY KEY (id)
);

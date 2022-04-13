create database sharding1;
CREATE TABLE `tb_book`
(
    `id`          int(11)      NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) NULL,
    `sum`         int(11)      NULL DEFAULT 0,
    `price`       int(11)      NULL DEFAULT 0,
    `create_time` timestamp(0) NULL ON UPDATE CURRENT_TIMESTAMP(0),
    `update_time` timestamp(0) NULL,
    PRIMARY KEY (`id`)
);


-- sharding1
INSERT INTO `sharding1`.`tb_book`( `name`, `sum`, `price`, `create_time`, `update_time`) VALUES ('java从入门到脱坑(sharding1)', 20, 100, NULL, NULL);
INSERT INTO `sharding1`.`tb_book`( `name`, `sum`, `price`, `create_time`, `update_time`) VALUES ('C++脱坑指南(sharding1)', 30, 200, NULL, NULL);
INSERT INTO `sharding1`.`tb_book`( `name`, `sum`, `price`, `create_time`, `update_time`) VALUES ('kotlin大法(sharding1)', 40, 50, NULL, NULL);


-- sharding2
INSERT INTO `sharding2`.`tb_book`( `name`, `sum`, `price`, `create_time`, `update_time`) VALUES ('rocketMq中间件(sharding2)', 10, 60, NULL, NULL);
INSERT INTO `sharding2`.`tb_book`( `name`, `sum`, `price`, `create_time`, `update_time`) VALUES ('Redis揭秘(sharding2)', 10, 80, NULL, NULL);
INSERT INTO `sharding2`.`tb_book`( `name`, `sum`, `price`, `create_time`, `update_time`) VALUES ('mysql运行原理(sharding2)', 15, 90, NULL, NULL);


-- sharding3
INSERT INTO `sharding3`.`tb_book`( `name`, `sum`, `price`, `create_time`, `update_time`) VALUES ('kafka底层(sharding3)', 10, 70, NULL, NULL);
INSERT INTO `sharding3`.`tb_book`( `name`, `sum`, `price`, `create_time`, `update_time`) VALUES ('rabbitMq起源(sharding3)', 10, 90, NULL, NULL);
INSERT INTO `sharding3`.`tb_book`( `name`, `sum`, `price`, `create_time`, `update_time`) VALUES ('python程序员是如何饿死的(sharding3)', 15, 200, NULL, NULL);
CREATE TABLE `tb_order`
(
    `id`          int(11)   NOT NULL AUTO_INCREMENT,
    `book_id`     int(11)        DEFAULT NULL,
    `quantity`    int(11)        DEFAULT NULL,
    `price`       int(11)        DEFAULT NULL,
    `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `creditcard` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `credit_card_number` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_creditcard_user_master_idx` (`user_id`),
  CONSTRAINT `fk_creditcard_user_master` FOREIGN KEY (`user_id`) REFERENCES `user_master` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

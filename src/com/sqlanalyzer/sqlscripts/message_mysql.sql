CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `message_text` longtext,
  PRIMARY KEY (`id`),
  KEY `fk_message_user_master_idx` (`user_id`),
  CONSTRAINT `fk_message_user_master` FOREIGN KEY (`user_id`) REFERENCES `user_master` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

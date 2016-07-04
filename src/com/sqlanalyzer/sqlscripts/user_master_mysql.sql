CREATE TABLE `user_master` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idxUnique_user_master_username` (`id`),
  UNIQUE KEY `idxUnique_user_master_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

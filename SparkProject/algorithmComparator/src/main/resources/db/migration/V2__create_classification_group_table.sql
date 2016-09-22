CREATE TABLE `classification_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cross_classification` varchar(255) DEFAULT NULL,
  `multilayer_classification` varchar(255) DEFAULT NULL,
  `simple_classification` varchar(255) DEFAULT NULL,
  `train_classification` varchar(255) DEFAULT NULL,
  `result_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_classification_group_result` (`result_id`),
  CONSTRAINT `FK_classification_group_result` FOREIGN KEY (`result_id`) REFERENCES `result` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17407 DEFAULT CHARSET=utf8;

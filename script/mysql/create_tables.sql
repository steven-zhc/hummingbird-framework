CREATE TABLE `events` (
  `aid` varchar(150) NOT NULL DEFAULT '' COMMENT 'aggregate id',
  `tid` varchar(150) DEFAULT NULL COMMENT 'command id',
  `ctime` datetime NOT NULL COMMENT 'create time',
  `version` int(11) NOT NULL COMMENT 'version',
  `mt` varchar(150) DEFAULT NULL COMMENT 'model type',
  `et` varchar(150) DEFAULT NULL COMMENT 'event type',
  `body` blob NOT NULL COMMENT 'event serialization',
  KEY `aid` (`aid`),
  KEY `tid` (`tid`),
  KEY `ctime` (`ctime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `snapshot` (
  `aid` varchar(150) NOT NULL DEFAULT '' COMMENT 'aggregate id',
  `mt` varchar(150) DEFAULT NULL COMMENT 'model type',
  `version` int(11) NOT NULL COMMENT 'version',
  `ctime` datetime NOT NULL COMMENT 'create time',
  `body` blob NOT NULL COMMENT 'event serialization',
  KEY `aid` (`aid`),
  KEY `ctime` (`ctime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


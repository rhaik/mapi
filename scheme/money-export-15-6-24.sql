# ************************************************************
# Sequel Pro SQL dump
# Version 4004
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.5.29)
# Database: money
# Generation Time: 2015-06-24 09:22:05 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table device
# ------------------------------------------------------------

DROP TABLE IF EXISTS `device`;

CREATE TABLE `device` (
  `id` bigint(20) unsigned NOT NULL,
  `estate` tinyint(4) DEFAULT NULL,
  `userid` bigint(20) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `devicetype` tinyint(4) DEFAULT NULL,
  `tokentype` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'token类型，1：个推，2：爱心推',
  `devicemodel` varchar(100) DEFAULT NULL COMMENT '用户机型',
  `appver` varchar(20) DEFAULT NULL,
  `cityid` int(11) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `updatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `userid` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;

INSERT INTO `device` (`id`, `estate`, `userid`, `token`, `devicetype`, `tokentype`, `devicemodel`, `appver`, `cityid`, `createtime`, `updatetime`)
VALUES
	(406526072285440,1,0,'1231312312',2,0,NULL,NULL,0,'2015-06-24 17:07:22','2015-06-24 17:07:22');

/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table id_increase
# ------------------------------------------------------------

DROP TABLE IF EXISTS `id_increase`;

CREATE TABLE `id_increase` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `mark` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table id_withtimemaker
# ------------------------------------------------------------

DROP TABLE IF EXISTS `id_withtimemaker`;

CREATE TABLE `id_withtimemaker` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id，自动递增',
  `mark` char(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='有时间信息id生成';



# Dump of table money_admin
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_admin`;

CREATE TABLE `money_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `lastlogintime` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_admin` WRITE;
/*!40000 ALTER TABLE `money_admin` DISABLE KEYS */;

INSERT INTO `money_admin` (`id`, `username`, `password`, `lastlogintime`)
VALUES
	(1,'admin','37029f3c646ca4d471884ac903754946',1435044840),
	(2,'test','37029f3c646ca4d471884ac903754946',1434614567),
	(3,'trees','37029f3c646ca4d471884ac903754946',1434614853);

/*!40000 ALTER TABLE `money_admin` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_admin_copy
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_admin_copy`;

CREATE TABLE `money_admin_copy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `lastlogintime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_admin_copy` WRITE;
/*!40000 ALTER TABLE `money_admin_copy` DISABLE KEYS */;

INSERT INTO `money_admin_copy` (`id`, `username`, `password`, `lastlogintime`)
VALUES
	(1,'admin','37029f3c646ca4d471884ac903754946','0000-00-00 00:00:00'),
	(2,'test','37029f3c646ca4d471884ac903754946','0000-00-00 00:00:00'),
	(3,'trees','37029f3c646ca4d471884ac903754946','0000-00-00 00:00:00');

/*!40000 ALTER TABLE `money_admin_copy` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_app
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_app`;

CREATE TABLE `money_app` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT 'APP名称',
  `icon` varchar(100) NOT NULL COMMENT 'APP图标',
  `url` varchar(100) NOT NULL COMMENT 'APP下载地址',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态(0:未审核1:已审核2:审核未通过',
  `creater` int(11) DEFAULT '0' COMMENT '提交人',
  `createtime` int(11) NOT NULL COMMENT '提交时间',
  `auditor` int(11) DEFAULT '0' COMMENT '审核人',
  `audit_time` int(11) NOT NULL DEFAULT '0' COMMENT '审核时间',
  `description` varchar(500) NOT NULL COMMENT 'APP描述',
  `agreement` varchar(20) DEFAULT NULL COMMENT '应用协议',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='app应用列表';

LOCK TABLES `money_app` WRITE;
/*!40000 ALTER TABLE `money_app` DISABLE KEYS */;

INSERT INTO `money_app` (`id`, `name`, `icon`, `url`, `status`, `creater`, `createtime`, `auditor`, `audit_time`, `description`, `agreement`)
VALUES
	(12,'熙敏楚格','http://money.lieqicun.cn/upload/2015/05/51R58PICfTe.jpg','http://www.zhenpin.com',1,0,1429839782,0,1431582448,'感尽人间沧桑，叹尽世态炎凉。 ',NULL),
	(16,'蜀山剑灵','http://money.lieqicun.cn/upload/2015/06/1_1280x800%20%281%29.jpg','http://money.lieqicun.cn/app/',1,0,1429841445,1,1433473801,'蜀山剑灵','ds'),
	(17,'零钱夺宝','http://money.lieqicun.cn/upload/2015/05/qrcode_for_gh_e020bfdcd32b_430%20%281%29.jpg','http://money.lieqicun.cn/',1,0,1431510169,0,1432116825,'用赚的钱来夺宝，1元抢iphone ',NULL),
	(18,'有赞微小店','http://money.lieqicun.cn/upload/2015/05/1121588508b9b75d45l.jpg','http://money.lieqicun.cn/',1,0,1431572091,0,1431572200,'下载手机注册并开店可获奖',NULL),
	(19,'邮箱大师','http://money.lieqicun.cn/upload/2015/05/49.jpg','http://money.lieqicun.cn/',1,1,1432200881,2,1432202302,'添加新账号就有机会赢ipad哦',NULL),
	(20,'闪银','http://money.lieqicun.cn/upload/2015/05/550cd04c103f2030.jpg%21600x600.jpg','http://money.lieqicun.cn',1,2,1432259411,1,1432272089,'互联网信用评估',NULL),
	(21,'APP测试','http://money.lieqicun.cn/upload/2015/05/3.jpg','http://www.zhenpin.com',1,1,1433043266,1,1434419381,'测试','个'),
	(23,'安居客','http://money.lieqicun.cn/upload/2015/06/019%20%282%29.jpg','http://money.lieqicun.cn/',1,1,1433127518,1,1434419383,'安居客，一手掌握','sdjk ');

/*!40000 ALTER TABLE `money_app` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_app_task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_app_task`;

CREATE TABLE `money_app_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` int(11) NOT NULL COMMENT 'appID（app表）',
  `name` varchar(50) NOT NULL COMMENT '任务名称',
  `description` varchar(500) NOT NULL COMMENT '任务描述',
  `keywords` varchar(20) NOT NULL COMMENT '任务搜索关键字',
  `amount` int(11) NOT NULL COMMENT '任务完成金额',
  `friends_amount` int(11) NOT NULL COMMENT '好友分成金额',
  `start_time` int(11) NOT NULL COMMENT '开始时间',
  `end_time` int(11) NOT NULL COMMENT '结束时间',
  `createtime` int(11) NOT NULL COMMENT '任务创建时间',
  `action` set('1','2','3') NOT NULL COMMENT '动作(1:搜索2:下载,3:浏览)',
  `sort` int(11) NOT NULL DEFAULT '0',
  `task_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0--所有任务，1--系统任务',
  PRIMARY KEY (`id`),
  KEY `start_end` (`start_time`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_app_task` WRITE;
/*!40000 ALTER TABLE `money_app_task` DISABLE KEYS */;

INSERT INTO `money_app_task` (`id`, `app_id`, `name`, `description`, `keywords`, `amount`, `friends_amount`, `start_time`, `end_time`, `createtime`, `action`, `sort`, `task_type`)
VALUES
	(3,16,'注册','24小时内注册，退出再登陆（必做任务）','蜀山剑灵',4,1,1430473641,1431164841,1429942201,'1',0,0),
	(13,12,'熙敏楚格','下载熙敏楚格并且试玩3分钟','熙敏楚格',20,8,1430287231,1431037836,1429955027,'2',0,0),
	(14,16,' 顶顶顶顶',' 的','的',10,5,1430360733,1431619233,1430112495,'2',0,0),
	(15,12,'注册游戏','注册游戏，并且试玩三分钟','熙敏楚格',20,5,1432177851,1433041851,1430709384,'1',0,0),
	(16,12,'下载','下载游戏','熙敏楚格',20,5,1431658850,1431843650,1430709802,'2',0,0),
	(21,16,'是','的','的',2,1,1432175428,1432322128,1430720759,'1,2,3',0,0),
	(22,16,'是','是','是',2,1,1431619202,1433001626,1430720839,'2,3',0,0),
	(23,12,'是','$connection=Yii::app()->db; \n$sql=\"SELECT u.account,i.* FROM sys_user as u left join user_info as i on u.id=i.user_id\";\n$rows=$connection->createCommand ($sql)->query();\nforeach ($rows as $k => $v ){\n    echo $v[\'add_time\'];\n}','是',2,1,1432224024,1432310424,1430720860,'2,3',0,0),
	(24,16,'的','的','的',2,1,1430720977,1432828822,1430721078,'1',0,0),
	(25,16,'多大','的','的额的',3,2,1430841635,1432137635,1430722495,'1,2,3',0,0),
	(26,12,'熙敏楚格','零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝1111111111111111111212121下载零钱夺宝下载零零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝1111111111111111111212121下','熙敏楚格',20,5,1431446438,1434124838,1431506286,'2',1,0),
	(27,12,'熙敏楚格浏览','熙敏楚格浏览多大','熙敏楚格',10,2,1431532801,1432915201,1431506364,'3',0,0),
	(28,17,'零钱夺宝下载','零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载零钱夺宝下载','零钱夺宝的',10,2,1431565230,1432429230,1431510282,'2',0,0),
	(29,18,'注册账号','注册账号填写资料完整体验1分钟','有赞微小店，微小店',20,5,1432224048,1433001648,1431572280,'1,2,3',0,0),
	(31,18,'test','test1111','test',12,5,1431918751,1432281005,1431918758,'1',0,0),
	(32,21,'测试任务','测试任务','测试任务',10,3,1433043322,1435818619,1433043304,'1',0,0),
	(33,23,'查看并收藏3处房源','查看并收藏3处房源','安居客',10,2,1433088041,1433088042,1433139851,'1',0,0),
	(34,23,'打开浏览附近3处房屋信息','打开浏览附近3处房屋信息','安居客',10,2,1433088058,1435680058,1433140157,'3',0,0),
	(37,23,'查看分享一处房源','查看分享一处房源','安居客',2,1,1433088050,1435680050,1433140703,'1,2,3',0,0),
	(38,23,'打开浏览附近3处房源信息','打开浏览附近3处房源信息','安居客',10,2,1433088026,1435680026,1433140759,'1,2,3',2,0);

/*!40000 ALTER TABLE `money_app_task` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_authority
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_authority`;

CREATE TABLE `money_authority` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `menu_id` int(11) NOT NULL DEFAULT '0' COMMENT 'money_menu主键id',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT 'money_admin主键id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_authority` WRITE;
/*!40000 ALTER TABLE `money_authority` DISABLE KEYS */;

INSERT INTO `money_authority` (`id`, `menu_id`, `user_id`)
VALUES
	(1,1,1),
	(2,2,1),
	(3,3,1),
	(4,4,1),
	(5,6,1),
	(6,27,1),
	(7,7,1),
	(8,8,1),
	(9,9,1),
	(10,10,1),
	(11,11,1),
	(12,13,1),
	(13,12,1),
	(14,14,1),
	(15,15,1),
	(16,16,1),
	(17,17,1),
	(18,18,1),
	(19,19,1),
	(20,20,1),
	(21,21,1),
	(22,22,1),
	(23,23,1),
	(24,24,1),
	(25,25,1),
	(27,30,1),
	(28,31,1),
	(29,32,1),
	(30,33,1),
	(31,34,1),
	(32,35,1),
	(104,1,3),
	(105,2,3),
	(106,3,3),
	(107,4,3),
	(108,6,3),
	(109,27,3),
	(110,7,3),
	(111,8,3),
	(112,9,3),
	(134,20,3),
	(135,21,3),
	(136,22,3),
	(137,23,3),
	(138,24,3),
	(139,25,3),
	(144,36,1),
	(145,37,1),
	(146,38,1),
	(147,39,1),
	(148,40,1),
	(149,42,1),
	(150,41,1),
	(151,43,1),
	(152,44,1),
	(153,45,1),
	(154,46,1),
	(155,1,2),
	(156,2,2),
	(157,7,2),
	(159,36,2),
	(162,47,1),
	(164,9,2),
	(184,50,1),
	(185,30,2),
	(186,31,2),
	(187,50,2),
	(189,34,2),
	(190,46,2),
	(192,49,1),
	(193,51,1),
	(194,3,2),
	(197,27,2),
	(198,8,2),
	(199,37,2),
	(200,47,2),
	(201,45,2),
	(202,36,3),
	(203,37,3),
	(204,47,3),
	(205,43,3),
	(206,44,3),
	(207,30,3),
	(208,31,3),
	(209,45,3),
	(210,34,3),
	(211,46,3);

/*!40000 ALTER TABLE `money_authority` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_menu
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_menu`;

CREATE TABLE `money_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) NOT NULL DEFAULT '0' COMMENT '父级id',
  `name` varchar(10) NOT NULL COMMENT '名字',
  `url` varchar(100) DEFAULT '0' COMMENT 'url地址 /AppTask/Create',
  `icon` varchar(50) NOT NULL DEFAULT '0' COMMENT '图标样式名称',
  `description` varchar(100) NOT NULL DEFAULT '0' COMMENT '描述',
  `type` tinyint(1) NOT NULL COMMENT '1--菜单，2--动作',
  `identity` varchar(50) NOT NULL DEFAULT '0' COMMENT '标识 Create',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_menu` WRITE;
/*!40000 ALTER TABLE `money_menu` DISABLE KEYS */;

INSERT INTO `money_menu` (`id`, `pid`, `name`, `url`, `icon`, `description`, `type`, `identity`)
VALUES
	(1,0,'应用管理','','fa-cogs','应用管理',1,'0'),
	(2,1,'App管理','/app/','','App管理',1,'app'),
	(3,2,'添加和修改应用','/app/Save','','保存和修改应用',2,'Save'),
	(4,2,'删除App','/app/Delete','','删除App',2,'Delete'),
	(6,2,'审核','/app/Audit','','审核应用',2,'Audit'),
	(7,1,'任务管理','/appTask/','','任务管理',1,'appTask'),
	(8,7,'添加任务','/appTask/SaveTask','','保存任务',2,'SaveTask'),
	(9,7,'删除任务','/appTask/Delete','','删除任务',2,'Delete'),
	(10,0,'用户管理','','fa-table','用户管理',1,'用户管理'),
	(11,10,'所有用户','/user/','','所有用户',1,'user'),
	(12,10,'用户试用日志','/userAppLog/','','用户试用日志',1,'userAppLog'),
	(13,11,'修改标签','/user/SaveUserLabel','','修改标签',2,'SaveUserLabel'),
	(14,10,'用户收支明细','/userIncomeLog/','','用户收支明细',1,'userIncomeLog'),
	(15,0,'消息管理','','fa-table','消息管理',1,''),
	(16,15,'试用消息通知','/userAppNotice/','','试用消息通知',1,'userAppNotice'),
	(17,15,'系统消息通知','/userNotice/','','系统消息通知',1,'userNotice'),
	(18,17,'添加','/userNotice/Save','','添加系统消息通知',2,'Save'),
	(19,17,'查看','/userNotice/View','','查看系统消息',2,'View'),
	(20,0,'财务管理','','fa-bar-chart-o','财务管理',1,''),
	(21,20,'提现列表','/userEnchashMent/','','提现列表',1,'userEnchashMent'),
	(22,21,'审核','/userEnchashMent/Audit','','审核',2,'Audit'),
	(23,21,'查看提现日志','/userEnchashMent/ShowLog','','查看提现日志',2,'ShowLog'),
	(24,20,'付款管理','/payMent/','','付款管理',1,'payMent'),
	(25,24,'付款详细数据','/payMent/payMentInfo','','付款详细数据',2,'payMentInfo'),
	(27,2,'查看任务','/app/View','','查看任务',2,'View'),
	(30,0,'系统管理','','fa-cogs','系统管理',1,''),
	(31,30,'菜单管理','/menu/','','',1,'menu'),
	(32,31,'添加和修改','/menu/Save','','添加和修改菜单',2,'Save'),
	(33,31,'删除','/menu/Delete','','删除菜单',2,'Delete'),
	(34,30,'用户管理','/authority/','','用户管理',1,'authority'),
	(35,34,'设置权限','/authority/Save','','设置权限',2,'Save'),
	(36,2,'App列表','/app/Index','','App列表',2,'Index'),
	(37,7,'任务列表','/appTask/Index','','任务列表',2,'Index'),
	(38,11,'用户列表','/user/Index','','用户列表',2,'Index'),
	(39,12,'日志列表','/userAppLog/Index','','用户试用日志列表',2,'Index'),
	(40,14,'明细列表','/userIncomeLog/Index','','明细列表',2,'Index'),
	(41,17,'系统消息列表','/userNotice/Index','','系统消息列表',2,'Index'),
	(42,16,'试用消息列表','/userAppNotice/Index','','试用消息列表',2,'Index'),
	(43,21,'提现列表','/userEnchashMent/Index','','提现列表',2,'Index'),
	(44,24,'付款列表','/payMent/Index','','付款列表',2,'Index'),
	(45,31,'菜单列表','/menu/Index','','菜单列表',2,'Index'),
	(46,34,'用户列表','/authority/Index','','用户列表',2,'Index'),
	(47,7,'查看','/appTask/View','','查看任务描述',2,'View'),
	(48,11,'查看','/user/UserTask','','查看任务',2,'UserTask'),
	(49,24,'付款','/payMent/Alipay','','付款',2,'Alipay'),
	(50,31,'查看','/menu/View','','查看描述',2,'View');

/*!40000 ALTER TABLE `money_menu` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_payment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_payment`;

CREATE TABLE `money_payment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL COMMENT '付款账号',
  `account_name` varchar(50) NOT NULL COMMENT '付款账户名',
  `pay_date` int(11) NOT NULL DEFAULT '0' COMMENT '付款当天日期',
  `batch_no` varchar(50) NOT NULL COMMENT '批次号',
  `batch_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '付款总金额',
  `batch_num` int(11) NOT NULL DEFAULT '0' COMMENT '付款笔数',
  `operation_time` int(11) NOT NULL COMMENT '操作时间',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(1：未打款2:打款成功,3:打款不成功)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='付款批次表';

LOCK TABLES `money_payment` WRITE;
/*!40000 ALTER TABLE `money_payment` DISABLE KEYS */;

INSERT INTO `money_payment` (`id`, `email`, `account_name`, `pay_date`, `batch_no`, `batch_fee`, `batch_num`, `operation_time`, `status`)
VALUES
	(1,'493653173@qq.com','沈秀迎',0,'0001',1.00,1,1434604435,2);

/*!40000 ALTER TABLE `money_payment` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_payment_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_payment_info`;

CREATE TABLE `money_payment_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_enchashment_id` int(11) NOT NULL COMMENT '提现表ID(user_enchashment)',
  `payment_id` int(11) NOT NULL COMMENT '付款表ID(payment)',
  `serial_number` varchar(50) DEFAULT '0' COMMENT '流水号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='付款详细数据';

LOCK TABLES `money_payment_info` WRITE;
/*!40000 ALTER TABLE `money_payment_info` DISABLE KEYS */;

INSERT INTO `money_payment_info` (`id`, `user_enchashment_id`, `payment_id`, `serial_number`)
VALUES
	(1,1,1,'00010001');

/*!40000 ALTER TABLE `money_payment_info` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_token
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_token`;

CREATE TABLE `money_token` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `action` varchar(50) NOT NULL COMMENT '动作',
  `identity` char(32) NOT NULL COMMENT '标示符',
  `token` char(32) NOT NULL COMMENT 'token',
  `expire_time` int(11) NOT NULL COMMENT '过期时间',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `data` varchar(500) NOT NULL COMMENT '数据参数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `identity_action` (`identity`,`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户TOKEN表';

LOCK TABLES `money_token` WRITE;
/*!40000 ALTER TABLE `money_token` DISABLE KEYS */;

INSERT INTO `money_token` (`id`, `action`, `identity`, `token`, `expire_time`, `user_id`, `data`)
VALUES
	(286,'user.login','9ae40d65d281231c39d4be63cf3b4f78','de91517601e6b23af45e286e31d75ad7',43200,1,'a:2:{s:7:\"user_id\";s:1:\"1\";s:6:\"openid\";s:28:\"oYWl1uItVfhhjUMcHDOOnm0a7bIU\";}'),
	(287,'user.login','f22ee918f3ac55f36c1608f1426459d5','fde5ade27af1e16d91c700c17821565b',3600,17,'a:2:{s:7:\"user_id\";s:2:\"17\";s:6:\"openid\";s:28:\"oYWl1uItVfhhjUMcHDOOnm0a7b3S\";}'),
	(288,'user.login','f82367ade0711f34774e9e0f20d7941a','7a03b2b610ec412c2a17805325dd8be8',1432891091,18,'a:2:{s:7:\"user_id\";s:2:\"18\";s:6:\"openid\";s:28:\"oVvAUt2BCCm4zOq-DlLr4lJ-Yhs4\";}'),
	(289,'user.login','109cc4a99cea3c7705339d2f9502b8f5','ca843a2979b71b7963b9a21f2b99807d',43200,25,'a:2:{s:7:\"user_id\";s:2:\"25\";s:6:\"openid\";s:28:\"oYWl1uItVfhhjUMcHDOOnm0a7b3R\";}');

/*!40000 ALTER TABLE `money_token` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user`;

CREATE TABLE `money_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_identity` int(11) unsigned NOT NULL COMMENT '用户标识',
  `avatar` varchar(200) NOT NULL COMMENT '头像地址',
  `name` varchar(50) NOT NULL COMMENT '用户名称',
  `openid` varchar(50) NOT NULL COMMENT '用户唯一标识',
  `area` varchar(20) DEFAULT NULL COMMENT '地区',
  `amount` int(11) NOT NULL DEFAULT '0' COMMENT '收入总金额',
  `appleid` char(50) NOT NULL DEFAULT '''''' COMMENT '设备号',
  `registration_time` int(11) NOT NULL COMMENT '注册时间',
  `user_label_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户标签ID',
  `account` varchar(50) DEFAULT '' COMMENT '用户账号',
  `account_name` varchar(50) DEFAULT '' COMMENT '用户账号名称',
  `soure` varchar(50) DEFAULT '0' COMMENT '来源',
  PRIMARY KEY (`id`),
  UNIQUE KEY `openid` (`openid`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_user` WRITE;
/*!40000 ALTER TABLE `money_user` DISABLE KEYS */;

INSERT INTO `money_user` (`id`, `user_identity`, `avatar`, `name`, `openid`, `area`, `amount`, `appleid`, `registration_time`, `user_label_id`, `account`, `account_name`, `soure`)
VALUES
	(1,150369456,'http://money.lieqicun.cn/upload/2015/05/51R58PICfTe.jpg','释然','oYWl1uItVfhhjUMcHDOOnm0a7bIU','北京',992,'001235YI0001',1429342454,1,'rhaik@163.com','海克','0'),
	(2,38265927,'http://money.lieqicun.cn/upload/2015/04/201700-120G222464096.jpg','Ginuy','oYWl1uItVfhhjUMcHDOOnm0a7b2U','北京',28,'001235YI0001',1429342454,2,'15011449343@163.com','莫晓天','0'),
	(3,38265926,'http://money.lieqicun.cn/upload/2015/04/201700-120G222464096.jpg','Hourglass','oYWl1uItVfhhjUMcHDOOnm0a7b3U','北京',233,'001235YI0001',1429342454,3,'493653173@qq.com','莫晓天','0'),
	(15,38265925,'http://wx.qlogo.cn/mmopen/CkBYF6IYNs35OjtibUC6drvsic0TBxHT5QLXvHPMOFxMPk1uC0wGOoIsf2v7pCzQq31e0FkIZukMIm8pFcic8GPlg/0','李凯-多塔里','oVvAUt233XC8HjmlpTEJQ7w_qjsc','Guangzhou',0,'B082C5FD-0D48-453B-849C-0658141BDBED',1431760362,3,'','','0'),
	(16,38265924,'http://www.lieqicun.cn/index/images/logo.png','测试中','oYWl1uItVfhhjUMcHDOOnm0a7b3T','beijing',4,'B082C5FD-0D48-453B-849C-0658141BDBED',1432001085,3,'','','0'),
	(17,38265923,'http://www.lieqicun.cn/index/images/logo.png','海克','oYWl1uItVfhhjUMcHDOOnm0a7b3S','beijing',0,'B082C5FD-0D48-453B-849C-0658141BDBED',1432019240,1,'','','0'),
	(18,38265922,'http://wx.qlogo.cn/mmopen/ajNVdqHZLLBLBticuq1beWGpHEIyrM7ZMy6cybICzv6kJZibbqmI8UoicSJnf6sjblSHMkGpBiatGtibxdAs6mmkx1Q/0','晓涛','oVvAUt2BCCm4zOq-DlLr4lJ-Yhs4','Guangzhou',0,'1578B541-E723-46B7-8FD4-1E324A11D951',1432714353,2,'','','0'),
	(19,38265921,'http://wx.qlogo.cn/mmopen/ibDRic4Cogo7htNnsdlBk6J5EMhB5NypFjtYucb9E8q3O5FkbOIbPVxEHm5nEI4l6usYoPzuaciapfYpWS52HskvE8K0bMx1ocR/0','宁晓涛','oVvAUt7ygc-5B06eobWyvoEEa7ws','Guangzhou',0,'57701512-A6FD-4D6B-8887-051DAC698961',1432887272,2,'','','0'),
	(20,38265920,'http://www.lieqicun.cn/index/images/logo.png','海克222','oYWl1uItVfhhjUMcHDOOnm0a7b3D','beijing',0,'B082C5FD-0D48-453B-849C-0658141BDBED',1433731590,0,'','','0'),
	(25,42542167,'http://www.lieqicun.cn/index/images/logo.png','海克444444','oYWl1uItVfhhjUMcHDOOnm0a7b3R','beijing',0,'B082C5FD-0D48-453B-849C-0658141BDBED',1433732547,0,'','','0');

/*!40000 ALTER TABLE `money_user` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user_app_log
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user_app_log`;

CREATE TABLE `money_user_app_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID(users表)',
  `app_id` int(11) NOT NULL COMMENT 'APP应用ID(app表)',
  `app_task_id` int(11) NOT NULL COMMENT '任务ID(app_task)',
  `user_task_id` int(11) NOT NULL COMMENT '用户任务ID(money_user_task)',
  `action` tinyint(4) NOT NULL COMMENT '动作(1:搜索2:下载,3:浏览)',
  `action_time` int(11) NOT NULL COMMENT '动作时间',
  `appleid` char(50) NOT NULL COMMENT '设备号',
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_user_app_log` WRITE;
/*!40000 ALTER TABLE `money_user_app_log` DISABLE KEYS */;

INSERT INTO `money_user_app_log` (`id`, `user_id`, `app_id`, `app_task_id`, `user_task_id`, `action`, `action_time`, `appleid`)
VALUES
	(172,1,1,3,44,1,1431938177,'2222'),
	(173,16,16,3,45,1,1432177851,'B082C5FD-0D48-453B-849C-0658141BDBED'),
	(174,1,12,15,46,1,1432177851,'B082C5FD-0D48-453B-849C-0658141BDBED');

/*!40000 ALTER TABLE `money_user_app_log` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user_app_notice
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user_app_notice`;

CREATE TABLE `money_user_app_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID(users表)',
  `user_task_id` int(11) NOT NULL COMMENT '用户任务(user_task_id)',
  `action` tinyint(4) NOT NULL COMMENT '动作(1:完成试用2:分享,3:邀请好友,4:好友分成)',
  `action_time` int(11) NOT NULL COMMENT '动作时间',
  `remarks` varchar(100) DEFAULT NULL COMMENT '备注',
  `is_read` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已读(1:已读0:未读)',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1:应用试用消息2:好友消息',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `user_task_id` (`user_task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_user_app_notice` WRITE;
/*!40000 ALTER TABLE `money_user_app_notice` DISABLE KEYS */;

INSERT INTO `money_user_app_notice` (`id`, `user_id`, `user_task_id`, `action`, `action_time`, `remarks`, `is_read`, `type`)
VALUES
	(468,1,44,1,1431938177,'完成,试用完后获得奖励.',0,1),
	(469,3,44,4,1431938177,'释然:完成了<注册>的试用,并获得收益4元.给你提供分成1元.',0,2),
	(470,16,45,1,1432177851,'完成,试用完后获得奖励.',0,1),
	(471,1,46,1,1432177851,'完成,试用完后获得奖励.',0,1),
	(472,3,46,4,1432177851,'释然:完成了<注册游戏>的试用,并获得收益20元.给你提供分成5元.',0,2);

/*!40000 ALTER TABLE `money_user_app_notice` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user_enchashment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user_enchashment`;

CREATE TABLE `money_user_enchashment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '提现金额',
  `account` varchar(50) NOT NULL COMMENT '提现账号',
  `account_name` varchar(50) NOT NULL COMMENT '提现账号名称',
  `times` int(11) NOT NULL COMMENT '提现时间',
  `status` tinyint(4) NOT NULL COMMENT '状态（1:待审核，2:已审核,3审核未通过4:打款成功,5:打款不成功）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_user_enchashment` WRITE;
/*!40000 ALTER TABLE `money_user_enchashment` DISABLE KEYS */;

INSERT INTO `money_user_enchashment` (`id`, `user_id`, `amount`, `account`, `account_name`, `times`, `status`)
VALUES
	(1,1,1.00,'15011449343@163.com','沈秀迎',1434595259,4);

/*!40000 ALTER TABLE `money_user_enchashment` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user_enchashment_log
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user_enchashment_log`;

CREATE TABLE `money_user_enchashment_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_enchashment_id` int(11) NOT NULL COMMENT '提现表ID(user_enchashment)',
  `operator` int(11) NOT NULL COMMENT '操作人',
  `type` tinyint(4) NOT NULL COMMENT '1==用户id，2==管理人id',
  `operator_time` int(11) NOT NULL COMMENT '操作时间',
  `remarks` varchar(100) NOT NULL COMMENT '备注',
  `status` tinyint(4) NOT NULL COMMENT '状态(1:待审核，2:已审核,3审核未通过4:打款成功,5:打款不成功)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_user_enchashment_log` WRITE;
/*!40000 ALTER TABLE `money_user_enchashment_log` DISABLE KEYS */;

INSERT INTO `money_user_enchashment_log` (`id`, `user_enchashment_id`, `operator`, `type`, `operator_time`, `remarks`, `status`)
VALUES
	(1,1,1,2,1434604440,'提现',4);

/*!40000 ALTER TABLE `money_user_enchashment_log` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user_idea
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user_idea`;

CREATE TABLE `money_user_idea` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID(money_users)',
  `content` varchar(500) NOT NULL COMMENT '反溃内容',
  `version` char(10) NOT NULL COMMENT '版本',
  `equipment` char(50) NOT NULL COMMENT '设备号',
  `create_time` int(11) NOT NULL COMMENT '时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='意见反溃';

LOCK TABLES `money_user_idea` WRITE;
/*!40000 ALTER TABLE `money_user_idea` DISABLE KEYS */;

INSERT INTO `money_user_idea` (`id`, `user_id`, `content`, `version`, `equipment`, `create_time`)
VALUES
	(1,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432019656),
	(2,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432020292),
	(3,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432020294),
	(4,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432020868),
	(5,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432020910),
	(6,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432021068),
	(7,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432021217),
	(8,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432021411),
	(9,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432021431),
	(10,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432021467),
	(11,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432021780),
	(12,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432535797),
	(13,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432886699),
	(14,16,'测试中...','1.1','B082C5FD-0D48-453B-849C-0658141BDBED',1432887914);

/*!40000 ALTER TABLE `money_user_idea` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user_income_log
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user_income_log`;

CREATE TABLE `money_user_income_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `user_task_id` int(11) NOT NULL COMMENT '用户任务Id(money_user_task)',
  `action` tinyint(4) NOT NULL COMMENT '动作(1:完成试用2:分享,3:邀请好友,4好友分成,5:提现)',
  `amount` int(11) NOT NULL COMMENT '本次操作积分',
  `total_amount` int(11) NOT NULL COMMENT '收入总积分',
  `type` tinyint(4) NOT NULL COMMENT '增加或减少(1:增加0减少)',
  `operator_time` int(11) NOT NULL COMMENT '操作时间',
  `remarks` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `user_operator` (`user_id`,`operator_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户积分明细';

LOCK TABLES `money_user_income_log` WRITE;
/*!40000 ALTER TABLE `money_user_income_log` DISABLE KEYS */;

INSERT INTO `money_user_income_log` (`id`, `user_id`, `user_task_id`, `action`, `amount`, `total_amount`, `type`, `operator_time`, `remarks`)
VALUES
	(474,1,44,1,4,972,1,1431938177,'释然完成了<注册>的试用,并获得收益4元.'),
	(475,3,44,4,1,228,1,1431938177,'释然:完成了<注册>的试用,并获得收益4元.给你提供分成1元.'),
	(476,16,45,1,4,4,1,1432177851,'测试中完成了<注册>的试用,并获得收益4元.'),
	(477,1,46,1,20,992,1,1432177851,'释然完成了<注册游戏>的试用,并获得收益20元.'),
	(478,3,46,4,5,233,1,1432177851,'释然:完成了<注册游戏>的试用,并获得收益20元.给你提供分成5元.');

/*!40000 ALTER TABLE `money_user_income_log` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user_invitation_friends
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user_invitation_friends`;

CREATE TABLE `money_user_invitation_friends` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `friends` int(11) NOT NULL COMMENT '好友ID',
  `times` int(11) NOT NULL COMMENT '时间',
  PRIMARY KEY (`id`),
  KEY `user` (`user_id`),
  KEY `friends` (`friends`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_user_invitation_friends` WRITE;
/*!40000 ALTER TABLE `money_user_invitation_friends` DISABLE KEYS */;

INSERT INTO `money_user_invitation_friends` (`id`, `user_id`, `friends`, `times`)
VALUES
	(1,1,3,1430112495),
	(2,1,2,1430112495),
	(3,3,1,1430112495),
	(4,16,15,1432004541);

/*!40000 ALTER TABLE `money_user_invitation_friends` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user_label
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user_label`;

CREATE TABLE `money_user_label` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_user_label` WRITE;
/*!40000 ALTER TABLE `money_user_label` DISABLE KEYS */;

INSERT INTO `money_user_label` (`id`, `name`, `sort`)
VALUES
	(1,'普通会员',1),
	(2,'银牌会员',NULL),
	(3,'金牌会员',1);

/*!40000 ALTER TABLE `money_user_label` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user_notice
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user_notice`;

CREATE TABLE `money_user_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT '0' COMMENT '用户ID',
  `title` varchar(50) NOT NULL COMMENT '标题',
  `content` varchar(200) NOT NULL COMMENT '内容',
  `times` int(11) NOT NULL COMMENT '时间',
  `is_read` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已读(1:已读0:未读)',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_user_notice` WRITE;
/*!40000 ALTER TABLE `money_user_notice` DISABLE KEYS */;

INSERT INTO `money_user_notice` (`id`, `user_id`, `title`, `content`, `times`, `is_read`)
VALUES
	(1,1,'玩游戏领红包','玩游戏领红包',1430112495,0),
	(2,2,'下载游戏奖励','下载游戏有奖励',1430112495,0),
	(3,1,'的','呵呵呵那大家开始登记卡登记卡四大皆空呵呵呵那大家开始登记卡登记卡四大皆空呵呵呵那大家开始登记卡登记卡四大皆空呵呵呵那大家开始登记卡登记卡四大皆空呵呵呵那大家开始登记卡登记卡四大皆空呵呵呵那大家开始登记卡登记卡四大皆空呵呵呵那大家开始登记卡登记卡四大皆空呵呵呵那大家开始登记卡登记卡四',1430792746,0),
	(4,1,'大大大滴滴答答','顶顶顶顶顶顶顶顶顶顶顶顶顶顶大大大',1430792924,0),
	(5,0,'地对地导弹','地对地导弹',1431666528,0),
	(6,0,'邀请好友最高奖励20积分','邀请好友最高奖励20积分',1431936436,0),
	(7,0,'下载熙敏楚格','下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏楚格下载熙敏',1431939634,0),
	(8,0,'点击','顶顶顶顶',1431939728,0),
	(9,0,'搜索','多大',1431939917,0),
	(10,0,'搜索 ','水水水水',1431939972,0),
	(11,0,'的','的',1431940017,0),
	(12,0,'的','顶顶顶顶',1431940084,0),
	(13,0,'搜索','搜索',1431940142,0);

/*!40000 ALTER TABLE `money_user_notice` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table money_user_task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `money_user_task`;

CREATE TABLE `money_user_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID(money_users)',
  `task_id` int(11) NOT NULL COMMENT '任务ID(money_app_task',
  `app_id` int(11) NOT NULL COMMENT 'APPID(money_app)',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '任务完成状态(1:未完成2:已完成3:已过期)',
  `finish_time` int(11) NOT NULL DEFAULT '0' COMMENT '完成时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `money_user_task` WRITE;
/*!40000 ALTER TABLE `money_user_task` DISABLE KEYS */;

INSERT INTO `money_user_task` (`id`, `user_id`, `task_id`, `app_id`, `status`, `finish_time`)
VALUES
	(44,1,3,1,2,1431938177),
	(45,16,3,16,2,1432177851),
	(46,1,15,12,2,1432177851);

/*!40000 ALTER TABLE `money_user_task` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

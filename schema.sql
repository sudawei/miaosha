/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50509
Source Host           : localhost:3306
Source Database       : test_mysql

Target Server Type    : MYSQL
Target Server Version : 50509
File Encoding         : 65001

Date: 2017-04-25 18:26:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `seckill`
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` varchar(120) NOT NULL COMMENT '商品名称',
  `number` int(11) NOT NULL COMMENT '库存数量',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '秒杀开启时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀结束时间',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- ----------------------------
-- Records of seckill
-- ----------------------------
INSERT INTO `seckill` VALUES ('1000', '1000元秒杀iphone6', '98', '2017-04-25 18:24:51', '2017-11-02 00:00:00', '2015-11-01 00:00:00');
INSERT INTO `seckill` VALUES ('1001', '500元秒杀ipad2', '200', '2017-04-25 18:22:56', '2015-11-02 00:00:00', '2015-11-01 00:00:00');
INSERT INTO `seckill` VALUES ('1002', '300元秒杀小米4', '300', '2017-04-25 18:22:56', '2015-11-02 00:00:00', '2015-11-01 00:00:00');
INSERT INTO `seckill` VALUES ('1003', '200元秒杀红米note', '400', '2017-04-25 18:22:56', '2015-11-02 00:00:00', '2015-11-01 00:00:00');

-- ----------------------------
-- Table structure for `success_killed`
-- ----------------------------
DROP TABLE IF EXISTS `success_killed`;
CREATE TABLE `success_killed` (
  `seckill_id` int(11) NOT NULL COMMENT '秒杀商品id',
  `user_phone` bigint(20) NOT NULL COMMENT '用户手机号',
  `state` int(1) NOT NULL DEFAULT '-1' COMMENT '状态标示,-1：无效,0：成功,1：无效，2：已发货',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- ----------------------------
-- Records of success_killed
-- ----------------------------
INSERT INTO `success_killed` VALUES ('1000', '13521658795', '1', '2017-04-25 18:24:08');
INSERT INTO `success_killed` VALUES ('1000', '13598565986', '1', '2017-04-25 18:24:51');

-- ----------------------------
-- Procedure structure for `execute_seckill`
-- ----------------------------
DROP PROCEDURE IF EXISTS `execute_seckill`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `execute_seckill`(in v_seckill_id bigint,in v_phone bigint,in v_kill_time TIMESTAMP ,out r_result int)
BEGIN

  DECLARE insert_count int DEFAULT 0;
    START TRANSACTION ;
    INSERT ignore INTO success_killed(seckill_id,user_phone,create_time,state)VALUES(v_seckill_id,v_phone,v_kill_time,1);

    SELECT ROW_COUNT() INTO insert_count;
    IF(insert_count = 0)THEN
     ROLLBACK ;
     SET r_result=-1;
    ELSEIF(insert_count<0)THEN
     ROLLBACK ;
     SET r_result=-2;

    ELSE
     UPDATE seckill set number = number-1 WHERE seckill_id = v_seckill_id and end_time > v_kill_time and start_time <v_kill_time
     AND  number>0;
    SELECT ROW_COUNT() INTO insert_count;
     IF(insert_count<=0)THEN
      ROLLBACK ;
      SET r_result = -2;
     ELSE
      COMMIT ;
      SET r_result = 1;
     END IF;
  END IF;
END
;;
DELIMITER ;
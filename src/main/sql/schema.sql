-- 数据库初始化脚本
-- 创建数据库
CREATE database seckill;
-- 使用数据库
use seckill;
-- 创建秒杀库存表
CREATE TABLE seckill(
'seckill_id' BIGINT NOT NULL auto_increment comment '商品库存ID',
'name' VARCHAR(120) not null comment '商品名称',
'number' INT NOT NULL comment '库存数量',
'start_time' TIMESTAMP NOT NULL comment '秒杀开启时间',
'end_time' TIMESTAMP NOT NULL comment '秒杀结束时间',
'create_time' TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
PRIMARY KEY (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)engine=InnoDB auto_increment=1000 DEFAULT charset=utf8 comment='秒杀库存表'

-- 初始化数据
insert into seckill(name,number,start_time,end_time)
VALUES
  ('1000元秒杀iphone6',100,'2016-01-01 00:00:00','2016-01-02 00:00:00'),
  ('800元秒杀ipad',200,'2016-01-01 00:00:00','2016-01-02 00:00:00'),
  ('6600元秒杀mac book pro',300,'2016-01-01 00:00:00','2016-01-02 00:00:00'),
  ('7000元秒杀iMac',400,'2016-01-01 00:00:00','2016-01-02 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关信息(简化为手机号)
CREATE TABLE success_killed(
  'seckill_id' BIGINT NOT NULL COMMENT '秒杀商品ID',
  'user_phone' BIGINT NOT NULL COMMENT '用户手机号',
  'state' TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
  'create_time' TIMESTAMP NOT NULL COMMENT '创建时间',
  PRIMARY KEY(seckill_id,user_phone),/*联合主键，强行让一个用户只能秒杀同一件产品，不能重复秒杀*/
  KEY idx_create_time(create_time)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- 连接数据库控制台
-- mysql -uroot -p


-- 建议手写数据库脚本
-- 用来记录每次上线的DDL修改
-- 如 V1.1
ALTER TABLE seckill
    DROP INDEX inx_create_time,
    ADD INDEX ...

-- 上线V1.2
-- DDL
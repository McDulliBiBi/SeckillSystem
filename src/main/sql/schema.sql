-- ���ݿ��ʼ���ű�
-- �������ݿ�
CREATE database seckill;
-- ʹ�����ݿ�
use seckill;
-- ������ɱ����
CREATE TABLE seckill(
'seckill_id' BIGINT NOT NULL auto_increment comment '��Ʒ���ID',
'name' VARCHAR(120) not null comment '��Ʒ����',
'number' INT NOT NULL comment '�������',
'start_time' TIMESTAMP NOT NULL comment '��ɱ����ʱ��',
'end_time' TIMESTAMP NOT NULL comment '��ɱ����ʱ��',
'create_time' TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '����ʱ��',
PRIMARY KEY (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)engine=InnoDB auto_increment=1000 DEFAULT charset=utf8 comment='��ɱ����'

-- ��ʼ������
insert into seckill(name,number,start_time,end_time)
VALUES
  ('1000Ԫ��ɱiphone6',100,'2016-01-01 00:00:00','2016-01-02 00:00:00'),
  ('800Ԫ��ɱipad',200,'2016-01-01 00:00:00','2016-01-02 00:00:00'),
  ('6600Ԫ��ɱmac book pro',300,'2016-01-01 00:00:00','2016-01-02 00:00:00'),
  ('7000Ԫ��ɱiMac',400,'2016-01-01 00:00:00','2016-01-02 00:00:00');

-- ��ɱ�ɹ���ϸ��
-- �û���¼��֤�����Ϣ(��Ϊ�ֻ���)
CREATE TABLE success_killed(
  'seckill_id' BIGINT NOT NULL COMMENT '��ɱ��ƷID',
  'user_phone' BIGINT NOT NULL COMMENT '�û��ֻ���',
  'state' TINYINT NOT NULL DEFAULT -1 COMMENT '״̬��ʶ:-1:��Ч 0:�ɹ� 1:�Ѹ��� 2:�ѷ���',
  'create_time' TIMESTAMP NOT NULL COMMENT '����ʱ��',
  PRIMARY KEY(seckill_id,user_phone),/*����������ǿ����һ���û�ֻ����ɱͬһ����Ʒ�������ظ���ɱ*/
  KEY idx_create_time(create_time)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='��ɱ�ɹ���ϸ��';

-- �������ݿ����̨
-- mysql -uroot -p


-- ������д���ݿ�ű�
-- ������¼ÿ�����ߵ�DDL�޸�
-- �� V1.1
ALTER TABLE seckill
    DROP INDEX inx_create_time,
    ADD INDEX ...

-- ����V1.2
-- DDL
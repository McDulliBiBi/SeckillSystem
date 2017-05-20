--秒杀执行的存储过程
DELIMITER $$; -- CONSOLE ; 转换为$$ 换行符
--定义存储过程
--参数 ：in:表示数据的参数   out:输出参数
--row_count() 返回上一条修改类型SQL的影响行数delete, insert, update
--row_count() :0 :未修改行  ; >0 表示修改的行数 ;  <0 修改错误/未执行
CREATE or REPLACE  PROCEDURE  `seckill`.`execute_seckill`
  (in v_seckill_id bigint,
  in v_phone bigint,
  in v_kill_time timestamp,
  out r_result bigint) as
  BEGIN
    DECLARE insert_count int DEFAULT 0;
    start TRANSACTION ;
    insert ignore into success_killed
      (seckill_id, user_phone, create_time )
    values (v_seckill_id, v_phone, v_kill_time);
    select ROW_COUNT() into insert_count;
    IF insert_count = 0 THEN
      ROLLBACK ;
      set r_result = -1;
    ELSEIF insert_count < 0 then
      ROLLBACK ;
      set r_result = -2;
    ELSE
      update seckill set number = number-1
      where seckill_id = v_seckill_id
        and end_time > v_kill_time
        and start_time < v_kill_time
        and number>0;
      select ROW_COUNT() into insert_count;
      if(insert_count = 0) THEN
        ROLLBACK ;
        set r_result = 0;
      elseif (insert_count < 0) THEN
        ROLLBACK ;
        set r_result = -2;
      else
        commit;
        set r_result = 1;
      end if;
    END if;
  end;
$$
--存储过程定义结束

--调用存储过程的步骤
--换行符改成分号
--DELIMITER ;
--设置变量
--set @r_result = 1;
--执行存储过程
--call execute_seckill(1, 13221212212, now(), @r_result);
--获取结果
--select @r_result;


--存储过程：
--1.存储过程优化：事务行级锁持有的时间尽可能短
--2.不要过度依赖存储过程。银行中多用存储过程
--3.简单的逻辑可以应用存储过程，
--4.QPS：一个秒杀单接近6000/qps

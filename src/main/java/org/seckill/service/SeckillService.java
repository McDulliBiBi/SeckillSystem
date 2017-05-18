package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：站在使用者的角度设计接口：
 * 三个方面考虑接口
 * 1.方法定义粒度（明确，关注执行秒杀，不要关注实现的细节）
 * 2.参数（越简练越好）
 * 3.返回类型/异常：友好，不要map/entity
 *
 * Created by Administrator on 2017/5/7.
 */
public interface SeckillService {
    /**
     * 查询所有秒杀产品接口
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀产品记录
     * @param seckillId
     * @return
     */
    Seckill getSeckillById(long seckillId);

    /**
     * 秒杀开启时，输出秒杀接口地址，
     * 否则输出系统时间个秒杀时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param phone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long phone, String md5);

    /**
     * 执行秒杀操作 by 存储过程
     * @param seckillId
     * @param phone
     * @param md5
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long phone, String md5);

}

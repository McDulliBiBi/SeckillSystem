package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * Created by Administrator on 2017/5/7.
 */
public interface SuccessKilledDao {
    /**
     *插入购买明细，可重复过滤
     * @param successKilledId
     * @param userPhone
     * @return 插入的行数
     */
    int insertSuccesskilled(@Param("successKilledId")long successKilledId, @Param("userPhone")long userPhone);

    /**
     * 根据ID查询SuccessKilled实体，并携带Seckill秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);

}

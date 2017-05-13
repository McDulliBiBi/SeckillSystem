package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * Created by Administrator on 2017/5/7.
 */
public interface SuccessKilledDao {
    /**
     *���빺����ϸ�����ظ�����
     * @param successKilledId
     * @param userPhone
     * @return ���������
     */
    int insertSuccesskilled(@Param("successKilledId")long successKilledId, @Param("userPhone")long userPhone);

    /**
     * ����ID��ѯSuccessKilledʵ�壬��Я��Seckill��ɱ��Ʒ����ʵ��
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);

}

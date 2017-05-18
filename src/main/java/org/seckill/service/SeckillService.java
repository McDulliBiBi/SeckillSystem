package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * ҵ��ӿڣ�վ��ʹ���ߵĽǶ���ƽӿڣ�
 * �������濼�ǽӿ�
 * 1.�����������ȣ���ȷ����עִ����ɱ����Ҫ��עʵ�ֵ�ϸ�ڣ�
 * 2.������Խ����Խ�ã�
 * 3.��������/�쳣���Ѻã���Ҫmap/entity
 *
 * Created by Administrator on 2017/5/7.
 */
public interface SeckillService {
    /**
     * ��ѯ������ɱ��Ʒ�ӿ�
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * ��ѯ������ɱ��Ʒ��¼
     * @param seckillId
     * @return
     */
    Seckill getSeckillById(long seckillId);

    /**
     * ��ɱ����ʱ�������ɱ�ӿڵ�ַ��
     * �������ϵͳʱ�����ɱʱ��
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * ִ����ɱ����
     * @param seckillId
     * @param phone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long phone, String md5);

    /**
     * ִ����ɱ���� by �洢����
     * @param seckillId
     * @param phone
     * @param md5
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long phone, String md5);

}

package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2017/5/7.
 */
public interface SeckillDao {
    /**
     *�����
     * @param seckillId
     * @param killTime
     * @return ����Ӱ���������
     *
     * ����ʹ����ע�� @Param ԭ����java�д��δ��Ķ����βΣ�����ָ�룬���Ե���������Ҫ�õ��������ʱ��
     * �������Ĳ������ֱ���arg0, arg1...�޷�ʶ�����Ӧ�Ĳ������ᱨ��
     * ����������ע��ķ�ʽ��ǿ�и��߽ӿ��еķ�����Ӧ�Ĳ���
     */
    //int reduceNumber(long seckillId, Date killTime);
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * ����successkilledId��ѯ��ɱ����
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * ����ƫ������ѯ��ɱ��Ʒ�б�   ����
     * ƫ��������
     * @param offset   ƫ����
     * @param limit  ��ƫ����֮��Ҫȡ��������¼
     * @return
     */
    //List<Seckill> queryAll(int offset, int limit);
    List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit);

    void killByProcedure(Map<String, Object> map);
}

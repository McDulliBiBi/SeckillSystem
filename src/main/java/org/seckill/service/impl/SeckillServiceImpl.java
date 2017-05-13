package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/11.
 */

//@component:ͳ��spring�������ע�⡣�������@Service   @Dao    @Controller
@Service
public class SeckillServiceImpl implements SeckillService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //ע��Serivicey����������Ҫ�Լ�newһ��ʵ��
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    //��������MD5
    private final String salt = "GHGFTIHyfyfuyb56790";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getSeckillById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);

        if(seckill == null){
            //û�иò�Ʒ����ɱ�
            return new Exposer(false,seckillId);
        }else{
            Date startTime = seckill.getStartTime();
            Date endTime = seckill.getEndTime();
            Date now = new Date();
            if( startTime.getTime() > now.getTime() || endTime.getTime() <now.getTime()){
                //��ɱδ����������ɱ�ѽ���
                return new Exposer(false, seckillId, now.getTime(), startTime.getTime(), endTime.getTime());
            }else{
                //��ɱ�ѿ���
                //����MD5
                String md5 = getMD5(seckillId);
                return new Exposer(true, md5 , seckillId);

            }
        }
    }

    //����MD5У�����ַ���
    private String getMD5( long seckillId ){
        String base = seckillId+"/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    @Transactional
    /**
     * ʹ��ע��������񷽷����ŵ㣺
     * 1.ʹ�����ŶӴ��һ�£���ȷ��ע���񷽷��ı�̷��
     * 2.��֤���񷽷���ִ��ʱ�価���̣ܶ���Ҫ��������������������绺��memcache��RPC/HTTP������Щ����ʱ��Ƚϳ������뼶�ġ�
     * ���߰��뵽���񷽷����������һ�����ϲ�Ĳ������׳�����ʱ�쳣������roll back
     * 3.�������еķ�������Ҫ������ֻ��һ���޸Ĳ�������ֻ������������Ҫ�����м�������ĵ���
     */
    public SeckillExecution executeSeckill(long seckillId, long phone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException{
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        //ִ����ɱ�߼��������+��¼��ɱ��Ϊ
        int updateCount = seckillDao.reduceNumber(seckillId, new Date());
        if(updateCount <= 0){
            //û�и��µ����ݿ��¼
            throw new SeckillCloseException("seckill is close");
        } else {
            try {
            //�����ɹ������¼������Ϊ
                int insertCount = successKilledDao.insertSuccesskilled(seckillId, phone);
                //Ψһ��seckillId, phone
                if( insertCount <=0){
                    //���ʾ�ظ���ɱ
                    throw new RepeatKillException("seckill repeat");
                }else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, phone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled); //�����������ֵ䣬ö������ʾ
                }
            }catch (SeckillCloseException e){
                throw e;
            }catch (RepeatKillException e){
                throw e;
            }catch (Exception e){
                logger.error(e.getMessage(), e);
                //���б������쳣 ת��Ϊ�������쳣��spring��⵽���ع�
                throw new SeckillException("seckill inner error:" + e.getMessage());
            }

        }

    }
}

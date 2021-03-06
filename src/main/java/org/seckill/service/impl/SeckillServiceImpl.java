package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
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

import java.util.*;

/**
 * Created by Administrator on 2017/5/11.
 */

//@component:统称spring中组件的注解。具体的有@Service   @Dao    @Controller
@Service
public class SeckillServiceImpl implements SeckillService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入Serivicey依赖，不需要自己new一个实例
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //用于生成MD5
    private final String salt = "GHGFTIHyfyfuyb56790";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getSeckillById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //缓存优化 从redis缓存中取，超时的基础上维护一致性
        //原：从mysql取的  Seckill seckill = seckillDao.queryById(seckillId);
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null){
            //redis缓存中不存在，访问数据库
            seckill = seckillDao.queryById(seckillId);
            if(seckill == null){
                //秒杀产品不存在
                return new Exposer(false, seckillId);
            }else{
                //秒杀产品存在，放入redis缓存中
                redisDao.setSeckill(seckill);
            }
        }

        if(seckill == null){
            //没有该产品的秒杀活动
            return new Exposer(false,seckillId);
        }else{
            Date startTime = seckill.getStartTime();
            Date endTime = seckill.getEndTime();
            Date now = new Date();
            if( startTime.getTime() > now.getTime() || endTime.getTime() <now.getTime()){
                //秒杀未开启，或秒杀已结束
                return new Exposer(false, seckillId, now.getTime(), startTime.getTime(), endTime.getTime());
            }else{
                //秒杀已开启
                //生成MD5
                String md5 = getMD5(seckillId);
                return new Exposer(true, md5 , seckillId);

            }
        }
    }

    //生成MD5校验后的字符串
    private String getMD5( long seckillId ){
        String base = seckillId+"/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    @Transactional
    /**
     * 使用注解控制事务方法的优点：
     * 1.使开发团队达成一致，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他的网络操作，如缓存memcache，RPC/HTTP请求，这些请求时间比较长，毫秒级的。
     * 或者剥离到事务方法外操作，做一个更上层的操作。抛出运行时异常，立即roll back
     * 3.不是所有的方法都需要事务，如只有一条修改操作，或只读操作，不需要事务。行级锁相关文档。
     */
    public SeckillExecution executeSeckill(long seckillId, long phone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException{
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        //为提高并发性，先insert 秒杀记录，后update产品的个数
        try {

            //减库存成功，则记录购买行为
            int insertCount = successKilledDao.insertSuccesskilled(seckillId, phone);
            //唯一的seckillId, phone
            if (insertCount <= 0) {
                //则表示重复秒杀
                throw new RepeatKillException("seckill repeat");
            } else {
                //减库存,热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, new Date());
                if (updateCount <= 0) {
                    //没有更新到数据库记录
                    throw new SeckillCloseException("seckill is close");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, phone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled); //可以用数据字典，枚举来表示
                }
            }
        } catch (SeckillCloseException e){
                throw e;
            }catch (RepeatKillException e){
                throw e;
            }catch (Exception e){
                logger.error(e.getMessage(), e);
                //所有编译期异常 转化为运行期异常。spring监测到后会回滚
                throw new SeckillException("seckill inner error:" + e.getMessage());
            }
        }

    /**
     * 执行秒杀操作 by 存储过程
     * @param seckillId
     * @param phone
     * @param md5
     */
    public SeckillExecution executeSeckillProcedure(long seckillId, long phone, String md5) {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", phone);
        map.put("killTime", killTime);
        map.put("result", null);
        //存过执行完，result被赋值
        try{
            seckillDao.killByProcedure(map);
            int result = MapUtils.getInteger(map, "result", -2);
            if(result == 1){
                SuccessKilled s = successKilledDao.queryByIdWithSeckill(seckillId, phone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS);
            }else{
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            throw  new SeckillException("seckill inner error: " + e.getMessage());
        }
    }
}

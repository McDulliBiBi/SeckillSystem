package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Administrator on 2017/5/17.
 */
public class RedisDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private JedisPool jedisPool;  //和数据库连接池类似？也有一个池pool

    public RedisDao(String ip, int port){
        jedisPool = new JedisPool(ip, port);
    }


    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
    public Seckill getSeckill(long seckillId){
        //redis操作逻辑。
        //缓存的逻辑不应该放在service中，而应该放在DAO层，DAO层不仅仅是与数据库的操作
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String  key = "seckill:" + seckillId;
                //redis没有实现内部序列化操作
                //从redis或memcache获取二进制数据 --》 反序列化，获得object
                //采用自定义序列化，github上
                //protostuff :必须是一个对象，不能是int ,string..
                //使用protostuff比jdk原生的序列化可以节省空间十分之一~五分之一
                byte[] bytes = jedis.get(key.getBytes());
                if(bytes != null){
                    //缓存中能获取到该对象
                    Seckill seckill = schema.newMessage(); //先获得一个空对象
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema); //seckill空对象被反序列
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String setSeckill(Seckill seckill){
        //把seckill对象传递到redis中
        //object seckill -->byte --> 发送给redis,其实就是一个序列化的过程
        try{
            Jedis jedis = jedisPool.getResource();
            try{
                String  key = "seckill:" + seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout = 60* 60; //1个小时
                String result = jedis.setex(key.getBytes() , timeout, bytes);
                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }

        return null;
    }

}

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
     *减库存
     * @param seckillId
     * @param killTime
     * @return 返回影响的行数。
     *
     * 这里使用了注解 @Param 原因是java中传参传的都是形参，不是指针，所以当方法中需要用到多个参数时，
     * 传过来的参数名分别是arg0, arg1...无法识别出对应的参数，会报错
     * 所以这里用注解的方式，强行告诉接口中的方法对应的参数
     */
    //int reduceNumber(long seckillId, Date killTime);
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据successkilledId查询秒杀对象
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表   ？？
     * 偏移量？？
     * @param offset   偏移量
     * @param limit  在偏移量之后要取多少条记录
     * @return
     */
    //List<Seckill> queryAll(int offset, int limit);
    List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit);

    void killByProcedure(Map<String, Object> map);
}

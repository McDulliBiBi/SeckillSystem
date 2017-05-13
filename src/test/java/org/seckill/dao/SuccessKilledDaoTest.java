package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/5/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    //注入DAO实现类依赖
    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccesskilled() throws Exception {
        long id = 2L;
        long phone = 13245367456L;
        int insertCount = successKilledDao.insertSuccesskilled(id, phone);
        System.out.println("insertCount = " + insertCount);
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {
        long id = 2L;
        long phone = 13245367456L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}
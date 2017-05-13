package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/5/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}", list);
    }

    @Test
    public void testGetSeckillById() throws Exception {

        long id = 1;
        Seckill seckill = seckillService.getSeckillById(id);
        logger.info("seckill={}", seckill);

    }

    //测试代码完整逻辑，可重复执行
    @Test
    public void testExportSeckillUrl() throws Exception {
        long id = 1;
        long phone = 12345678954L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.getExposed()){
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone,exposer.getMd5());
                logger.info("mcdull={}", seckillExecution);
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }
        }else {
            logger.info("seckill is not start!");
        }
        logger.info("exposer={}", exposer);

    }

    @Test
    public void testExecuteSeckill() throws Exception {
        long id = 1;
        long phone = 12345678906L;
        String md5="caecc54327c66f3c9ddb30599709e7ae";
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone,md5);
            logger.info("mcdull={}", seckillExecution);
        }catch (SeckillCloseException e){
            logger.error(e.getMessage());
        }catch (RepeatKillException e){
            logger.error(e.getMessage());
        }
    }
}
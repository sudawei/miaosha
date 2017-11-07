package com.suwei.miaosha.Service;

import com.github.pagehelper.PageInfo;
import com.suwei.miaosha.Common.ServerResponse;
import com.suwei.miaosha.Entity.Seckill;
import com.suwei.miaosha.Entity.SuccesskilledKey;
import com.suwei.miaosha.Exception.RepeatKillException;
import com.suwei.miaosha.Exception.SeckillCloseException;
import com.suwei.miaosha.Exception.SeckillException;
import com.suwei.miaosha.Vo.Exposer;
import com.suwei.miaosha.Vo.SeckillExecution;
import org.springframework.stereotype.Service;

/**
 * @Auhter : suwei
 * @Descprition : 秒杀接口， 站在使用者的角度设计接口：
 *                  1，方法定义的粒度；
 *                  2，参数
 *                  3，返回类型
 * @Date : Created in 14:49
 * @Modified By :
 */

public interface SeckillService {

    /**
     * 查询全部秒杀的商品
     * @return
     */
    ServerResponse<PageInfo> getSeckillList(int pageNum, int pageSize );

    /**
     * 查询单个秒杀商品
     * @param seckillId
     * @return
     */
    ServerResponse<Seckill> getSeckillById(Long seckillId);

    /**
     * 暴露秒杀的接口地址：秒杀开启时输出接口地址，否则输出系统时间和秒杀时间
     * @param seckillId
     */
    ServerResponse<Exposer> exportSeckillUrl(Long seckillId);

    /**
     * 执行秒杀操作
     * @param successkilledKey
     * @param md5
     */
    ServerResponse<SeckillExecution> executeSeckill(SuccesskilledKey successkilledKey, String md5)
            throws SeckillException,RepeatKillException,SeckillCloseException;

    /**
     * 执行秒杀操作by 存储过程
     *
     * @param successkilledKey
     * @param md5
     */
    ServerResponse<SeckillExecution> executeSeckillProcedure(SuccesskilledKey successkilledKey, String md5);

    /**
     * 检验是否重复秒杀
     * @param successkilledKey
     * @return
     */
    ServerResponse checkSuccessSeckilled(SuccesskilledKey successkilledKey);
}

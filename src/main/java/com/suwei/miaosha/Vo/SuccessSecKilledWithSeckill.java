package com.suwei.miaosha.Vo;

import com.suwei.miaosha.Entity.Seckill;
import com.suwei.miaosha.Entity.Successkilled;

/**
 * @Auhter : suwei
 * @Descprition : 根据id查询SuccessSecKilled，并且组合对应的Seckill对象
 * @Date : Created in 14:30
 * @Modified By :
 */
public class SuccessSecKilledWithSeckill extends Successkilled {
    private Seckill seckill;

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }
}

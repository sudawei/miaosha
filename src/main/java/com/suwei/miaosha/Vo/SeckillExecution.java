package com.suwei.miaosha.Vo;

/**
 * @Auhter : suwei
 * @Descprition : 封装秒杀执行结果
 * @Date : Created in 15:10 2017\11\5 0005
 * @Modified By :
 */
public class SeckillExecution {

    private Long seckillId;

    //执行秒杀结果的状态
    private int stata;

    //秒杀结果的提示
    private String stateInfo;

    //秒杀成功之后返回的对象
    private SuccessSecKilledWithSeckill successSecKilledWithSeckill;

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public int getStata() {
        return stata;
    }

    public void setStata(int stata) {
        this.stata = stata;
    }

    public SuccessSecKilledWithSeckill getSuccessSecKilledWithSeckill() {
        return successSecKilledWithSeckill;
    }

    public void setSuccessSecKilledWithSeckill(SuccessSecKilledWithSeckill successSecKilledWithSeckill) {
        this.successSecKilledWithSeckill = successSecKilledWithSeckill;
    }

    public SeckillExecution(Long seckillId, int stata, String stateInfo) {
        this.seckillId = seckillId;
        this.stata = stata;
        this.stateInfo = stateInfo;
    }

    public SeckillExecution(Long seckillId, int stata, String stateInfo, SuccessSecKilledWithSeckill successSecKilledWithSeckill) {
        this.seckillId = seckillId;
        this.stata = stata;
        this.stateInfo = stateInfo;
        this.successSecKilledWithSeckill = successSecKilledWithSeckill;
    }
}

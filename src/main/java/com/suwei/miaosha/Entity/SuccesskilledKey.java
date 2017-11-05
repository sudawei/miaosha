package com.suwei.miaosha.Entity;

public class SuccesskilledKey {
    private Long seckillId;

    private Long userPhone;

    public SuccesskilledKey(Long seckillId, Long userPhone) {
        this.seckillId = seckillId;
        this.userPhone = userPhone;
    }

    public SuccesskilledKey() {
        super();
    }

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public Long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }
}
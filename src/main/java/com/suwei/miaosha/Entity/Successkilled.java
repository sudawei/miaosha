package com.suwei.miaosha.Entity;

import java.util.Date;

public class Successkilled extends SuccesskilledKey {
    private Integer state;

    private Date createTime;

    public Successkilled(Long seckillId, Long userPhone, Integer state, Date createTime) {
        super(seckillId, userPhone);
        this.state = state;
        this.createTime = createTime;
    }

    public Successkilled() {
        super();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
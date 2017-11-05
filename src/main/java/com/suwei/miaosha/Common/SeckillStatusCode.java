package com.suwei.miaosha.Common;

/**
 * 返回对象的状态码
 * @author suwei
 * @date 2017/10/16/016.
 */
public enum SeckillStatusCode {

    SUCCESS(0,"秒杀成功"),

    ERROR(1,"秒杀失败");

    private final int code;

    private final String desc;

    SeckillStatusCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

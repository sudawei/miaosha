package com.suwei.miaosha.Exception;

/**
 * @Auhter : suwei
 * @Descprition : 秒杀相关的业务异常
 * @Date : Created in 15:21 2017\11\5 0005
 * @Modified By :
 */
public class SeckillException extends RuntimeException{

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}

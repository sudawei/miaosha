package com.suwei.miaosha.Exception;

/**
 * @Auhter : suwei
 * @Descprition : 秒杀关闭异常
 * @Date : Created in 15:20 2017\11\5 0005
 * @Modified By :
 */
public class SeckillCloseException extends SeckillException{

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}

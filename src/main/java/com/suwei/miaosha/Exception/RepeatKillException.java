package com.suwei.miaosha.Exception;

/**
 * @Auhter : suwei
 * @Descprition : 重复秒杀的异常（运行期异常）
 * @Date : Created in 15:17 2017\11\5 0005
 * @Modified By :
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}

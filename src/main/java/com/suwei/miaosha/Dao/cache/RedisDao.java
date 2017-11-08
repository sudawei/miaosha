package com.suwei.miaosha.Dao.cache;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.suwei.miaosha.Entity.Seckill;
import com.suwei.miaosha.Entity.Successkilled;
import com.suwei.miaosha.Entity.SuccesskilledKey;
import com.suwei.miaosha.Redis.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * @Auhter : suwei
 * @Descprition :
 * @Date : Created in 10:29 2017\11\7 0007
 * @Modified By :
 */
@Repository
public class RedisDao {

    public static final String ID_PREFIXX = "seckillId_";
    public static final String SUCCESS_PREFIXX = "successkilled_";

    @Autowired
    private Cache cache;

    /**
     * 根据seckillId取出对应的Seckill
     * @param seckillId
     * @return
     */
    public Seckill getSeckill(Long seckillId){
       return (Seckill) cache.opsForValueGet(ID_PREFIXX +seckillId);
    }

    /**
     * 根据seckillId设置对应的Seckill,过期时间为1个小时
     * @param seckillId
     * @return
     */
    public void setSeckill(Long seckillId,Seckill seckill){
        cache.opsForValueSet(ID_PREFIXX +seckillId,seckill,1, TimeUnit.HOURS);
    }

    /**
     * 根据联合主键查询Successkilled
     * @param
     * @return
     */
    public Successkilled getSuccesskilled(SuccesskilledKey successkilledKey){
        String key =SUCCESS_PREFIXX + successkilledKey.getSeckillId() + "_" +successkilledKey.getUserPhone();
        return (Successkilled)cache.opsForValueGet(key);
    }

    /**
     * 根据联合主键设置Successkilled
     * @param
     * @return
     */
    public void setSuccesskilled(SuccesskilledKey successkilledKey,Successkilled successkilled){
        String key =SUCCESS_PREFIXX + successkilledKey.getSeckillId() + "_" +successkilledKey.getUserPhone();
        cache.opsForValueSet(key,successkilled,1,TimeUnit.DAYS);
    }



}
